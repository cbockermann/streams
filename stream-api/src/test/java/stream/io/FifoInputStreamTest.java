/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
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

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.FileUtils;
import stream.util.MD5;

/**
 * @author chris
 * 
 */
public class FifoInputStreamTest {

	static Logger log = LoggerFactory.getLogger(FifoInputStreamTest.class);

	@Test
	public void test() {

		try {
			File fifo = new File("/tmp/" + System.currentTimeMillis()
					+ ".queue");
			fifo = new File("/tmp/test-fifo.queue");
			final File file = FileUtils.mkfifo(fifo);
			file.deleteOnExit();
			log.info("Testing FifoInputStream with fifo at {}", file);

			if (System.getProperty("testing") == null)
				return;

			final FifoInputStream fis = new FifoInputStream(file);

			// Generator g = new Generator(file);
			// g.start();

			Thread t = new Thread(new Runnable() {
				public void run() {
					try {

						log.info("Opening reader for fifo-pipe {}", file);
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(fis), 8);
						log.info("Reading line from fifo...");
						String line = reader.readLine();
						while (line != null) {
							log.info("Read line: {}", line);
							line = reader.readLine();
						}

						log.info("Reading finished, closing reader-side of fifo");
						reader.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			t.start();

			t.join();

			fis.close();

		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
			fail("Error: " + e.getMessage());
		}
	}

	public class Generator extends Thread {
		Logger log = LoggerFactory.getLogger(Generator.class);
		File file;

		public Generator(File f) {
			this.file = f;
		}

		public void run() {
			try {

				int delay = 10;
				log.info("Delaying data generation for {} seconds...", delay);
				// Thread.sleep(1000 * delay);

				log.info("Opening output stream to fifo-pipe...");
				FileOutputStream fos = new FileOutputStream(file);
				int i = 0;

				while (i++ < 10) {
					String data = MD5.md5("1234" + i) + "\n";
					log.info("Writing '{}'", data.replace("\n", "\\n"));
					fos.write(data.getBytes());
					fos.flush();
					Thread.sleep(1000);
				}

				log.info("Closing fifo-pipe...");
				fos.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}