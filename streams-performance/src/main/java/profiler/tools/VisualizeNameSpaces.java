/**
 * 
 */
package profiler.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
public class VisualizeNameSpaces {

    static Map<String, String> keys = new LinkedHashMap<String, String>();
    final static AtomicInteger id = new AtomicInteger(0);

    public static String nextId() {
        int nodeId = id.incrementAndGet();
        return "N" + nodeId;
    }

    public static void add(String key) {
        keys.put(key, "");
    }

    public static void add(String key, String type) {
        if (type == null) {
            keys.put(key, "");
        } else {
            keys.put(key, type);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] params) throws Exception {
        //
        // add("1:1");
        // add("1:2:1");
        // add("1:2:2");
        // add("1:3:1");

        String[] args = params;
        args = "/Users/chris/fact-tools-profile.xml".split(" ");

        if (args.length > 0) {
            Document doc = XMLUtils.parseDocument(new File(args[0]));
            NodeList list = doc.getElementsByTagName("fields");
            System.out.println("Found " + list.getLength() + " 'fields' elements!");
            for (int i = 0; i < list.getLength(); i++) {
                Element fs = (Element) list.item(i);

                List<Element> access = new ArrayList<Element>();
                access.addAll(XMLUtils.getElementsByName(fs, "read"));
                access.addAll(XMLUtils.getElementsByName(fs, "write"));

                for (Element fieldAccess : access) {
                    add(fieldAccess.getAttribute("key"), fieldAccess.getAttribute("type"));
                }
            }
        } else {
            add("meta:id");
            add("meta:npix");
            add("meta:nroi");
            add("meta:night");

            add("mc:corsika:energy");
            add("mc:corsika:label");
            add("mc:ceres:something");

            add("pixels:arrivalTimes");
            add("pixels:arrivalTimes:mean");
            add("pixels:arrivalTimes:std");

            add("pixels:estNumPhotons");
            add("pixels:estNumPhotons:mean");
            add("pixels:estNumPhotons:std");

            add("shower:ellipse");
            add("shower:ellipse:width");
            add("shower:ellipse:length");
            add("shower:ellipse:size");

            add("pedestal:baseline");
            add("pedestal:baseline:mean");
            add("pedestal:baseline:std");
        }

        Node tree = new Node();

        for (String key : keys.keySet()) {
            tree.add(key.split(":"));
        }

        File texFile = new File("/tmp/name-space-tree.tex");
        PrintStream out = new PrintStream(new FileOutputStream(texFile));
        // tree.print();
        // tree.printTikz(System.out);

        out.println("\\documentclass{scrartcl}");
        out.println("\\usepackage{tikz}");
        out.println("\\usepackage{color}");
        out.println("\\definecolor{gruen1}{RGB}{105,193,16}");
        out.println("\\begin{document}");
        out.println("\\begin{tikzpicture}");

        int maxDepth = maxDepth(tree);
        List<Node> dfs = tree.dfs();
        int lvs = 0;

        for (Node n : dfs) {
            if (n.depth() == maxDepth) {
                lvs++;
            }
        }
        System.out.println("max depth is: " + maxDepth);
        System.out.println(lvs + " leaves have max-depth!");
        // layout(tree, 4.0);

        Iterator<Node> it = dfs.iterator();
        Node prev = null;
        double x = 0.0;
        Double width = 3.0;
        double y = 0.0;
        while (it.hasNext()) {
            Node cur = it.next();

            x = cur.depth() * 2.5;
            y -= 0.5;

            // y = 0.0; // cur.height() / 2.0;
            // if (cur.parent != null) {
            // double off = cur.height() / 2.0;
            //
            // double sibs = cur.parent.siblings.size();
            // double idx = cur.parent.siblings.indexOf(cur);
            // double height = cur.height() / sibs;
            // y = cur.parent.pos.y + idx * height - off;
            // // y = cur.height() / 2.0;
            // }

            if (cur.pos != null) {
                System.out.println("Using existing layout position");
                x = cur.pos.x;
                y = cur.pos.y;
            } else {
                cur.pos = new Point(x, y);
            }

            String label = cur.name;
            if (!cur.leaf()) {
                label = cur.name;
            }

            if (!label.trim().isEmpty()) {
                out.println("\\node[minimum width=" + Tikz.format(width)
                        + "cm, minimum height=0.6cm,rectangle,draw=black!40,anchor=west,scale=0.65] (" + cur.id
                        + ")  at " + new Point(x, y) + " { \\ttfamily{ " + label + "} };");
            }

            if (cur.parent != null) {
                double xoff = 1.0;
                out.println("\\draw[draw=gruen1] " + new Point(cur.parent.pos.x + xoff, cur.parent.pos.y - 0.25)
                        + " -- " + new Point(cur.parent.pos.x + xoff, cur.pos.y) + " -- " + cur.pos + ";");
                // out.println("\\draw (" + cur.parent.id + ".east) -- (" +
                // cur.id + ".west);");
            }

            prev = cur;
        }

        out.println("\\end{tikzpicture}");
        out.println("\\end{document}");
        out.close();

        TexCompiler tc = new TexCompiler();
        tc.compile(texFile, new File("/tmp/"));
    }

