/**
 * 
 */
package stream.urls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.SourceURL;

/**
 * <p>
 * This class provides an implementation for <code>fifo://</code> URLs. Such a
 * URL points to a file that is expected to be a named pipe (Unix). Thus, this
 * implementation is not portable and can only be used on Unix systems.
 * </p>
 * <p>
 * If the file denoted by the URL does not exist, a new named pipe is created
 * using the <code>mkfifo</code> system command, that is expected to exist on
 * the system.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public class FIFOConnection extends Connection {

	static Logger log = LoggerFactory.getLogger(FIFOConnection.class);

	protected InputStream inputStream;

	/**
	 * @param url
	 */
	public FIFOConnection(SourceURL url) {
		super(url);
	}

	/**
	 * @see stream.urls.Connection#getSupportedProtocols()
	 */
	@Override
	public String[] getSupportedProtocols() {
		return new String[] { "fifo" };
	}

	/**
	 * @see stream.urls.Connection#connect()
	 */
	@Override
	public InputStream connect() throws IOException {

		try {
			String theUrl = url.toString();

			log.debug("Handling FIFO URL pattern...");
			theUrl = theUrl.replace("fifo:", "file:");
			File file = new File(theUrl.replace("file:", ""));
			if (!file.exists()) {
				log.debug("Creating new fifo file '{}' with mkfifo", file);
				Process p = Runtime.getRuntime().exec(
						"mkfifo " + file.getAbsolutePath());
				log.debug("Waiting for mkfifo to return...");
				int ret = p.waitFor();
				log.debug("mkfifo finished: {}", ret);
			} else {
				log.debug("Using existing fifo-file '{}'", file);
			}

			if (!file.exists()) {
				throw new IOException("Failed to create/acquire FIFO file '"
						+ file.getAbsolutePath() + "'!");
			}

			log.debug("Returning FileInputStream for FIFO {}", file);
			inputStream = new FileInputStream(file);
			return inputStream;

		} catch (Exception e) {
			throw new IOException("Failed to open fifo-queue: "
					+ e.getMessage());
		}
	}

	/**
	 * @see stream.urls.Connection#disconnect()
	 */
	@Override
	public void disconnect() throws IOException {
		if (inputStream != null)
			inputStream.close();
	}
}