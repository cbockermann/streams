/**
 * 
 */
package stream.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.annotations.Parameter;
import stream.io.sql.DatabaseDialect;
import stream.io.sql.HsqlDialect;
import stream.io.sql.MysqlDialect;

/**
 * @author chris
 * 
 */
public abstract class AbstractSQLProcessor extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(AbstractSQLProcessor.class);
	protected String url;
	protected String username;
	protected String password;
	protected DatabaseDialect dialect = new MysqlDialect();

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

		if (url.toLowerCase().startsWith("jdbc:mysql")) {
			dialect = new MysqlDialect();
		}

		if (url.toLowerCase().startsWith("jdbc:hsqldb"))
			dialect = new HsqlDialect();
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

	protected Connection openConnection() {
		try {
			log.debug("Opening database connection to {}", url);
			Connection con = DriverManager.getConnection(url, username,
					password);
			log.debug("   connection is: {}", con);
			return con;
		} catch (Exception e) {
			log.error("Failed to open database connection: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
			return null;
		}
	}

	protected void close(Connection con) {
		try {
			if (con != null)
				con.close();
		} catch (Exception e) {
			log.error("Failed to close database connection: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}

	protected boolean createTable(String name, Map<String, Class<?>> types) {

		String create = dialect.getCreateTableCommand(name, types);
		log.debug("Trying to create table '{}':\n{}", name, create);

		try {
			Connection con = openConnection();
			Statement stmt = con.createStatement();
			log.debug("Running create: {}", create);
			int rc = stmt.executeUpdate(create);
			log.debug("create returned: {}", rc);
			stmt.close();
			con.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected boolean hasTable(String name) {
		Statement stmt = null;
		boolean tableExists = false;
		Connection con = null;
		try {
			con = openConnection();
			stmt = con.createStatement();
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
		close(con);
		return tableExists;
	}
}
