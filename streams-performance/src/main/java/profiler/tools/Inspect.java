/**
 * 
 */
package profiler.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.util.URLUtilities;
import stream.util.Variables;
import stream.util.WildcardPattern;
import stream.util.XMLUtils;
import streams.tikz.Tikz;

/**
 * @author chris
 *
 */
public class Inspect {

    static Logger log = LoggerFactory.getLogger(Inspect.class);

    public static boolean matches(String name, Collection<String> patterns) {
        for (String pattern : patterns) {
            if (WildcardPattern.matches(pattern, name)) {
                return true;
            }

            if (name.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    public static String createTikzPicture(Element e) {

        StringWriter s = new StringWriter();
        PrintWriter p = new PrintWriter(s);

        final Map<String, String> reads = new TreeMap<String, String>();
        final Map<String, String> writes = new TreeMap<String, String>();

        final String name = e.getNodeName();

        List<Element> fields = XMLUtils.getElementsByName(e, "fields");
        if (fields.isEmpty()) {
            return null;
        }

        NodeList cs = e.getChildNodes();
        for (int i = 0; i < cs.getLength(); i++) {
            Node c = cs.item(i);
            if (c.getNodeType() == Node.ATTRIBUTE_NODE) {
                log.info("Attribute: {} = {}", c.getNodeName(), c.getNodeValue());
            }
        }

        Element fs = fields.get(0);
        Map<String, String> attr = XMLUtils.getAttributes(e);
        log.info("attributes: {}", attr);

        for (Element w : XMLUtils.getElementsByName(fs, "write")) {
            log.info("Adding write for '{}'", w.getAttribute("key"));
            writes.put(w.getAttribute("key"), w.getAttribute("type"));
        }

        for (Element r : XMLUtils.getElementsByName(fs, "read")) {
            String k = r.getAttribute("key");
            String t = r.getAttribute("type");
            log.info(e.getNodeName() + " ::  {} => {}", k, t);
            reads.put(k, t);
        }

        double w = 5.0;
        double h = 3.0;
        p.println("\\fill " + Tikz.point(0, 0) + " circle(4pt); ");

        p.println("\\node[scale=0.75] at (0," + Tikz.format(h * 0.5 + 0.75) + ") {\\color{blauRand}\\textbf{\\textsf{"
                + name + "}}};");
        p.println("\\node[draw=blauRand,thick,fill=blau,rectangle,rounded corners=4pt,inner sep=0pt,minimum width="
                + Tikz.format(w) + "cm,minimum height=" + Tikz.format(h) + "cm] (P) at (0,0) {};");

        p.println("\\node at (0,0) {\n\t\\begin{tikzpicture}[scale=0.5,transform shape]");
        double px = 0.0;
        double py = 0.0;
        for (String param : attr.keySet()) {
            p.println(
                    "\t\t\\node[anchor=west,scale=0.75] at "
                            + Tikz.point(px, py) + "{\\color{white}\\ttfamily " + param + " = " + attr.get(param)
                                    .replace("_", "\\_").replace("%", "\\%").replace("{", "\\{").replace("}", "\\}")
                    + "};");
            py -= 0.35;
        }

        p.println("\t\\end{tikzpicture}");
        p.println("};");

        // p.println("\\draw[blauRand,thick,fill=blau,rounded corners=4pt] (P) "
        // + Tikz.point(0 - w * 0.5, 0 - h * 0.5)
        // + " rectangle " + Tikz.point(0 + w * 0.5, 0 + h * 0.5) + ";");

        double rs = reads.size();
        double ws = writes.size();

        double keyHeight = 0.65;
        double x = 0.0;
        double y = (ws * keyHeight) / 2.0 - (keyHeight * 0.5);
        double maxHeight = y;
        double minHeight = 0.0;
        x = 4.0;
        int idx = 0;
        for (String wr : writes.keySet()) {
            maxHeight = Math.max(maxHeight, y);
            minHeight = Math.min(minHeight, y);

            String t = writes.get(wr);

            p.println("\\node[anchor=west] (W" + idx + ") at " + Tikz.point(x, y) + "{");
            p.println("\t\\begin{tikzpicture}[scale=0.75,transform shape]");
            p.println("\t\t\\node[anchor=west,scale=0.75] (W" + idx + ") at " + Tikz.point(0, 0.15) + " {\\ttfamily "
                    + wr.replace("_", "\\_") + "};");
            p.println("\t\t\\node[anchor=west,scale=0.5] (W" + idx + ") at " + Tikz.point(0, -0.15)
                    + " {\\color{black!70}\\ttfamily " + t.replace("_", "\\_") + "};");
            p.println("\t\\end{tikzpicture}");
            p.println("};");

            p.println("\\draw[thick,black!60,->] " + Tikz.point(0 + w * 0.5 + 0.125, y * 0.25) + " -- (W" + idx
                    + ".west);");
            // p.println("\\draw[thick,black!60,->] (P.east) -- (W" + idx +
            // ".west);");
            y -= keyHeight;
            idx++;
        }

        x = -4.0;
        y = (rs * keyHeight) / 2.0 - (keyHeight * 0.5);
        idx = 0;
        for (String r : reads.keySet()) {
            maxHeight = Math.max(maxHeight, y);
            minHeight = Math.min(minHeight, y);

            String t = reads.get(r);
            if (t == null) {
                log.info("No type provided for read on key '{}' in processor {}", r, e.getNodeName());
                t = "?";
            }

            p.println("\\node[anchor=east] (R" + idx + ") at " + Tikz.point(x, y) + "{");
            p.println("\t\\begin{tikzpicture}[scale=0.75,transform shape]");
            p.println("\t\t\\node[anchor=east,scale=0.75] (W" + idx + ") at " + Tikz.point(0, 0.15) + " {\\ttfamily "
                    + r.replace("_", "\\_") + "};");
            p.println("\t\t\\node[anchor=east,scale=0.5] (W" + idx + ") at " + Tikz.point(0, -0.15)
                    + " {\\color{black!70}\\ttfamily " + t.replace("_", "\\_") + "};");
            p.println("\t\\end{tikzpicture}");
            p.println("};");

            // p.println("\\node[anchor=east,scale=0.75] (R" + idx + ") at " +
            // Tikz.point(x, y) + " {\\ttfamily " + label
            // + "};");
            p.println("\\draw[thick,black!60,->] (R" + idx + ".east) -- " + Tikz.point(0 - w * 0.5 - 0.125, y * 0.25)
                    + ";");
            y -= keyHeight;
            idx++;
        }

        maxHeight = Math.max(2.5, maxHeight + 0.5);

        p.println("\\draw[orangeRand] (-8," + Tikz.format(-maxHeight) + ") rectangle (9," + Tikz.format(maxHeight)
                + ");");

        p.close();
        return s.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] params) throws Exception {

        String[] args = params;

        args = "/Users/chris/Uni/Projekte/fact-tools/cb-performance/profile-cb-analysis-data.xml *".split(" ");
        args = "/Users/chris/fact-minimal-perf.xml *".split(" ");

        final File profile = new File(args[0]);
        final List<String> classPatterns = new ArrayList<String>();
        final List<String> pics = new ArrayList<String>();

        for (int i = 1; i < args.length; i++) {
            classPatterns.add(args[i]);
        }

        List<String> written = new ArrayList<String>();

        Document doc = XMLUtils.parseDocument(profile);

        NodeList processes = doc.getElementsByTagName("process");
        for (int i = 0; i < processes.getLength(); i++) {

            Element process = (Element) processes.item(i);

            NodeList ps = process.getChildNodes();
            for (int j = 0; j < ps.getLength(); j++) {
                Node ch = ps.item(j);
                if (ch.getNodeType() == Node.ELEMENT_NODE) {
                    Element p = (Element) ch;
                    String name = p.getNodeName();
                    if (matches(name, classPatterns)) {
                        String tikz = createTikzPicture(p);
                        if (tikz != null) {
                            pics.add(tikz);
                        }

                        List<Element> fs = XMLUtils.getElements(p);
                        for (Element f : fs) {
                            List<Element> write = XMLUtils.getElementsByName(f, "write");
                            for (Element w : write) {
                                written.add(w.getAttribute("key"));
                            }
                        }
                    }
                }
            }
        }

        log.info("List of written fields:\n{}", written);

        TN featureTree = new TN("root");
        for (String field : written) {
            String[] path = field.split(":");
            featureTree.add(path, 0);
        }

        StringBuffer s = new StringBuffer();
        for (String tikz : pics) {
            s.append("\\begin{tikzpicture}[scale=0.75,transform shape]\n");
            // log.info("{}", tikz);
            s.append(tikz + "\n");
            s.append("\\end{tikzpicture}\n\n\n\n\n");
        }

        String template = URLUtilities.readContent(Inspect.class.getResource("/tikz-template.tex"));

        Variables vars = new Variables();
        vars.put("tikz.picture", s.toString());

        String out = vars.expand(template);
        FileWriter writer = new FileWriter("/tmp/inspector.tex");
        writer.write(out);
        writer.close();

        TexCompiler c = new TexCompiler();
        // c.compile(new File("/tmp/inspector.tex"), new File("/tmp"));

        featureTree.print();
    }

    static class TN {
        TN parent;
        String name;
        List<TN> children = new ArrayList<TN>();

        public TN(String name) {
            this.name = name;
        }

        public TN child(String name) {
            for (TN ch : children) {
                if (ch.name.equals(name)) {
                    return ch;
                }
            }
            return null;
        }

        public void add(String[] path, int i) {
            if (i >= path.length) {
                return;
            }

            String cur = path[i];
            TN sib = child(cur);
            if (sib == null) {
                sib = new TN(cur);
                sib.parent = this;
                children.add(sib);
            }

            sib.add(path, i + 1);
        }

        public int depth() {
            if (parent == null) {
                return 0;
            }
            return parent.depth() + 1;
        }

        public void print() {
            String prefix = "";
            for (int i = 0; i < depth(); i++) {
                prefix += "  ";
            }

            System.out.println(prefix + name);
            for (TN ch : children) {
                ch.print();
            }
        }
    }
}