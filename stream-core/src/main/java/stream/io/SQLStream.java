/**
 * 
 */
package stream.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import stream.data.Data;

/**
 * @author chris
 * 
 */
public class SQLStream extends AbstractDataStream {

	String url;
	String driver;
	String select;
	Connection connection;

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
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @param driver
	 *            the driver to set
	 */
	public void setDriver(String driver) {
		this.driver = driver;
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

			connection = DriverManager.getConnection(getUrl(), getUsername(),
					getPassword());

			PreparedStatement stmt = connection.prepareStatement(select);

		} catch (Exception e) {

		}

	}

	/**
	 * @see stream.io.AbstractDataStream#readItem(stream.data.Data)
	 */
	@Override
	public Data readItem(Data instance) throws Exception {
		return null;
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {

	}
}
