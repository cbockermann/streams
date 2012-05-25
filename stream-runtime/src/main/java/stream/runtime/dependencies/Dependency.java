/**
 * 
 */
package stream.runtime.dependencies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is a simple entity object that represents a Maven dependency
 * definition. It contains a groupId, artifactId and version.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */

public class Dependency {

	static Logger log = LoggerFactory.getLogger(Dependency.class);
	final String groupId;
	final String artifactId;
	final String version;

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

	public String getPomPath() {
		return groupId.replace('.', '/') + "/" + artifactId + "/" + version
				+ "/" + artifactId + "-" + version + ".pom";
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

		File lib = new File(System.getProperty("user.home") + File.separator
				+ ".streams" + File.separator + getPath());
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

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Dependency)
			return toString().compareTo(obj.toString()) == 0;
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public String toString() {
		return groupId + ":" + artifactId + ":" + version;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @return the artifactId
	 */
	public String getArtifactId() {
		return artifactId;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
}
