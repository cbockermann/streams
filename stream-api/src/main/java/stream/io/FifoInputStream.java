/**
 * 
 */
package stream.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.FileUtils;

/**
 * @author chris
 * 
 */
public class FifoInputStream extends InputStream {

	static Logger log = LoggerFactory.getLogger(FifoInputStream.class);
	final File fifo;
	InputStream pipe;

	public FifoInputStream(File pipe) throws Exception {
		fifo = FileUtils.mkfifo(pipe);
	}

	/**
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		if (pipe != null)
			return pipe.available();
		return 0;
	}

	/**
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		if (pipe != null)
			pipe.close();
	}

	/**
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void mark(int arg0) {
		if (pipe != null)
			pipe.mark(arg0);
	}

	/**
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		if (pipe != null)
			return pipe.markSupported();
		return false;
	}

	/**
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		if (pipe != null)
			pipe.reset();
	}

	/**
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long arg0) throws IOException {
		if (pipe != null)
			return pipe.skip(arg0);
		return 0L;
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {

		if (pipe == null) {

			if (fifo == null) {
				throw new FileNotFoundException("No file specified!");
			}

			if (!fifo.exists() || fifo.isDirectory()) {
				log.error(
						"The file denoted by '{}' does not exist or is a directory!",
						fifo);
				throw new FileNotFoundException("File "
						+ fifo.getAbsolutePath()
						+ " does not exist or is a directory!");
			}

			log.debug("Opening new file-based input-stream for FIFO {}", fifo);
			pipe = new FileInputStream(fifo);
			log.info("Pipe is: {}", pipe);
		}

		if (pipe != null) {

			int b = pipe.read();
			// log.info("Read: {} ({})", Integer.toHexString(b), b);

			// while (b < 0) {
			// try {
			// b = pipe.read();
			// // log.debug("Waiting for new data to arrive at the fifo...");
			// System.out.print(".");
			// Thread.sleep(50);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }

			return b;
		}
		return -1;
	}
}