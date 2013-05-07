/**
 * 
 */
package stream.urls;

import java.io.IOException;
import java.io.InputStream;

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
}
