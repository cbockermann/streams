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
 * @author chris
 * 
 */
public abstract class AbstractLineStream extends AbstractStream {

	protected Map<String, Class<?>> attributes;
	protected BufferedReader reader;

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

	/**
	 * @see stream.io.Source#close()
	 */
	@Override
	public void close() throws Exception {
		if (reader != null)
			reader.close();
	}

	/**
	 * @see stream.io.Stream#init()
	 */
	@Override
	public void init() throws Exception {
		attributes = new LinkedHashMap<String, Class<?>>();
		reader = new BufferedReader(new InputStreamReader(getInputStream()));
	}

	public String readLine() throws Exception {
		return reader.readLine();
	}
}