    public static void layout(Node node, double stepping) {

        if (node.pos == null) {
            node.pos = new Point(0, 0);
        }

        double depth = node.depth();
        double ht = node.height();

        // leads to
        double y0 = node.pos.y + ht * 0.5;

        for (Node ch : node.siblings) {
            ch.pos = new Point(4.0 * ch.depth(), y0);
            layout(ch, stepping / 2.0);
            y0 -= stepping;
        }
    }

    public static int maxDepth(Node node) {
        int depth = node.depth();
        for (Node sib : node.siblings) {
            depth = Math.max(depth, maxDepth(sib));
        }
        return Math.max(node.depth(), depth);
    }

    public static int countLeaves(Node node) {
        if (node.leaf()) {
            return 1;
        } else {

            int count = 0;
            for (Node ch : node.siblings) {
                count += countLeaves(ch);
            }
            return count;
        }
    }

    public static class Node {

        final String id = nextId();
        final Node parent;

        final String name;
        final String type;

        List<Node> siblings = new ArrayList<Node>();

        Point pos;

        public Node() {
            this(null, " ", null);
        }

        public Node(String name, String type) {
            this(null, name, type);
        }

        public Node(Node parent, String name, String type) {
            this.parent = parent;
            this.name = name;
            this.type = type;
        }

        public void add(String[] path) {
            add(path, null);
        }

        public void add(String[] path, String type) {
            List<String> p = new ArrayList<String>();
            for (String pe : path) {
                p.add(pe);
            }
            add(p, type);
        }

        private void add(List<String> path, String type) {
            if (path.isEmpty()) {
                return;
            }

            Node next = null;
            String first = path.remove(0);
            for (Node ch : siblings) {
                if (ch.name.equals(first)) {
                    next = ch;
                    break;
                }
            }

            if (next == null) {
                next = new Node(this, first, type);
                siblings.add(next);
            }

            if (!path.isEmpty()) {
                next.add(path, type);
            }
        }

        public boolean leaf() {
            return siblings.isEmpty();
        }

        public int depth() {
            if (parent == null) {
                return 0;
            } else {
                return parent.depth() + 1;
            }
        }

        public double height() {
            double sum = 0.0;
            for (Node ch : siblings) {
                sum += ch.height();
            }

            return Math.max(1.0, sum);
        }

        public List<Node> dfs() {
            List<Node> order = new ArrayList<Node>();
            order.add(this);
            for (Node ch : siblings) {
                order.addAll(ch.dfs());
            }
            return order;
        }

        public String path() {
            if (parent == null) {
                return name;
            } else {
                return parent.path() + ":" + name;
            }
        }

        public void print() {

            String prefix = "";
            int depth = depth();
            for (int i = 0; i < depth; i++) {
                prefix += "   ";
            }

            System.out.println(prefix + "'" + name + "'");
            for (Node ch : siblings) {
                ch.print();
            }
        }

        public void printTikz(PrintStream out) {

            String prefix = "";
            int depth = depth();
            for (int i = 0; i < depth; i++) {
                prefix += "   ";
            }

            if (depth == 0) {
                out.println("\\node{ " + " }");
            } else {
                out.print(prefix + " node {" + name + "}");
            }

            if (!siblings.isEmpty()) {
                out.println();
                for (Node ch : siblings) {
                    out.print(prefix + " child { ");
                    ch.printTikz(out);
                }
            } else {
            }
            out.println(prefix + " } ");
        }
    }
}