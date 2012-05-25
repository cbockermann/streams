/**
 * 
 */
package stream.runtime.dependencies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author chris
 * 
 */
public class DependencyResolver {

	static Logger log = LoggerFactory.getLogger(DependencyResolver.class);
	final List<String> baseUrls = new ArrayList<String>();
	final Set<URL> classPathUrls = new LinkedHashSet<URL>();

	String localRepo = System.getProperty("user.home") + File.separator + ".m2"
			+ File.separator + "repository" + File.separator;

	final Set<Dependency> resolvedDependencies = new LinkedHashSet<Dependency>();
	final Set<String> scopes = new HashSet<String>();

	public DependencyResolver() {

		if (System.getenv("STREAMS_REPOSITORY") != null
				&& !"".equals(System.getenv("STREAMS_REPOSITORY").trim())) {
			localRepo = System.getenv("STREAMS_REPOSITORY");
			log.info("Using local repository {}", localRepo);
		}
		baseUrls.add(localRepo);
		baseUrls.add("http://repo.maven.apache.org/maven2/");
		scopes.add("compile");
		scopes.add("provided");
	}

	public Set<URL> getClasspathURLs() {
		return Collections.unmodifiableSet(classPathUrls);
	}

	public Set<Dependency> resolve(Dependency dep) throws Exception {

		Set<Dependency> toResolve = new HashSet<Dependency>();
		toResolve.add(dep);

		while (!toResolve.isEmpty()) {
			log.debug("{} dependency left to resolve", toResolve.size());
			Iterator<Dependency> it = toResolve.iterator();
			Dependency cur = it.next();
			it.remove();

			if (!resolvedDependencies.contains(cur)) {

				resolvedDependencies.add(cur);
				boolean found = false;
				for (String base : baseUrls) {
					try {

						if (base.startsWith("/"))
							base = "file:" + base;

						log.info("Checking for {}", cur);
						Set<Dependency> trans = extractDependenciesFromPom(
								base, cur);

						for (Dependency t : trans) {
							if (!resolvedDependencies.contains(t))
								toResolve.add(t);
						}
						found = true;
						break;
					} catch (Exception e) {
						log.error("Failed to resolve dependency {}", cur, base);
					}
				}
				if (!found)
					throw new Exception("Failed to resolve dependency '" + cur
							+ "'!");
			}
		}

		for (Dependency d : resolvedDependencies) {
			File file = getFileLocation(d);
			classPathUrls.add(file.toURI().toURL());
		}

		return resolvedDependencies;
	}

	protected File getFileLocation(Dependency d) throws Exception {

		File m2repo = new File(localRepo);

		File jar = new File(m2repo.getAbsolutePath() + File.separator
				+ d.getPath());

		File pom = new File(m2repo.getAbsolutePath() + File.separator
				+ d.getPomPath());

		if (jar.isFile() && pom.isFile()) {
			return jar;
		}

		return fetch(d, m2repo);
	}

	public File fetch(Dependency d, File repoDir) throws Exception {
		Map<String, String> list = new LinkedHashMap<String, String>();
		list.put("http://repo.maven.apache.org/maven2/" + d.getPomPath(),
				repoDir.getAbsolutePath() + File.separator + d.getPomPath());
		list.put("http://repo.maven.apache.org/maven2/" + d.getPath(),
				repoDir.getAbsolutePath() + File.separator + d.getPath());

		File jarFile = new File(repoDir.getAbsolutePath() + File.separator
				+ d.getPath());

		for (String urlString : list.keySet()) {

			File file = new File(list.get(urlString));
			if (file.exists()) {
				log.info("File {} already exists.", file);
			} else {

				file.getParentFile().mkdirs();

				URL url = new URL(urlString);
				InputStream in = url.openStream();

				log.info("Downloading artifact {} to {}", d.getArtifactId(),
						file.getAbsolutePath());
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

			}
		}
		log.info("Returning jar-file {}", jarFile.getAbsolutePath());
		return jarFile;
	}

	protected Set<Dependency> extractDependenciesFromPom(String repoUrl,
			Dependency dep) throws Exception {

		URL pomUrl = new URL(repoUrl + dep.getPomPath());
		log.debug("Using pom @ {}", pomUrl);

		Set<Dependency> resolved = new HashSet<Dependency>();

		DocumentBuilder db = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document pom = db.parse(pomUrl.openStream());

		Element root = pom.getDocumentElement();
		String pomVersion = null;

		NodeList parent = root.getElementsByTagName("parent");
		for (int k = 0; k < parent.getLength(); k++) {
			Element p = (Element) parent.item(k);
			NodeList nl = p.getChildNodes();
			for (int n = 0; n < nl.getLength(); n++) {
				Node ch = nl.item(n);
				if (ch.getNodeType() == Node.ELEMENT_NODE
						&& ch.getNodeName().equals("version")) {
					pomVersion = ch.getTextContent();
				}
			}
		}

		NodeList list = root.getElementsByTagName("dependency");
		for (int i = 0; i < list.getLength(); i++) {

			Element el = (Element) list.item(i);

			String groupId = null;
			String artifactId = null;
			String version = null;
			String scope = null;

			NodeList children = el.getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {

				Node node = children.item(j);
				if (node.getNodeName().equals("groupId"))
					groupId = node.getTextContent();

				if (node.getNodeName().equals("artifactId"))
					artifactId = node.getTextContent();

				if (node.getNodeName().equals("version"))
					version = node.getTextContent();

				if (node.getNodeName().equals("scope"))
					scope = node.getTextContent();
			}

			if (pomVersion != null && "${pom.version}".equals(version)) {
				version = pomVersion;
			}

			if (groupId != null && artifactId != null && version != null) {

				if (version.startsWith("["))
					version = version.substring(1);

				if (version.endsWith(",)"))
					version = version.replace(",)", "");

				if (isScopeIncluded(scope)) {
					log.debug("Adding dependency {} with scope {}", artifactId,
							scope);
					resolved.add(new Dependency(groupId, artifactId, version));
				} else {
					log.debug("Dependencies with scope '{}' will be ignored.",
							scope);
				}

			}
		}

		return resolved;
	}

	public boolean isScopeIncluded(String scope) {

		if (scope == null || scopes.contains(scope.toLowerCase())) {
			return true;
		}
		return false;
	}
}
