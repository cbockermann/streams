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
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.ProcessorException;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.DataFactory;
import stream.io.sql.HsqlDialect;
import stream.io.sql.MysqlDialect;
import stream.util.KeyFilter;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Output")
public class SQLWriter extends AbstractSQLProcessor {

	static Logger log = LoggerFactory.getLogger(SQLWriter.class);
	boolean dropTable = false;
	String table;
	String[] keys;

	final LinkedHashSet<String> keysToStore = new LinkedHashSet<String>();
	Map<String, Class<?>> tableSchema = null;
	transient boolean tableExists = false;
	transient long count = 0L;
	transient Connection connection = null;
	transient List<String> columns = new ArrayList<String>();

	/**
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table
	 *            the table to set
	 */
	@Parameter(required = true, description = "The database table to insert items into.")
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * @return the keys
	 */
	public String[] getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	@Parameter(required = false, description = "A list of attributes to insert (columns), empty string for all attributes.")
	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	/**
	 * @return the dropTable
	 */
	public boolean isDropTable() {
		return dropTable;
	}

	/**
	 * @param dropTable
	 *            the dropTable to set
	 */
	@Parameter(required = false, defaultValue = "false")
	public void setDropTable(boolean dropTable) {
		this.dropTable = dropTable;
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);

		if (table == null || table.trim().equals(""))
			throw new Exception("No 'table' attribute provided!");

		init();
		log.debug("init(ProcessContext) done.");
	}

	private void init() throws Exception {

		connection = openConnection();
		log.debug("Opened connection to {} = {}", getUrl(), connection);
		log.debug("Dialect = {} ", dialect);

		if (url.toLowerCase().startsWith("jdbc:mysql")) {
			dialect = new MysqlDialect();
		}

		if (url.toLowerCase().startsWith("jdbc:hsqldb"))
			dialect = new HsqlDialect();

		log.debug("Using dialect {}", dialect);

		if (dropTable) {
			log.debug("Dropping existing table '{}'", getTable());

			try {
				Statement stmt = connection.createStatement();
				int ret = stmt.executeUpdate("DROP TABLE " + getTable());
				log.debug("Return of DROP TABLE: {}", ret);
			} catch (Exception e) {
				log.error("Failed to drop table: {}", e.getMessage());
			}
		} else {
			Map<String, Class<?>> schema = dialect.getTableSchema(connection,
					getTable());
			if (schema != null) {
				log.debug("Using existing table schema: {}", schema);

				log.info("Existing schema is: {}", schema);
				if (tableSchema == null)
					tableSchema = new LinkedHashMap<String, Class<?>>(schema);
				else
					tableSchema.putAll(schema);

				if (keys != null) {
					for (String key : keys) {
						if (!tableSchema.containsKey(key)) {
							log.info("Removing non-selected key '{}'", key);
							tableSchema.remove(key);
						}
					}
				}

				log.debug("Types:\n{}", tableSchema);
			} else {
				// throw new Exception("Cannot determine table-schema!");
			}
		}

	}

	public boolean hasTable(String name) {
		if (tableExists)
			return true;

		tableExists = super.hasTable(name);
		return tableExists;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (connection == null) {
			try {
				init();
			} catch (Exception e) {
				throw new ProcessorException(this,
						"Failed to initialize database connection: "
								+ e.getMessage());
			}
		}

		if (tableSchema == null) {
			log.debug("No table-schema found, does table exist? {}",
					this.hasTable(getTable()));

			tableSchema = dialect.getTableSchema(connection, getTable());
			log.debug("Tried to read schema from database: {}", tableSchema);

			if (tableSchema == null) {
				log.debug("Creating new table {} from first item {}",
						getTable(), input);
				Data sample = DataFactory.create();

				Set<String> ks = KeyFilter.select(input, keys);
				for (String k : ks) {
					sample.put(k, input.get(k));
				}

				Map<String, Class<?>> schema = dialect.getColumnTypes(sample);
				if (createTable(getTable(), schema)) {
					tableSchema = schema;
				} else {
					throw new ProcessorException(this,
							"Failed to create table " + getTable()
									+ " for item: " + input);
				}
			}
		}

		if (!hasTable(getTable())) {

			if (keys != null) {
				for (String key : keys) {
					Serializable value = input.get(key);
					if (value == null)
						throw new ProcessorException(
								this,
								"Cannot determine type of key '"
										+ key
										+ "' for table creation! First item does not provide a value for '"
										+ key + "'!");

					tableSchema.put(key, value.getClass());
				}
			} else {
				for (String key : input.keySet()) {
					tableSchema.put(key, input.get(key).getClass());
				}
			}

			if (!this.createTable(getTable(), tableSchema)) {
				throw new ProcessorException(this, "Failed to create table '"
						+ getTable() + "'!");
			} else {
				this.tableExists = true;
			}
		}

		try {
			StringBuffer insert = new StringBuffer("INSERT INTO ");
			insert.append(getTable());
			insert.append(" ( ");

			StringBuffer values = new StringBuffer(" VALUES ( ");

			List<Serializable> valueObject = new ArrayList<Serializable>();

			Iterator<String> it = tableSchema.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				Serializable value = input.get(key);
				if (value != null) {
					valueObject.add(value);
					insert.append(dialect.mapColumnName(key));
					values.append("?");
					if (it.hasNext()) {
						insert.append(", ");
						values.append(", ");
					}
				}
			}

			insert.append(" ) ");
			values.append(" ) ");
			insert.append(values.toString());

			log.debug("INSERT statement is: {}", insert);
			PreparedStatement ps = connection.prepareStatement(insert
					.toString());
			for (int i = 0; i < valueObject.size(); i++) {
				ps.setObject(i + 1, valueObject.get(i));
			}

			int ret = ps.executeUpdate();
			if (ret == 1)
				count++;
			log.debug("INSERT retured {}", ret);
			ps.close();

		} catch (Exception e) {
			log.error("Failed to insert data item: {}", e.getMessage());
		}

		return input;
	}

	/**
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();
		log.debug("Closing SQL writer, {} items written.", count);
		log.debug("Closing SQL connection...");
		connection.close();
		this.tableSchema = null;
	}
}