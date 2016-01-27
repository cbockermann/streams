/**
 * 
 */
package streams.tikz;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import stream.util.XMLUtils;

/**
 * @author chris
 *
 */
public class Tikz2SVG {

    static Logger log = LoggerFactory.getLogger(Tikz2SVG.class);

    public Document createSVG(List<Component> elements) throws Exception {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();

        Rectangle bounds = Rectangle.boundsOf(elements);
        Element svg = doc.createElement("svg");
        svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
        svg.setAttribute("version", "1.1");
        svg.setAttribute("baseProfile", "full");

        svg.setAttribute("width", Tikz.format(bounds.width()) + "px");
        svg.setAttribute("height", Tikz.format(bounds.height()) + "px");

        doc.appendChild(svg);

        for (Component c : elements) {
            Element e = createElement(doc, c);
            if (e != null) {
                svg.appendChild(e);
            }
        }

        return doc;
    }

    protected Element createElement(Document doc, Component o) {

        if (o instanceof Path) {
            log.info("Calling specific createPath(..) method");
            return createPath(doc, (Path) o);
        }

        return null;
    }

    protected Element createPath(Document doc, Path p) {

        if (p.isEmpty()) {
            return null;
        }

        Element path = doc.createElement("path");

        if (p.opts.get("fill") != null) {
            path.setAttribute("fill", p.opts.get("fill"));
        } else {
            path.setAttribute("fill", "none");
        }

        path.setAttribute("stroke", "black");
        if (p.color != null) {
            path.setAttribute("stroke", p.color);
        }

        StringBuffer d = new StringBuffer();

        Iterator<Point> it = p.iterator();
        while (it.hasNext()) {
            Point pt = it.next();
            if (d.length() == 0) {
                d.append("M ");
            }

            d.append(Tikz.format(pt.x) + " " + Tikz.format(pt.y));
            if (it.hasNext()) {
                d.append(" L ");
            }
        }

        d.append(" z");
        path.setAttribute("d", d.toString());
        return path;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        List<Component> img = new ArrayList<Component>();
        Path path = new Path();
        path.add(new Point(0, 0));
        path.add(new Point(1, 0));
        path.add(new Point(1, 1));
        path.add(new Point(0, 1));
        path.add(new Point(0, 0));

        path = path.scale(10, 10);
        img.add(path);

        Tikz2SVG builder = new Tikz2SVG();
        Document svg = builder.createSVG(img);

        PrintWriter w = new PrintWriter(new FileWriter("/Users/chris/tikz.svg"));
        w.print(XMLUtils.toString(svg));
        w.close();
        System.out.println(XMLUtils.toString(svg));

    }

}
