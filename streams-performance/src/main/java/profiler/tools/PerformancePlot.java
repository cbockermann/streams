/**
 * 
 */
package profiler.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import stream.util.WildcardPattern;
import stream.util.XMLUtils;
import streams.tikz.Path;
import streams.tikz.Point;
import streams.tikz.Tikz;

/**
 * @author chris
 *
 */
public class PerformancePlot {

    static Logger log = LoggerFactory.getLogger(PerformancePlot.class);

    /**
     * @param args
     */
    public static void main(String[] params) throws Exception {

        String[] args = params;

        if (args.length < 1) {
            System.err.println("Usage:");
            System.err.println("\tjava profiler.tools.PerformancePlot profile-file.xml [output-file]");
            System.exit(-1);
        }

        File input = new File(args[0]);
        File output = new File(input.getAbsolutePath().replaceAll("\\.xml$", "") + ".tex");
        if (args.length > 1) {
            output = new File(args[1]);
        }

        String filter = System.getProperty("processors", "*");
        LinkedHashMap<String, Double> columns = new LinkedHashMap<String, Double>();

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(input);

        NodeList processes = doc.getElementsByTagName("process");
        for (int i = 0; i < processes.getLength(); i++) {
            Element process = (Element) processes.item(i);

            List<Element> ps = XMLUtils.getElements(process);

            for (Element processor : ps) {

                String className = processor.getNodeName();
                log.info("Found processor {}", className);

                // boolean interest = className.contains("features"); // ||
                // // className.contains("BasicExtraction");
                boolean interest = WildcardPattern.matches(filter, className);
                if (!interest) {
                    log.info("  >>> skipping");
                    continue;
                }

                List<Element> ch = XMLUtils.getElements(processor);

                for (Element c : ch) {
                    if (c.getNodeName().equalsIgnoreCase("performance")) {
                        String nanos = c.getAttribute("nanos");
                        columns.put(className, new Double(nanos));
                    }
                }
            }
        }

        Double width = new Double(System.getProperty("column.width", "1.0"));
        Double margin = new Double(System.getProperty("column.margin", "0.1"));
        Double scaleY = new Double(System.getProperty("scale.y", "0.001"));
        Double labelScale = new Double(System.getProperty("label.scale", "0.65"));
        Double labelRotate = new Double(System.getProperty("label.rotate", "-30.0"));

        Double max = 0.0;
        for (Double d : columns.values()) {
            max = Math.max(d, max);
        }

        double maxY = 10.0;
        double maxX = columns.size() * (width + margin) + 0.25;

        scaleY = maxY / max;

        PrintStream p = new PrintStream(new FileOutputStream(output));
        // p.println("\\begin{tikzpicture}");

        double xoff = 0.0;

        for (String key : columns.keySet()) {
            Path col = new Path();
            col.set("draw", "gruen1").set("fill", "gruen1!50");
            double value = scaleY * columns.get(key);
            col.add(new Point(xoff, 0.0));
            col.add(new Point(xoff + width, 0.0));
            col.add(new Point(xoff + width, value));
            col.add(new Point(xoff, value));
            col.add(new Point(xoff, 0.0));

            p.println(col.toString());

            p.println("\\node[anchor=west,scale=" + Tikz.format(labelScale) + ",rotate=" + Tikz.format(labelRotate)
                    + " ] at " + new Point(xoff + 0.5 * width, -0.25) + " {\\ttfamily " + key.replace("example.", "")
                    + "};");
            xoff += (width + margin);
        }

        Path xaxis = new Path();
        xaxis.set("draw", "black!70"); // .set("thick", "");
        xaxis.add(new Point(-0.25, 0));
        xaxis.add(new Point(maxX, 0));
        p.println(xaxis.toString());

        // Path yaxis = new Path();
        // yaxis.set("draw", "black!70").set("thick", "");
        // yaxis.add(new Point(0, 0));
        // yaxis.add(new Point(0, maxY));
        // p.println(yaxis.toString());

        // p.println("\\end{tikzpicture}");

        p.close();
    }
}
