/**
 * 
 */
package stream.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author chris,hendrik
 * 
 */
public abstract class AbstractLineStream extends AbstractStream {

	protected Map<String, Class<?>> attributes;
	protected BufferedReader reader;
	protected String encoding;

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @param in
	 */
	public AbstractLineStream(SourceURL url) {
		super(url);
	}

	/**
	 * @param in
	 */
	public AbstractLineStream(InputStream in) {
		super(in);
	}

	public AbstractLineStream() throws Exception {
		super();
	}

	/**
	 * @see stream.io.Source#close()
	 */
	@Override
	public void close() throws Exception {
		if (reader != null)
			reader.close();
		super.close();
	}

	/**
	 * @see stream.io.Stream#init()
	 */
	@Override
	public void init() throws Exception {
		super.init();
		attributes = new LinkedHashMap<String, Class<?>>();

		if (encoding != null)
			reader = new BufferedReader(new InputStreamReader(getInputStream(),
					encoding));
		else
			reader = new BufferedReader(new InputStreamReader(getInputStream()));
	}

	public String readLine() throws Exception {
		return reader.readLine();
	}
}
