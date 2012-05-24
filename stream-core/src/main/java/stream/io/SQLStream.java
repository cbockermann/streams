/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.io;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.data.DataFactory;
import stream.io.sql.DatabaseDialect;
import stream.io.sql.HsqlDialect;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Sources")
public class SQLStream extends AbstractDataStream {

	String url;
	String driver;
	String select;
	Connection connection;
	ResultSet result;
	String[] columns = null;
	DatabaseDialect dialect = new HsqlDialect();

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	@Parameter(required = true, description = "The JDBC database url to connect to.")
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the select
	 */
	public String getSelect() {
		return select;
	}

	/**
	 * @param select
	 *            the select to set
	 */
	@Parameter(required = true, description = "The select statement to select items from the database.")
	public void setSelect(String select) {
		this.select = select;
	}

	/**
	 * @see stream.io.AbstractDataStream#readHeader()
	 */
	@Override
	public void readHeader() throws Exception {
	}

	/**
	 * @see stream.io.AbstractDataStream#init()
	 */
	@Override
	public void init() throws Exception {
		super.init();

		try {

			log.info("Opening connection to database {}", getUrl());
			connection = DriverManager.getConnection(getUrl(), getUsername(),
					getPassword());

			PreparedStatement stmt = connection.prepareStatement(select);
			result = stmt.executeQuery();

		} catch (Exception e) {
			log.error("Failed to connect to database: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @see stream.io.AbstractDataStream#readItem(stream.data.Data)
	 */
	@Override
	public Data readItem(Data instance) throws Exception {

		if (instance == null)
			instance = DataFactory.create();

		if (result != null && result.next()) {

			if (columns == null) {
				ResultSetMetaData meta = result.getMetaData();
				columns = new String[meta.getColumnCount()];
				for (int i = 0; i < columns.length; i++) {
					columns[i] = meta.getColumnName(i + 1);
				}
			}

			for (String col : columns) {
				Object object = result.getObject(col);
				String key = dialect.unmapColumnName(col);
				instance.put(key, (Serializable) object);
			}

			return instance;
		}

		return null;
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
		try {
			if (result != null)
				result.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
