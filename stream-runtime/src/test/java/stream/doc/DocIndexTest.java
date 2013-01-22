/**
 * 
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
