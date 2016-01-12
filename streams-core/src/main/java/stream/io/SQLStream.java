/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
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

import stream.Data;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.DataFactory;
import stream.io.sql.DatabaseDialect;
import stream.io.sql.HsqlDialect;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Sources")
public class SQLStream extends AbstractStream {

	String username;
	String password;
	String driver;
	String select;
	Connection connection;
	ResultSet result;
	String[] columns = null;
	DatabaseDialect dialect = new HsqlDialect();

	public SQLStream(SourceURL url) {
		super(url);
	}

	public SQLStream() {
		super((SourceURL) null);
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @see stream.io.AbstractStream#init()
	 */
	@Override
	public void init() throws Exception {
		super.init();

		try {

			log.info("Opening connection to database {}", getUrl());

			SourceURL url = this.getUrl();
			connection = DriverManager.getConnection(url.toString(),
					getUsername(), getPassword());

			PreparedStatement stmt = connection.prepareStatement(select);
			result = stmt.executeQuery();

		} catch (Exception e) {
			log.error("Failed to connect to database: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @see stream.io.AbstractStream#readItem(stream.Data)
	 */
	@Override
	public Data readNext() throws Exception {

		Data instance = DataFactory.create();

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
	 * @see stream.io.Stream#close()
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
