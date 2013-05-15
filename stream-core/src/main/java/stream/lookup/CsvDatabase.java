/**
 * 
 */
package stream.lookup;

import java.util.Map;

import stream.Data;
import stream.annotations.Parameter;
import stream.io.CsvStream;
import stream.io.SourceURL;

/**
 * <p>
 * An implementation of the AbstractDatabase lookup service, that reads data
 * from a CSV file at initialization time.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public class CsvDatabase extends AbstractDatabase {

	boolean header = true;
	String[] keys = null;
	String separator = "(;|,)";

	/**
	 * @see stream.lookup.AbstractDatabase#populateDatabase(stream.io.SourceURL,
	 *      java.util.Map)
	 */
	@Override
	protected void populateDatabase(SourceURL url, Map<String, Data> database)
			throws Exception {
		CsvStream stream = new CsvStream(url);
		stream.setHeader(isHeader());
		stream.setKeys(getKeys());
		stream.setSeparator(getSeparator());
		stream.init();
		readDatabase(stream, database);
		stream.close();
	}

	/**
	 * @return the header
	 */
	public boolean isHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	@Parameter(description = "Whether to use the first line as headers (keys/attribute names). ", required = false)
	public void setHeader(boolean header) {
		this.header = header;
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
	@Parameter(description = "The keys of the CSV file. If not specified, the first line of the CSV will be used as keys.", required = false)
	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator
	 *            the separator to set
	 */
	@Parameter(description = "The separator string used for the CSV stream reading.", required = false)
	public void setSeparator(String separator) {
		this.separator = separator;
	}
}
