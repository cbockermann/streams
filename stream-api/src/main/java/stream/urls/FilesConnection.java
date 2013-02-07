/**
 * 
 */
package stream.urls;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import stream.io.SequentialFileInputStream;
import stream.io.SourceURL;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class FilesConnection extends Connection {

	protected SequentialFileInputStream seqFileStream;

	/**
	 * @param url
	 */
	public FilesConnection(SourceURL url) {
		super(url);
	}

	/**
	 * @see stream.urls.Connection#getSupportedProtocols()
	 */
	@Override
	public String[] getSupportedProtocols() {
		return new String[] { "files" };
	}

	/**
	 * @see stream.urls.Connection#connect()
	 */
	@Override
	public InputStream connect() throws IOException {

		boolean removeAfterRead = "true".equalsIgnoreCase(url.getParameters()
				.get("remove"));

		String pattern = ".*";
		if (url.getParameters().containsKey("pattern")) {
			pattern = url.getParameters().get("pattern");
		}

		File file = new File(url.getPath());
		seqFileStream = new SequentialFileInputStream(file, pattern,
				removeAfterRead);
		return seqFileStream;
	}

	/**
	 * @see stream.urls.Connection#disconnect()
	 */
	@Override
	public void disconnect() throws IOException {
		if (seqFileStream != null) {
			seqFileStream.close();
			seqFileStream = null;
		}
	}
}
