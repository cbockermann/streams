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

import stream.util.XMLUtils;

/**
 * @author chris
 * 
 */
public class DependencyResolver {

    static Logger log = LoggerFactory.getLogger(DependencyResolver.class);
    final List<String> baseUrls = new ArrayList<String>();
    final Set<URL> classPathUrls = new LinkedHashSet<URL>();

    String localRepo = System.getProperty("user.home") + File.separator + ".m2" + File.separator + "repository"
            + File.separator;

    final Set<Dependency> resolvedDependencies = new LinkedHashSet<Dependency>();
    final Set<String> scopes = new HashSet<String>();

    public DependencyResolver() {

        if (System.getenv("STREAMS_REPOSITORY") != null && !"".equals(System.getenv("STREAMS_REPOSITORY").trim())) {
            localRepo = System.getenv("STREAMS_REPOSITORY");
            log.debug("Using local repository {}", localRepo);
        }
        // baseUrls.add(localRepo);
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

                        List<String> versions = extractVersions(cur);
                        log.debug("Versions found for {}: {}", cur.artifactId, versions);

                        List<String> tryVersions = this.tryVersions(versions, cur.version);
                        log.info("   trying: {}", tryVersions);
                        for (String sel : tryVersions) {

                            Dependency tryCur = new Dependency(cur);
                            tryCur.version = sel;
                            log.debug("Checking for {}", cur);

                            if (this.exists(tryCur)) {

                                Set<Dependency> trans = extractDependenciesFromPom(base, cur);

                                for (Dependency t : trans) {
                                    if (!resolvedDependencies.contains(t))
                                        toResolve.add(t);
                                }
                                found = true;
                                break;
                            } else {
                                log.info("    dependency {} does not exist", tryCur.getPomPath());
                                log.info("artifact required by {}");
                            }
                        }

                        if (found) {
                            break;
                        }

                        // String sel = selectVersion(versions, cur.version);
                        // cur.version = sel;
                        //
                        // found = true;
                        // break;
                    } catch (Exception e) {
                        log.error("Failed to resolve dependency {}", cur, base);
                    }
                }
                if (!found)
                    throw new Exception("Failed to resolve dependency '" + cur + "'!");
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

        File jar = new File(m2repo.getAbsolutePath() + File.separator + d.getPath());

        File pom = new File(m2repo.getAbsolutePath() + File.separator + d.getPomPath());

        if (jar.isFile() && pom.isFile()) {
            return jar;
        }

        return fetch(d, m2repo);
    }

    public List<String> extractVersions(Dependency d) {
        List<String> vs = new ArrayList<String>();

        for (String base : this.baseUrls) {

            if (base.startsWith("/"))
                base = "file:" + base;

            String metaXml = base + d.getGroupId().replace('.', '/') + "/" + d.getArtifactId() + "/maven-metadata.xml";
            log.debug("Fetching metadata-xml from {}", metaXml);
            try {

                URL url = new URL(metaXml);
                Document doc = XMLUtils.parseDocument(url.openStream());
                NodeList versions = doc.getElementsByTagName("version");
                for (int i = 0; i < versions.getLength(); i++) {
                    Element v = (Element) versions.item(i);
                    if (v.getTextContent() != null) {
                        String ver = v.getTextContent().trim();
                        if (ver.length() > 0 && !vs.contains(ver)) {
                            vs.add(ver);
                        }
                    }
                }

            } catch (Exception e) {
                log.error("Failed to find maven-metadata.xml at {}", metaXml);
                // e.printStackTrace();
            }
        }

        return vs;
    }

    public File fetch(Dependency d, File repoDir) throws Exception {
        Map<String, String> list = new LinkedHashMap<String, String>();
        list.put("http://repo.maven.apache.org/maven2/" + d.getPomPath(),
                repoDir.getAbsolutePath() + File.separator + d.getPomPath());
        list.put("http://repo.maven.apache.org/maven2/" + d.getPath(),
                repoDir.getAbsolutePath() + File.separator + d.getPath());

        File jarFile = new File(repoDir.getAbsolutePath() + File.separator + d.getPath());

        for (String urlString : list.keySet()) {

            File file = new File(list.get(urlString));
            if (file.exists()) {
                log.debug("File {} already exists.", file);
            } else {

                file.getParentFile().mkdirs();

                URL url = new URL(urlString);
                InputStream in = url.openStream();

                log.debug("Downloading artifact {} to {}", d.getArtifactId(), file.getAbsolutePath());
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
                log.debug("{} bytes fetched.", total);
                out.close();

            }
        }
        log.debug("Returning jar-file {}", jarFile.getAbsolutePath());
        return jarFile;
    }

    public boolean exists(Dependency dep) throws Exception {

        URL pomUrl = null;

        for (String baseUrl : this.baseUrls) {
            try {
                pomUrl = new URL(baseUrl + dep.getPomPath());

                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document pom = db.parse(pomUrl.openStream());
                if (pom != null) {
                    return true;
                }

            } catch (Exception e) {
                log.error("Failed to read pom URL '{}'!", pomUrl);
            }
        }

        return false;
    }

    protected Set<Dependency> extractDependenciesFromPom(String repoUrl, Dependency dep) throws Exception {

        URL pomUrl = new URL(repoUrl + dep.getPomPath());
        log.debug("Using pom @ {}", pomUrl);

        Set<Dependency> resolved = new HashSet<Dependency>();

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document pom = db.parse(pomUrl.openStream());

        Element root = pom.getDocumentElement();
        String pomVersion = null;

        NodeList parent = root.getElementsByTagName("parent");
        for (int k = 0; k < parent.getLength(); k++) {
            Element p = (Element) parent.item(k);
            NodeList nl = p.getChildNodes();
            for (int n = 0; n < nl.getLength(); n++) {
                Node ch = nl.item(n);
                if (ch.getNodeType() == Node.ELEMENT_NODE && ch.getNodeName().equals("version")) {
                    pomVersion = ch.getTextContent();
                }
            }
        }

        NodeList list = root.getElementsByTagName("dependency");
        for (int i = 0; i < list.getLength(); i++) {

            Element el = (Element) list.item(i);
            log.debug("Checking element {}", el.getNodeName());
            String groupId = null;
            String artifactId = null;
            String version = null;
            String scope = "compile";

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

            log.debug("artifact '{}', group '{}'", artifactId, groupId);
            log.debug("    scope = '{}', version = '{}'", scope, version);

            if (groupId != null && artifactId != null) {

                if (groupId.indexOf("${") >= 0) {
                    log.warn("Macro expansion currently not supported in dependency resolver!");
                } else {

                    if (version != null && version.startsWith("["))
                        version = version.substring(1);

                    if (version != null && version.endsWith(",)"))
                        version = version.replace(",)", "");

                    if (isScopeIncluded(scope)) {
                        log.debug("Adding dependency {} with scope {}", artifactId, scope);

                        Dependency d = new Dependency(groupId, artifactId, version);
                        List<String> versions = this.extractVersions(d);
                        d.addVersions(versions);
                        log.debug("{} versions are: {}", artifactId, versions);
                        String sel = selectVersion(versions, version);
                        log.debug("Selected version is: {}", sel);
                        d.version = sel;
                        resolved.add(d);
                    } else {
                        log.debug("Dependencies with scope '{}' will be ignored.", scope);
                    }
                }
            }
        }

        return resolved;
    }

    public List<String> tryVersions(List<String> versions, String ver) {

        List<String> tries = new ArrayList<String>();

        if (versions.isEmpty()) {
            log.info("No versions list could be created, assuming fixed version '{}' exists", ver);
            tries.add(ver);
            return tries;
        }

        if (ver != null && versions.contains(ver)) {
            log.debug("selecting specific version {}", ver);
            tries.add(ver);
            return tries;
        }

        tries.addAll(versions);
        Collections.reverse(tries);
        return tries;
    }

    public String selectVersion(List<String> versions, String ver) {

        if (versions.isEmpty()) {
            log.info("No versions list could be created, assuming fixed version '{}' exists", ver);
            return ver;
        }

        if (ver != null && versions.contains(ver)) {
            log.debug("selecting specific version {}", ver);
            return ver;
        }

        String sel = versions.get(versions.size() - 1);
        log.debug("selecting last version in list {} => {}", versions, sel);
        return sel;
    }

    public boolean isScopeIncluded(String scope) {

        if (scope == null || scopes.contains(scope.toLowerCase())) {
            log.debug("scope '{}' will be included...", scope);
            return true;
        }

        log.debug("scope '{}' will be excluded", scope);
        return false;
    }
}
