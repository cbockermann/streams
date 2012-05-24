/**
 * 
 */
package stream.runtime.setup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class LibrariesElementHandler implements ElementHandler {

	static Logger log = LoggerFactory.getLogger(LibrariesElementHandler.class);
	ObjectFactory objectFactory;

	public LibrariesElementHandler(ObjectFactory factory) {
		this.objectFactory = factory;
	}

	/**
	 * @see stream.runtime.ElementHandler#getKey()
	 */
	@Override
	public String getKey() {
		return "LibsHandler";
	}

	/**
	 * @see stream.runtime.ElementHandler#handlesElement(org.w3c.dom.Element)
	 */
	@Override
	public boolean handlesElement(Element element) {
		if (element.getNodeName().equalsIgnoreCase("libraries")
				|| element.getNodeName().equalsIgnoreCase("libs")
				|| element.getNodeName().equalsIgnoreCase("dependencies"))
			return true;
		return false;
	}

	/**
	 * @see stream.runtime.ElementHandler#handleElement(stream.runtime.ProcessContainer
	 *      , org.w3c.dom.Element)
	 */
	@Override
	public void handleElement(ProcessContainer container, Element element)
			throws Exception {

		String text = element.getTextContent();
		if (text == null) {
			return;
		}

		BufferedReader reader = new BufferedReader(new StringReader(text));
		String line = reader.readLine();
		while (line != null) {

			String[] dep = line.trim().split(":");
			if (dep.length != 3) {

			} else {
				Dependency d = new Dependency(dep[0], dep[1], dep[2]);
				File file = null;
				if (d.getLocalFile().exists()) {
					log.info("Dependency {} already exists at {}", line.trim(),
							d.getLocalFile());
					file = d.getLocalFile();
				} else {
					file = d.download();
				}
				System.out.println("Adding file " + file.getAbsolutePath()
						+ " to classpath!");
			}

			line = reader.readLine();
		}
	}

	public static class Dependency {

		String groupId;
		String artifactId;
		String version;

		public Dependency(String groupId, String artifactId, String version) {
			this.groupId = groupId;
			this.artifactId = artifactId;
			this.version = version;
		}

		public File getLocalFile() {

			StringBuffer s = new StringBuffer(System.getProperty("user.home")
					+ File.separator + ".m2" + File.separator + "repository"
					+ File.separator);
			s.append(getPath());
			File file = new File(s.toString());
			return file;
		}

		public String getPath() {
			return groupId.replace('.', '/') + "/" + artifactId + "/" + version
					+ "/" + artifactId + "-" + version + ".jar";
		}

		public File download() throws Exception {

			StringBuffer s = new StringBuffer(
					"http://repo.maven.apache.org/maven2/");

			s.append(getPath());

			URL url = new URL(s.toString());
			InputStream in = url.openStream();

			log.info("Downloading library {} from {}", artifactId, url);
			File file = new File(System.getProperty("user.home") + "/.streams/"
					+ artifactId + "-" + version + ".jar");

			File lib = new File(System.getProperty("user.home")
					+ File.separator + ".streams" + File.separator + getPath());
			lib.getParentFile().mkdirs();
			file = lib;

			FileOutputStream out = new FileOutputStream(file);

			byte[] buf = new byte[8192];
			int total = 0;
			int read = in.read(buf);
			while (read > 0) {
				total += read;
				out.write(buf, 0, read);
				read = in.read(buf);
				// System.out.println(total + " bytes fetched...");
			}
			log.info("{} bytes fetched.", total);
			out.close();
			return file;
		}
	}

	public static void main(String[] args) throws Exception {

		Dependency dep = new Dependency("org.jwall", "org.jwall.web.audit",
				"0.6.2");
		File file = dep.getLocalFile();
		System.out.println("Dependency should be at " + file.getAbsolutePath());

		File download = dep.download();
		System.out.println("Downloaded artifact to "
				+ download.getAbsolutePath());
	}
}