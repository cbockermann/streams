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
package stream.runtime;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Date;

/**
 * <p>
 * This is a simple class for providing version information about this package.
 * The version information is obtained from the /org.jwall.web.audit.info file.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class Version {
	String groupId;

	String artifactId;

	String version;

	String revision = "";

	String build = "";

	Date released;

	public Version(String ver, String rev, Date releaseDate) {
		this.version = ver;
		this.revision = rev;
		this.released = releaseDate;
	}

	public Version(String groupId, String artifactId, String ver, String rev,
			String build, Date releaseDate) {
		this(ver, rev, releaseDate);
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.build = build;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the revision
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * @param revision
	 *            the revision to set
	 */
	public void setRevision(String revision) {
		this.revision = revision;
	}

	/**
	 * @return the released
	 */
	public Date getReleased() {
		return released;
	}

	/**
	 * @param released
	 *            the released to set
	 */
	public void setReleased(Date released) {
		this.released = released;
	}

	public final static Version getVersion(String groupId, String artifactId) {

		final String propertiesPath = "/" + artifactId + ".info"; // +
																	// PATH_POSTFIX;
		// log.debug( "Looking for version-file: {}", propertiesPath );
		try {
			final InputStream resourceStream = Version.class
					.getResourceAsStream(propertiesPath);
			if (resourceStream == null)
				return new Version(groupId, artifactId, "?", "?", "",
						new Date());

			LineNumberReader reader = new LineNumberReader(
					new InputStreamReader(resourceStream));

			String versionString = "";
			String groupString = "";
			String artifactString = artifactId;
			String buildString = "";

			// find "version=..."
			String line = reader.readLine();
			while (line != null) {

				if (!line.trim().startsWith("#")) {
					String[] t = line.split("=", 2);
					if (t[0].equals("version"))
						versionString = t[1].trim();

					if (t[0].equals("groupId"))
						groupString = t[1].trim();

					if (t[0].equals("artifactId"))
						artifactString = t[1].trim();

					if (t[0].equals("build"))
						buildString = t[1].trim();

				}

				line = reader.readLine();
			}

			reader.close();
			return new Version(groupString, artifactString, versionString, "",
					buildString, new Date());

		} catch (Exception e) {
			System.err
					.println("can't load pom.properites => version information will be unavailable");
		}

		return new Version(groupId, artifactId, "?", "?", "", new Date());
	}

	public String toString() {
		String b = build;
		if (b != null)
			b = b.replaceAll("\\D*", "");

		StringBuffer s = new StringBuffer();
		if (groupId != null && !groupId.isEmpty())
			s.append(groupId + ":");

		if (artifactId != null)
			s.append(artifactId);

		if (version != null && !version.trim().isEmpty())
			s.append("-" + version.trim());

		if (b != null && !b.trim().isEmpty())
			s.append("-b" + b);

		return s.toString();
	}

	/**
	 * This method extracts the revision number from the given string. The input
	 * string is expected to simply contain the subversion $Revision...$ format.
	 * 
	 * @param revision
	 * @return
	 */
	public static String extractRevision(String revision) {
		int st = revision.indexOf(" ");
		if (st < 0)
			return "";
		int en = revision.indexOf(" ", st + 1);
		if (en < st)
			return "";

		String rev = revision.substring(st + 1, en);
		return rev;
	}

	public String getBuild() {
		return build;
	}

	public static void main(String[] args) {

		if (args.length > 1) {
			System.out.println(Version.getVersion(args[0], args[1]));
		} else {
			System.out.println(Version.getVersion("org.jwall", "stream-api"));
		}
	}
}