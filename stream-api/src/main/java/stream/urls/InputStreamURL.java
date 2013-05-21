/**
 * 
 */
package stream.urls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import stream.io.SourceURL;

/**
 * @author chris
 * 
 */
public class InputStreamURL extends SourceURL {

	/** The unique class ID */
	private static final long serialVersionUID = -8598798178387924422L;
	final InputStream stream;

	public InputStreamURL(InputStream is) {
		super();
		stream = is;
	}

	/**
	 * @see stream.io.SourceURL#openStream()
	 */
	@Override
	public InputStream openStream() throws IOException {
		return stream;
	}

	/**
	 * @see stream.io.SourceURL#getFile()
	 */
	@Override
	public String getFile() {
		return "java.io.InputStream";
	}

	/**
	 * @see stream.io.SourceURL#getProtocol()
	 */
	@Override
	public String getProtocol() {
		return "direct";
	}

	/**
	 * @see stream.io.SourceURL#getHost()
	 */
	@Override
	public String getHost() {
		return "127.0.0.1";
	}

	/**
	 * @see stream.io.SourceURL#getPort()
	 */
	@Override
	public int getPort() {
		return 0;
	}

	/**
	 * @see stream.io.SourceURL#getPath()
	 */
	@Override
	public String getPath() {
		return "" + this.stream;
	}

	/**
	 * @see stream.io.SourceURL#getParameters()
	 */
	@Override
	public Map<String, String> getParameters() {
		return super.getParameters();
	}
}