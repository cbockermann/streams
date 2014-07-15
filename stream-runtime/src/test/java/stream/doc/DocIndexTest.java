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
package stream.doc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.doc.helper.DocIndex;
import stream.doc.helper.DocIndex.Result;
import stream.util.URLUtilities;

/**
 * @author chris
 * 
 */
public class DocIndexTest {

	static Logger log = LoggerFactory.getLogger(DocIndexTest.class);

	@Test
	public void test() {
		try {
			DocIndex index = new DocIndex();
			Map<Class<?>, URL> help = DocFinder.findDocumentations(null);

			for (Class<?> clazz : help.keySet()) {
				URL url = help.get(clazz);
				String text = URLUtilities.readContentOrEmpty(url);
				index.add(text, url, clazz);
			}

			List<Result> results = index.search("attribute item");
			for (Result result : results) {
				log.info("doc: {}, score: {}", result.url, result.score);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			DocIndex index = new DocIndex();
			Map<Class<?>, URL> help = DocFinder.findDocumentations(null);

			for (Class<?> clazz : help.keySet()) {
				URL url = help.get(clazz);
				String text = URLUtilities.readContentOrEmpty(url);
				index.add(text, url, clazz);
			}

			List<Result> results = index.search("stream");
			for (Result result : results) {
				log.info("doc: {}, score: {}", result.url, result.score);
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			String line = null;
			do {
				line = reader.readLine();
				if (line != null && !line.trim().isEmpty()) {
					results = index.search(line.trim());
					for (Result result : results) {
						log.info("doc: {}, score: {}", result.url, result.score);
					}
				}

			} while (line != null && !line.trim().isEmpty());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
