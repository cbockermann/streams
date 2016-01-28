/**
 * 
 */
package profiler.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import stream.util.XMLUtils;

/**
 * @author chris
 *
 */
public class DependencyGraph {

    static Logger log = LoggerFactory.getLogger(DependencyGraph.class);

    final static String readColor = "gruen1!60";
    final static String firstColor = "gruen1";
    final static String writeColor = "orangeRand";

    public static Map<String, Dependency> available = new HashMap<String, Dependency>();

    public static Dependency get(String key) {
        if (!available.containsKey(key)) {
            return new Dependency(key);
            // available.put(key, new Dependency(":input:"));
        }
        Dependency dep = available.get(key);
        return dep;
    }

    /**
     * @param args
     */
    public static void main(String[] params) throws Exception {

        Map<String, String> currentlyAvailable = new HashMap<String, String>();
        Map<String, Dependency> deps = new HashMap<String, Dependency>();

        String[] args = params;
        // args = "/Users/chris/fact-tools-profiling.xml".split(",");

        if (args.length < 1) {
            System.err.println("Usage:");
            System.err.println("\tjava profiler.tools.AccessGraph profile-file.xml [output-file]");
            System.exit(-1);
        }

        File input = new File(args[0]);
        File output = new File(input.getAbsolutePath().replaceAll("\\.xml$", "") + ".tex");
        if (args.length > 1) {
            output = new File(args[1]);
        }

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(input);

        List<String> ks = new ArrayList<String>();

        double scaleY = new Double(System.getProperty("scale.y", "0.625"));
        double scaleLabel = new Double(System.getProperty("label.scale", "1.0"));
        double rotateLabel = new Double(System.getProperty("label.rotate", "-50.0"));

        Set<String> produced = new HashSet<String>();
        Set<String> accessed = new HashSet<String>();

        double stretch = new Double(System.getProperty("stretch", "0.9"));

        NodeList processes = doc.getElementsByTagName("process");

        for (int j = 0; j < processes.getLength(); j++) {

            Element process = (Element) processes.item(j);
            log.info("Handling process {}", process.getAttribute("id"));

            List<Element> ps = XMLUtils.getElements(process);
            for (int i = 0; i < ps.size(); i++) {
                Element e = ps.get(i);

                String className = e.getNodeName();
                String simple = className;
                int d = className.lastIndexOf(".");
                if (d > 0) {
                    simple = className.substring(d + 1);
                }

                log.info("Found processor '{}'", className);

                Set<String> readAccess = new LinkedHashSet<String>();

                NodeList reads = e.getElementsByTagName("read");
                for (int r = 0; r < reads.getLength(); r++) {
                    Element read = (Element) reads.item(r);
                    String key = read.getAttribute("key");

                    accessed.add(key);
                    readAccess.add(key);
                }

                NodeList writes = e.getElementsByTagName("write");
                if (writes != null) {
                    log.info("found {} write accesses", writes.getLength());
                    for (int w = 0; w < writes.getLength(); w++) {
                        Element write = (Element) writes.item(w);
                        log.info("write at {}: {}", w, write.getNodeName());
                        String key = write.getAttribute("key");
                        produced.add(key);

                        Dependency dep = new Dependency(key);
                        for (String read : readAccess) {
                            dep.add(get(read));
                        }
                        available.put(key, dep);
                    }
                }
            }
        }

        for (Dependency d : new TreeSet<Dependency>(available.values())) {
            System.out.print("'" + d.key + "' depends in: ");
            for (String dep : closure(d)) {
                System.out.print(" '" + dep + "'");
            }
            System.out.println();
            // System.out.println(d.toString());
        }
    }

    public static Set<String> closure(Dependency d) {
        Set<String> keys = new HashSet<String>();
        for (Dependency dp : d) {
            keys.add(dp.key);
            keys.addAll(closure(dp));
        }
        return keys;
    }

    public static class Dependency extends LinkedHashSet<Dependency>implements Comparable<Dependency> {
        private static final long serialVersionUID = 4966485400551562354L;
        final String key;

        public Dependency(String key) {
            this.key = key;
        }

        public String toString() {
            if (isEmpty()) {
                return "(" + key + ")";
            }

            StringBuffer s = new StringBuffer("('" + key + "' dependes on {");
            for (Dependency d : this) {
                s.append(d.toString() + " ");
            }
            s.append("})");
            return s.toString();
        }

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(Dependency o) {
            return key.compareTo(o.key);
        }
    }
}