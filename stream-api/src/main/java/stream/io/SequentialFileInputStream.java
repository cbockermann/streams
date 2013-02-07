package stream.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class implements an input stream that will read from a sequence of
 * files. It is intended to provide the opposite of a split of files into parts.
 * Each part is read in chronological order.
 * </p>
 * <p>
 * At the end, the stream waits for new data to be appended to the last file, or
 * a new file to be created that matches the names.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class SequentialFileInputStream extends InputStream {
	/* A global logger for this class */
	static Logger log = LoggerFactory
			.getLogger(SequentialFileInputStream.class);

	/*
	 * This constant object implements a comparator for files, which determines
	 * the order in which this file stream processes files
	 */
	final static Comparator<File> FILE_ORDER = new Comparator<File>() {
		public int compare(File arg0, File arg1) {
			if (arg0 == arg1 || arg0.compareTo(arg1) == 0)
				return 0;

			Long lastModified = new Long(arg0.lastModified());
			int rc = lastModified.compareTo(arg1.lastModified());
			if (rc == 0)
				return arg0.getName().compareTo(arg1.getName());

			return rc;
		}
	};

	/* The time gap this stream puts itself to sleep until new data is available */
	Integer sleep = 500;

	/* The initial file this reader was created from */
	File file = null;

	/* The file we currently read from */
	File current = null;

	/* The next file to read */
	File next = null;

	/* The input-stream of the current file */
	InputStream reader;

	String pattern = "";

	boolean closed = false;

	long maxWaitingTime = -1L;
	long read = 0L;
	long total = 0L;
	long waitingTime = 0L;
	boolean removeAfterRead = true;
	Set<File> finished = new TreeSet<File>(FILE_ORDER);

	/**
	 * Creates a new SequentialInputStream, which will read the specified file
	 * and any subsequent files that match the file's name, possibly and a
	 * appended number, i.e. for the file <code>/tmp/test.log</code> the stream
	 * will read
	 * 
	 * <pre>
	 *    /tmp/test.log
	 *    /tmp/test.log.1
	 *    /tmp/test.log.2
	 *    ...
	 * </pre>
	 * 
	 * The trailing digits may as well be time-stamps or the like. The files are
	 * read in order of their last-modification-time.
	 * 
	 * @param file
	 *            The initial file.
	 * @throws IOException
	 */
	public SequentialFileInputStream(File file) throws IOException {
		this(file, false);
	}

	/**
	 * <p>
	 * This creates a SequentialInputStream which will remove any files that
	 * have completeley been read (i.e. they have been processed until EOF
	 * <b>and</b> another, newer file matching the pattern does exist).
	 * </p>
	 * <p>
	 * Whether the old files are to be removed is determined by the
	 * <code>removeAfterRead</code> flag.
	 * </p>
	 * 
	 * @param file
	 *            The initial file to start with.
	 * @param removeAfterRead
	 *            Whether the old files should be removed or not.
	 * @throws IOException
	 */
	public SequentialFileInputStream(File file, boolean removeAfterRead)
			throws IOException {
		this(file, file.getName() + "(\\.\\d+)?$", removeAfterRead);
	}

	public SequentialFileInputStream(File file, String pattern,
			boolean removeAfterRead) throws IOException {
		this.file = file;
		this.current = this.file;
		this.pattern = pattern;
		this.removeAfterRead = removeAfterRead;

		if (current.isFile())
			reader = new FileInputStream(current);
	}

	public boolean matchesSequence(File f) {

		if (f.getAbsolutePath().equals(file.getAbsolutePath()))
			return false;

		if (f.isFile() && !f.equals(file) && f.getName().matches(pattern)) {
			return !finished.contains(f);
		}
		return false;
	}

	/**
	 * This method checks if there exists a next file in the sequence.
	 * 
	 * @return <code>true</code> if a new file exists, which may indicate that
	 *         the current file is finished.
	 */
	protected boolean hasNext() {
		File dir = file.getParentFile();
		if (file.isDirectory())
			dir = file;

		for (File f : dir.listFiles())
			if (matchesSequence(f) && !f.equals(current))
				return true;
		return false;
	}

	/**
	 * <p>
	 * This method closes the current file an opens the next file in the
	 * sequence. If no <i>next</i> file exists, this method will block until one
	 * has been created.
	 * </p>
	 * 
	 * @throws IOException
	 */
	protected void openNextFile() throws IOException {
		log.debug("Current file {} seems to have ended, checking for next one",
				current);
		boolean proceeded = false;

		do {
			TreeSet<File> files = new TreeSet<File>(FILE_ORDER);
			File dir = file.getParentFile();
			if (file.isDirectory())
				dir = file;

			for (File f : dir.listFiles()) {
				if (matchesSequence(f) && !finished.contains(f)) {
					// log.info(
					// "  File {} is considered a candidate to proceed", f );
					files.add(f);
				}
			}

			SortedSet<File> sequence = files; // .tailSet( current,
												// finished.contains( current )
												// );
			for (File file : sequence) {
				log.debug("   file: {} (modified: {})", file,
						file.lastModified());
			}
			if (!sequence.isEmpty()) {
				if (reader != null) {
					log.debug("Closing old reader on file {}...", current);
					reader.close();
				}
				if (removeAfterRead) {
					log.debug("Removing file {}", current);
					current.delete();
				}
				log.debug("Read {} bytes from {}", read, current);
				current = sequence.first();
				finished.add(current);
				read = 0L;
				reader = new FileInputStream(current);
				log.debug("Now reading from '{}'", current);
				proceeded = true;
			} else {
				try {
					log.debug("After reading {} bytes from {}", read, current);
					log.debug("   a total of {} bytes read so far", total);
					log.debug(
							"No sequential file found for {}, sleeping for {} ms and checking again...",
							file, sleep);
					Thread.sleep(sleep);
				} catch (Exception e) {
				}
			}
		} while (!proceeded);
	}

	/**
	 * <p>
	 * This read method is basically a read of the current open file. It will
	 * block if there is no more data and no new file exists.
	 * </p>
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {

		if (closed)
			return -1;

		while (reader == null)
			openNextFile();

		int data = reader.read();
		while (data == -1) {
			if (hasNext()) {
				openNextFile();
			} else {

				if (maxWaitingTime == 0)
					return -1;

				try {
					log.debug("Waiting for new data to arrive at file {}",
							current);
					Thread.sleep(sleep);
					waitingTime += sleep;
				} catch (Exception e) {
				}
				if (maxWaitingTime > 0 && waitingTime > maxWaitingTime) {
					closed = true;
					log.debug("Total sleeping time exhausted!");
					return -1;
				}
			}
			data = reader.read();
		}
		read++;
		total++;
		return data;
	}

	/**
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return false;
	}

	/**
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		if (reader == null)
			return super.available();
		return reader.available();
	}

	public static void main(String[] args) throws Exception {
		File input = new File("/tmp/test.log");

		SequentialFileInputStream f = new SequentialFileInputStream(input,
				false);
		PrintStream out = new PrintStream(new FileOutputStream(new File(
				input.getAbsolutePath() + "-complete")));

		byte[] buf = new byte[1024];
		int read = 0;
		int written = 0;
		do {
			if (f.available() > 0)
				buf = new byte[Math.min(1024, f.available())];
			read = f.read(buf);
			if (read >= 0) {
				written += read;
				out.write(buf, 0, read);
				out.flush();
			}
			log.debug("{} bytes written", written);
		} while (read > 0);
	}
}