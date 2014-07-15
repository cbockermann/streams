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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class FilesConnectionTest {

	static Logger log = LoggerFactory.getLogger(FilesConnectionTest.class);

	@Test
	public void test() {

		try {
			File dir = File.createTempFile("test", ".d");
			dir.delete();

			dir.mkdirs();
			if (!dir.isDirectory()) {
				log.error("Failed to create directory {}", dir);
				Assert.fail("Failed to create directory " + dir + "!");
			}

			for (int i = 0; i < 10; i++) {
				File fi = new File(dir.getAbsolutePath() + File.separator
						+ "file-" + i);
				FileWriter w = new FileWriter(fi);
				w.write(i + "\n");
				w.close();
				log.info("Write file {}", fi);
				fi.deleteOnExit();
			}

			ArrayList<Integer> results = new ArrayList<Integer>();

			SequentialFileInputStream fis = new SequentialFileInputStream(dir,
					"file-.*", false);
			fis.setMaxWaitingTime(0L);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));
			String line = reader.readLine();
			while (line != null) {
				log.info("Line: {}", line);
				results.add(new Integer(line.trim()));
				line = reader.readLine();
			}
			reader.close();

			for (int i = 0; i < 10; i++) {
				if (results.size() < i || (results.get(i).intValue() != i)) {
					Assert.fail("Failed to check item at position " + i
							+ " in result list. Was expecting " + i
							+ ", but found: " + results.get(i));
				}
				Assert.assertEquals(new Integer(i), results.get(i));
			}

		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			e.printStackTrace();
		}
		// fail("Not yet implemented");
	}
}
