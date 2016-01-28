/**
 * 
 */
package profiler.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import stream.util.XMLUtils;
import streams.tikz.Point;
import streams.tikz.Tikz;

/**
 * @author chris
 *
 */
public class AccessGraph {

    static Logger log = LoggerFactory.getLogger(AccessGraph.class);

    final static String readColor = "gruen1!60";
    final static String firstColor = "gruen1";
    final static String writeColor = "orangeRand";

    /**
     * @param args
     */
    public static void main(String[] params) throws Exception {

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

        PrintStream p = new PrintStream(new FileOutputStream(output));

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

                p.println("\\node[rotate=" + Tikz.format(rotateLabel) + ",anchor=west,scale=" + Tikz.format(scaleLabel)
                        + "] at " + new Point(i * stretch, -1 * scaleY - 0.25) + " {\\ttfamily " + simple + "};");

                double x = i;

                NodeList reads = e.getElementsByTagName("read");
                for (int r = 0; r < reads.getLength(); r++) {
                    Element read = (Element) reads.item(r);
                    String key = read.getAttribute("key");

                    String color = readColor;
                    if (!produced.contains(key) & !accessed.contains(key)) {
                        color = firstColor;
                    }

                    accessed.add(key);

                    int idx = ks.indexOf(key);
                    if (idx < 0) {
                        ks.add(key);
                        idx = ks.indexOf(key);
                    }

                    Point dot = new Point((x - 0.25) * stretch, idx * scaleY);
                    p.println("\\draw[thin,draw=black!20] " + dot + " -- " + new Point(x * stretch, idx * scaleY)
                            + " -- " + new Point(x * stretch, -1.0 * scaleY) + " ;");

                    p.println("\\node[circle,fill=" + color
                            + ",inner sep=0pt,minimum height=1.5ex,minimum width=1.5ex] at " + dot + " {};");
                }

                NodeList writes = e.getElementsByTagName("write");
                if (writes != null) {
                    log.info("found {} write accesses", writes.getLength());
                    for (int w = 0; w < writes.getLength(); w++) {
                        Element write = (Element) writes.item(w);
                        log.info("write at {}: {}", w, write);
                        String key = write.getAttribute("key");
                        produced.add(key);

                        int idx = ks.indexOf(key);
                        if (idx < 0) {
                            ks.add(key);
                            idx = ks.indexOf(key);
                        }

                        Point dot = new Point((x + 0.25) * stretch, idx * scaleY);
                        p.println("\\draw[thin,draw=black!20] " + dot + " -- " + new Point(x * stretch, idx * scaleY)
                                + " -- " + new Point(x * stretch, -1.0 * scaleY) + " ;");

                        p.println("\\node[circle,fill=" + writeColor
                                + ",inner sep=0pt,minimum height=1.5ex,minimum width=1.5ex] at " + dot + " {};");
                    }
                }
                // Point c = new Point(2.0, y);
                // p.println(
                // "\\node[rectangle,minimum width=1cm,minimum height=1cm,inner
                // sep=0pt,draw=black!70,fill=black!6] at "
                // + c + " {};");
            }

            for (String k : ks) {
                int idx = ks.indexOf(k);
                p.println("\\node[anchor=east,scale=" + Tikz.format(scaleLabel) + "] at " + new Point(-1, idx * scaleY)
                        + " {\\color{black!80}{\\ttfamily " + k.replaceAll("_", ":") + "}};");
            }
        }

        p.close();
    }
}