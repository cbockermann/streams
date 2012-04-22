/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequentialFileInputStream extends InputStream {
	static Logger log = LoggerFactory
			.getLogger(SequentialFileInputStream.class);

	public static interface FileHandler {

		public void handle(File file) throws Exception;
	}

	/* The default sleep time to wait between checking for new files... */
	Integer sleep = 1000;

	/* The initial file this reader was created from */
	File file = null;

	/* The file we currently read from */
	File current = null;

	Long currentOffset = 0L;

	/* The next file to read */
	File next = null;

	/* The reader of the current file */
	BufferedReader reader;

	boolean removeAfterRead = true;

	Long startTime = 0L;

	boolean running = true; // a running-flag

	Long toSkip = 0L;

	/* This handler is called after we finished reading a file... */
	FileHandler oldFileHandler = null;

	public SequentialFileInputStream(File file) throws IOException {
		this(file, 0L, false);
	}

	public SequentialFileInputStream(File file, long start,
			boolean removeAfterRead) throws IOException {
		this.file = file;
		this.current = this.file;
		startTime = start;

		this.removeAfterRead = removeAfterRead;
		if (removeAfterRead) {
			oldFileHandler = new FileHandler() {
				@Override
				public void handle(File file) throws Exception {
					file.delete();
				}
			};
		}
	}

	public File getCurrentFile() {
		return current;
	}

	public Long getCurrentOffset() {
		return currentOffset;
	}

	/**
	 * @return the oldFileHandler
	 */
	public FileHandler getOldFileHandler() {
		return oldFileHandler;
	}

	/**
	 * @param oldFileHandler
	 *            the oldFileHandler to set
	 */
	public void setOldFileHandler(FileHandler oldFileHandler) {
		this.oldFileHandler = oldFileHandler;
	}

	public boolean matchesSequence(File f) {
		if (log.isTraceEnabled()) {
			log.trace("checking file {}", f);
			log.trace("   start-time: {}", startTime);
			log.trace("   f.last-mod: {}  ({})", f.lastModified(),
					new Date(f.lastModified()));
		}

		if (f.isFile()
				&& (f.equals(file) || f.getName().matches(
						file.getName() + "(\\.\\d+)?"))) {
			if (reader == null || f.lastModified() > current.lastModified())
				return true;
			else
				log.debug("Current file {} is still newer than {}", current, f);
		}
		return false;
	}

	public boolean hasNext() {
		File dir = file.getParentFile();
		for (File f : dir.listFiles())
			if (matchesSequence(f))
				return true;
		return false;
	}

	protected void openNextFile() throws IOException {
		log.debug("Current file {} seems to have ended, checking for next one",
				current);
		boolean proceeded = false;

		do {
			TreeSet<File> files = new TreeSet<File>();
			File dir = file.getParentFile();
			for (File f : dir.listFiles()) {
				if (matchesSequence(f) && this.startTime <= f.lastModified()) {
					log.debug(
							"  File {}  (last-modified-at {}) is considered a candidate to proceed",
							f, f.lastModified());

					if (startTime <= f.lastModified())
						files.add(f);
					else
						log.debug("Skipping file {} which has modification "
								+ f.lastModified()
								+ " time BEFORE offset-time ({})", f, startTime);
				}
			}

			SortedSet<File> sequence = files; // .tailSet( file, false );
			log.debug("candidate files: {}", sequence);

			if (!sequence.isEmpty()) {
				//
				//
				//

				if (reader != null) {
					log.debug("Closing old reader...");
					reader.close();
				}

				try {
					if (this.oldFileHandler != null)
						oldFileHandler.handle(current);
				} catch (Exception e) {
					e.printStackTrace();
				}

				current = sequence.first();
				startTime = current.lastModified();
				currentOffset = 0L;
				reader = new BufferedReader(new FileReader(current));
				if (toSkip > 0) {
					log.debug("Skipping {} bytes", toSkip);
					Long skipped = reader.skip(toSkip);
					toSkip = 0L;
					this.currentOffset += skipped;
				}
				proceeded = true;
			} else {

				if (!running) {
					log.trace("file-reader closed, returning!");
					return;
				}

				log.debug("sequential reader running? {}", running);

				try {
					log.debug(
							"No sequential file found for {}, sleeping for {} ms and checking again...",
							file, sleep);
					Thread.sleep(sleep);
				} catch (Exception e) {
					log.debug("seq-file-reader running? {}", running);
					e.printStackTrace();
				}
			}

			if (!running)
				return;

		} while (running && !proceeded);
	}

	/**
	 * Read a line from the current file, if the file reveals a 'null' line AND
	 * there is a similarly named file already existing, then switch over to the
	 * 'next' file and start reading from that.
	 * 
	 * @return
	 */
	public synchronized String readLine() {

		if (!running)
			return null;

		try {
			while (reader == null) {
				log.trace("reader is null, opening next file...");
				openNextFile();

				if (!running)
					return null;

				if (reader == null) {
					log.trace("Waiting for new file to become available...");
					Thread.sleep(sleep);
				}
			}

			log.trace("Reading line from {}...", this.current);
			String line = reader.readLine();
			while (line == null) {

				if (!running)
					return null;

				if (line == null && hasNext()) {
					log.trace("Switching to next file...");
					openNextFile();
				} else {
					log.trace("Waiting for lines to be appended to {}", current);
					Thread.sleep(sleep);
				}
				log.trace("Reading next line from {}...", current);
				line = reader.readLine();
				log.trace("   line is: {}", line);
			}

			log.trace("Read line (from {}): {}", current, line);
			currentOffset += line.getBytes().length + 1;
			return line;
		} catch (InterruptedException ie) {
			if (running) {
				log.error(
						"interrupted-exception, but reader still running: {}",
						ie.getMessage());
				ie.printStackTrace();
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getSource() {
		return current + "";
	}

	@Override
	public long skip(long bytes) {
		long skipped = 0L;
		try {
			if (reader != null) {
				skipped = reader.skip(bytes);
				this.currentOffset += skipped;
			} else {
				toSkip = bytes;
			}
		} catch (Exception e) {
			log.error("Failed to skip {} bytes: {}", bytes, e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
		return skipped;
	}

	public Long bytesRead() {
		return this.getCurrentOffset();
	}

	public void close() {
		log.trace("Closing sequential-file-reader");
		this.running = false;
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int data = reader.read();
		if (data >= 0)
			currentOffset++;
		return data;
	}
}