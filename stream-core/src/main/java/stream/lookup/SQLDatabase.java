/**
 * 
 */
package stream.lookup;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Context;
import stream.Data;
import stream.annotations.Parameter;
import stream.data.DataFactory;
import stream.io.SourceURL;
import stream.io.sql.DatabaseDialect;
import stream.io.sql.HsqlDialect;
import stream.runtime.LifeCycle;
import stream.service.LookupService;

/**
 * @author chris
 * 
 */
public class SQLDatabase implements LookupService, LifeCycle {

	static Logger log = LoggerFactory.getLogger(SQLDatabase.class);

	@Parameter(description = "The database URL. This is expected to be JDBC compatible URL.")
	SourceURL url;

	@Parameter(description = "The user to connect to the database.")
	String username = "";

	@Parameter(description = "The password for connecting to the database.")
	String password = "";

	@Parameter(description = "The SQL query to call for the lookup. The '?' character is used as placeholder for the ID value.")
	String select;

	String[] columns;
	DatabaseDialect dialect = new HsqlDialect();

	transient Connection connection;
	transient PreparedStatement statement;

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
	}

	/**
	 * @see stream.runtime.LifeCycle#init(stream.Context)
	 */
	@Override
	public void init(Context context) throws Exception {
		try {

			log.info("Opening connection to database {}", getUrl());

			SourceURL url = this.getUrl();
			connection = DriverManager.getConnection(url.toString(),
					getUsername(), getPassword());

			statement = connection.prepareStatement(select);

		} catch (Exception e) {
			log.error("Failed to connect to database: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @see stream.runtime.LifeCycle#finish()
	 */
	@Override
	public void finish() throws Exception {

		connection.close();
	}

	/**
	 * @see stream.service.LookupService#lookup(java.lang.String)
	 */
	@Override
	public Data lookup(String key) {
		Data item = null;

		try {
			log.debug("Calling statement for key '{}'", key);
			statement.setString(1, key);

			ResultSet rs = statement.executeQuery();
			if (rs.next()) {

				item = DataFactory.create();

				if (columns == null) {
					ResultSetMetaData meta = rs.getMetaData();
					columns = new String[meta.getColumnCount()];
					for (int i = 0; i < columns.length; i++) {
						columns[i] = meta.getColumnName(i + 1);
					}
				}

				for (String col : columns) {
					Object object = rs.getObject(col);
					String k = dialect.unmapColumnName(col);
					item.put(k, (Serializable) object);
				}

			} else {
				log.debug("No result for lookup of key '{}'", key);
			}
			rs.close();

		} catch (Exception e) {
			log.error("Failed to run select for lookup('{}'): {}", key,
					e.getMessage());
			e.printStackTrace();
		}

		return item;
	}

	/**
	 * @return the url
	 */
	public SourceURL getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(SourceURL url) {
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
}
