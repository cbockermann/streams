/**
 * 
 */
package stream.urls;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.SourceURL;

/**
 * @author chris
 * 
 */
public class FilesConnectionTest {

	static Logger log = LoggerFactory.getLogger(FilesConnectionTest.class);

	static List<String> linesWritten = new ArrayList<String>();

	@Test
	public void test() {

		try {
			File dir = new File("/tmp/" + System.currentTimeMillis() + ".d");
			log.info("files directory is {}", dir);

			dir.mkdirs();

			if (!dir.isDirectory()) {
				log.error("Failed to create directory {}", dir);
			}

			String pattern = dir.getAbsolutePath() + "/*";
			log.info("pattern is: '{}'", pattern);

			String url = "files:" + pattern;
			log.info("URL is '{}'", url);

			Consumer c = new Consumer(new SourceURL(url
					+ "?maxWaitingTime=1000&removeAfterRead=true"));
			c.start();

			Producer p = new Producer(dir, 4, 250L);
			p.run();
			log.info("Producer finished.");

			log.info("Waiting for consumer...");
			c.join();
			log.info("Consumer finished.");

			log.info("Producer write {} lines.", linesWritten.size());
			log.info("Consumer read  {} lines.", c.linesRead.size());

			Assert.assertEquals(linesWritten.size(), c.linesRead.size());

			for (int i = 0; i < linesWritten.size(); i++) {
				String exp = linesWritten.get(i);
				String found = c.linesRead.get(i);
				Assert.assertEquals(exp, found);
			}

			log.info("All lines match expectations!");

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

	public class Consumer extends Thread {
		final Logger log = LoggerFactory.getLogger(Consumer.class);
		final SourceURL url;
		final List<String> linesRead = new ArrayList<String>();

		public Consumer(SourceURL url) {
			this.url = url;
		}

		public void run() {
			try {
				// BufferedReader reader = new BufferedReader(
				// new InputStreamReader(url.openStream()));

				InputStream in = url.openStream();
				InputStreamReader r = new InputStreamReader(in);
				// String line = readLine(r);

				BufferedReader reader = new BufferedReader(r);
				String line = reader.readLine();

				while (line != null) {
					log.info("Read line: '{}'", line);
					linesRead.add(line);
					line = reader.readLine();
					// line = readLine(r);
				}
				in.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String readLine(InputStreamReader in) throws IOException {
			StringBuffer s = new StringBuffer();

			int c = in.read();
			while (c != 0) {

				if (c == 0xffffffff) {
					log.info("Found EOF!?");
					return null;
				}

				// log.info("Char:  '{}'  (0x{})", (char) c,
				// Integer.toHexString(c));
				if (c == 0xa) {
					log.info("found end-of-line!");
					return s.toString();
				} else {
					s.append((char) c);
				}

				c = in.read();
			}

			return s.toString();
		}
	}

	public class Producer extends Thread {

		final Logger log = LoggerFactory.getLogger(Producer.class);
		File dir;
		int files;
		long delay;

		public Producer(File f, int files, long delay) {
			dir = f;
			this.files = files;
			this.delay = delay;
		}

		public void run() {

			for (int i = 0; i < files; i++) {

				File f = new File(dir.getAbsolutePath() + File.separator
						+ "output." + i);

				try {
					if (!f.getParentFile().isDirectory()) {
						log.info("Creeating directory {}", f);
						f.getParentFile().mkdirs();
					}
					log.info("Starting file {}", f);
					PrintWriter w = new PrintWriter(new FileWriter(f));
					int count = 0;
					for (int l = 0; l < 5; l++) {
						String line = f.getAbsolutePath() + " @ line " + l;
						log.info("Writing line to {}", f);
						w.println(line);
						w.flush();
						linesWritten.add(line);
						count++;
						Thread.sleep(delay);
					}
					log.info("{} lines written to file {}", count, f);
					w.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}
}
