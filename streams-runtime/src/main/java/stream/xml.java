/**
 * 
 */
package stream;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import stream.util.XIncluder;
import stream.util.XMLUtils;

/**
 * @author chris
 *
 */
public class xml {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.err.println("Usage:\n\tstream.xml CONFIG_XML [OUTPUT-XML]\n");
            System.exit(-1);
        }

        Document doc = XMLUtils.parseDocument(new File(args[0]));

        XIncluder includer = new XIncluder();
        doc = includer.perform(doc);

        TransformerFactory tf = TransformerFactory.newInstance();
        // tf.setAttribute("indent-number", new Integer(4));
        Transformer trans = tf.newTransformer();
        trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        trans.setOutputProperty(OutputKeys.METHOD, "xml");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        Source source = new DOMSource(doc);
        StringWriter out = new StringWriter();
        Result output = new StreamResult(out);
        trans.transform(source, output);

        String xml = out.toString();

        if (args.length > 1) {
            FileWriter writer = new FileWriter(args[1]);
            writer.write(xml);
            writer.close();
        } else {
            System.out.println(xml);
        }
    }
}
