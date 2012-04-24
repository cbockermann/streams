/**
 * 
 */
package stream.io;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.ProcessContext;
import stream.ProcessorException;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.io.sql.DatabaseDialect;
import stream.io.sql.HsqlDialect;
import stream.io.sql.MysqlDialect;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Output")
public class SQLWriter extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(SQLWriter.class);
	boolean dropTable = false;
	String url;
	String username;
	String password;
	String table;
	String[] keys;

	final LinkedHashSet<String> keysToStore = new LinkedHashSet<String>();
	final Map<String, Class<?>> types = new LinkedHashMap<String, Class<?>>();
	transient boolean tableExists = false;
	transient long count = 0L;
	transient Connection connection = null;
	transient List<String> columns = new ArrayList<String>();
	DatabaseDialect dialect = new MysqlDialect();

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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	@Parameter(required = false, description = "The username used to connect to the database.")
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
	@Parameter(required = false, description = "The password used to connect to the database.")
	public void setPassword(String password) {
		this.password = password;
	}

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
		connection = DriverManager.getConnection(getUrl(), getUsername(),
				getPassword());
		log.debug("Opened connection to {}", getUrl());

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

				if (keys == null) {
					log.debug("No keys defined, adding all columns...");
					for (String col : schema.keySet()) {
						types.put(dialect.unmapColumnName(col), types.get(col));
					}
				} else {
					for (String key : keys) {
						String col = dialect.mapColumnName(key);
						if (schema.containsKey(col)) {
							log.debug("Adding key {} (column {})", key, col);
							types.put(key, schema.get(col));
						} else {
							log.debug(
									"key '{}' (column {}) is not within specified keys and will not be stored.",
									key, col);
						}
					}
				}

				log.debug("Types:\n{}", types);
			} else {
				throw new Exception("Cannot determine table-schema!");
			}
		}
	}

	public boolean createTable(String name, Map<String, Class<?>> types) {

		String create = dialect.getCreateTableCommand(name, types);
		log.debug("Trying to create table '{}':\n{}", name, create);

		try {
			Statement stmt = connection.createStatement();
			log.debug("Running create: {}", create);
			int rc = stmt.executeUpdate(create);
			log.debug("create returned: {}", rc);
			stmt.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean hasTable(String name) {
		Statement stmt = null;
		if (tableExists)
			return true;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + name);
			if (rs.next()) {
				tableExists = true;
			}
			rs.close();
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}
		return tableExists;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
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

		if (types.isEmpty()) {

			Map<String, Class<?>> schema = dialect.getColumnTypes(input);
			if (createTable(getTable(), schema)) {
				this.types.putAll(schema);
			} else {
				throw new ProcessorException(this, "Failed to create table "
						+ getTable() + " for item: " + input);
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

					types.put(key, value.getClass());
				}
			} else {
				for (String key : input.keySet()) {
					types.put(key, input.get(key).getClass());
				}
			}

			if (!this.createTable(getTable(), types)) {
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

			Iterator<String> it = types.keySet().iterator();
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
	}

}