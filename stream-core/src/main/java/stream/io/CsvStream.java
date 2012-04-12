/**
 * 
 */
package stream.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.runtime.annotations.Description;
import stream.runtime.annotations.Parameter;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Sources")
public class CsvStream extends AbstractDataStream {
	static Logger log = LoggerFactory.getLogger(CsvStream.class);

	String[] keys;
	String splitExpression = "(;|,)";
	LinkedList<String> buffer;

	/**
	 * @param url
	 * @throws Exception
	 */
	public CsvStream(URL url) throws Exception {
		super(url);
	}

	public CsvStream(InputStream in) throws Exception {
		super(in);
		this.splitExpression = "(;|,)";
		log.debug("Split expression is: {}", splitExpression);
		// initReader();
		reader = new BufferedReader(new InputStreamReader(in));
	}

	public CsvStream(InputStream in, String splitter) throws Exception {
		this(in, Charset.defaultCharset(), splitter);
	}

	public CsvStream(InputStream in, Charset charset, String splitter)
			throws Exception {
		super(in);
		this.splitExpression = splitter;
		log.debug("Split expression is: {}", splitExpression);
		reader = new BufferedReader(new InputStreamReader(in, charset));
	}

	public CsvStream(URL url, String splitExp) throws Exception {
		super(url);
		this.url = url;
		this.splitExpression = splitExp;
		this.initReader();
	}

	@Parameter(name = "separator", required = true, defaultValue = "(;|,)")
	public void setSeparator(String separator) {
		splitExpression = separator;
	}

	public String getSeparator() {
		return splitExpression;
	}

	public String removeQuotes(String str) {
		String s = str;
		if (s.startsWith("\""))
			s = s.substring(1);

		if (s.endsWith("\""))
			s = s.substring(0, s.length() - 1);

		return s;
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
	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public void readHeader() throws Exception {
		log.debug("Reading header, splitExpression is '{}'", splitExpression);
		if (buffer == null)
			buffer = new LinkedList<String>();

		String[] tok;

		if (keys == null || keys.length == 0) {

			String line = reader.readLine();
			log.debug("line is: {}", line);
			while (line.startsWith("#"))
				line = line.substring(1);

			tok = line.split(splitExpression);
			for (int i = 0; i < tok.length; i++) {
				tok[i] = removeQuotes(tok[i]);
				attributes.put(tok[i], Double.class);
			}
		} else
			tok = keys;

		String data = reader.readLine();
		while (data.startsWith("#")) {
			data = reader.readLine();
		}

		buffer.add(data);
		String dt[] = data.split(splitExpression);
		for (int i = 0; i < tok.length && i < dt.length; i++) {
			if (i < dt.length) {
				if (dt[i].matches("\\d*\\.\\d*"))
					attributes.put(tok[i], Double.class);
				else
					attributes.put(tok[i], String.class);
			}
		}
	}

	/**
	 * @see stream.io.DataStream#readNext()
	 */
	public Data readItem(Data datum) throws Exception {
		if (datum == null)
			datum = new DataImpl();
		else
			datum.clear();

		String line = readLine();
		while (line != null && (line.trim().isEmpty() || line.startsWith("#"))) {
			if (line.startsWith("#")) {
				String dt[] = line.substring(1).split(splitExpression);
				for (int i = 0; i < dt.length; i++) {
					if (i < dt.length) {
						if (dt[i].matches("(-|\\+)?\\d*\\.\\d*"))
							attributes.put(dt[i], Double.class);
						else
							attributes.put(dt[i], String.class);
					}
				}
			}

			line = reader.readLine();
		}

		if (line != null && !line.trim().equals("")) {
			String[] tok = line.split(splitExpression); // QuotedStringTokenizer.splitRespectQuotes(
			// line, ';');
			// //line.split( "(;|,)"
			// );
			int i = 0;
			for (String name : attributes.keySet()) {
				if (i < tok.length) {
					if (Double.class.equals(attributes.get(name))) {
						try {
							datum.put(name, new Double(removeQuotes(tok[i])));
						} catch (Exception e) {
							datum.put(name, removeQuotes(tok[i]));
						}
					} else
						datum.put(name, removeQuotes(tok[i]));
					i++;
				} else
					break;
			}
		} else
			return null;

		return preprocess(datum);
	}

	public String readLine() throws Exception {

		if (reader == null)
			initReader();

		if (buffer != null && !buffer.isEmpty())
			return buffer.removeFirst();
		return reader.readLine();
	}

	protected Data preprocess(Data datum) throws Exception {
		if (datum == null)
			return null;

		return datum;
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
		try {
			reader.close();
		} catch (Exception e) {
			log.error("Failed to properly close reader: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}
}