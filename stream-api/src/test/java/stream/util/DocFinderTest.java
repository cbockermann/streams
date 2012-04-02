/**
 * 
 */
package stream.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.ResourceFinder.Condition;

/**
 * @author chris
 * 
 */
public class DocFinderTest {

	static Logger log = LoggerFactory.getLogger(DocFinderTest.class);

	public final static Condition isMarkdownAndProcessor = new Condition() {
		@Override
		public boolean matches(String path) {
			try {
				if (path.endsWith(".md")) {
					String f = path.replace(".md", "");
					log.debug("  replaced .md to {}", f);
					String cn = path.replace(".md", "").replaceAll("/", ".")
							.replaceFirst("\\.", "");
					log.debug("Expecting class to be {}", cn);
					Class.forName(cn);
					return true;
				}
				return false;
			} catch (Exception e) {
				return false;
			}
		}
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			String[] resources = ResourceFinder.getResources("",
					isMarkdownAndProcessor);
			for (String res : resources) {
				log.info("Found: {}", res);
			}
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
		}
	}

}
