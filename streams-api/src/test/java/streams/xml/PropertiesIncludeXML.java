/**
 * 
 */
package streams.xml;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import stream.util.Variables;
import stream.util.XIncluder;
import stream.util.XMLUtils;

/**
 * @author chris
 *
 */
public class PropertiesIncludeXML {

    @Test
    public void test() {

        try {
            URL url = PropertiesIncludeXML.class.getResource("/xml/properties-inclusion.xml");

            Document doc = XMLUtils.parseDocument(new File(url.getFile()));
            XIncluder inc = new XIncluder();
            doc = inc.perform(doc);

            System.out.println(XMLUtils.toString(doc));

            Variables vars = XMLUtils.getProperties(doc);
            System.out.println("vars:");
            for (String k : vars.keySet()) {
                System.out.println("  '" + k + "' => '" + vars.get(k) + "'");
            }

            List<Element> ps = XMLUtils.getElementsByName(doc.getDocumentElement(), "process");
            for (Element p : ps) {
                Map<String, String> params = XMLUtils.getAttributes(p);
                System.out.println("Found process element with attributes:  " + params);

                Assert.assertTrue("123456".equals(params.get("myId")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            org.junit.Assert.fail(e.getMessage());
        }
    }

}
