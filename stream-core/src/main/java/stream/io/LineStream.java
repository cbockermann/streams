/**
 * 
 */
package stream.io;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.util.parser.Parser;
import stream.util.parser.ParserGenerator;

/**
 * This is a very simple stream that just reads from a URL line-by-line. The
 * content of the line is stored in the attribute determined by the
 * <code>getKey()</code> method of this instance. By default the key
 * <code>LINE</code> is used.
 * 
 * It also supports the specification of a simple format string that can be used
 * to create a generic parser to populate additional fields of the data item
 * read from the stream.
 * 
 * The parser format is:
 * 
 * <pre>
 *       %{IP} [%{DATE}] "%{URL}"
 * </pre>
 * 
 * This will create a parser that is able to read line in the format
 * 
 * <pre>
 *       127.0.0.1 [2012/03/14 12:03:48 +0100] "http://example.com/index.html"
 * </pre>
 * 
 * The outcoming data item will have the attribute <code>IP</code> set to
 * "127.0.0.1" and the <code>DATE</code> attribute set to
 * "2012/03/14 12:03:48 +0100". The <code>URL</code> attribute will be set to
 * <code>http://example.com/index.html</code>.
 * 
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class LineStream extends AbstractDataStream {

	static Logger log = LoggerFactory.getLogger(LineStream.class);

	String key = "LINE";
	String format = null;
	Parser<Map<String, String>> parser = null;

	public LineStream(URL url) throws Exception {
		super(url);
		this.initReader();
	}

	public LineStream(URL url, String username, String password)
			throws Exception {
		super(url, username, password);
	}

	public LineStream(InputStream in) throws Exception {
		super(in);
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
		try {
			ParserGenerator pg = new ParserGenerator(format);
			parser = pg.newParser();
		} catch (Exception e) {
			throw new RuntimeException("Failed to create parser for format: "
					+ e.getMessage());
		}
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
		try {
			reader.close();
		} catch (Exception e) {
			log.error("FAiled to close reader: {}", e.getMessage());
			if (log.isTraceEnabled())
				e.printStackTrace();
		}
	}

	/**
	 * @see stream.io.AbstractDataStream#readHeader()
	 */
	@Override
	public void readHeader() throws Exception {
	}

	/**
	 * @see stream.io.AbstractDataStream#readItem(stream.data.Data)
	 */
	@Override
	public Data readItem(Data instance) throws Exception {

		String line = reader.readLine();
		if (line == null)
			return null;

		instance.put(key, line);

		if (parser != null) {
			Map<String, String> map = parser.parse(line);
			for (String key : map.keySet()) {
				try {
					instance.put(key, new Double(map.get(key)));
				} catch (Exception e) {
					instance.put(key, map.get(key));
				}
			}
		}

		return instance;
	}
}