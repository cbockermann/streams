/**
 * 
 */
package streams.profiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import stream.Processor;
import stream.runtime.ApplicationContext;
import stream.runtime.DefaultProcess;
import stream.runtime.setup.ParameterInjection;
import streams.profiler.ProxyInjection.ProxyNode;

/**
 * @author chris
 *
 */
public class Process extends DefaultProcess {

    static Logger log = LoggerFactory.getLogger(Process.class);
    final List<ProxyNode> proxies = new ArrayList<ProxyNode>();

    File file;

    /**
     * @see stream.runtime.AbstractProcess#init(stream.runtime.ApplicationContext)
     */
    @Override
    public void init(ApplicationContext context) throws Exception {
        super.init(context);

        ProxyInjection injection = new ProxyInjection();

        for (int i = 0; i < processors.size(); i++) {
            Processor p = processors.get(i);
            ProxyNode node = injection.inject(p);
            processors.set(i, node);
            proxies.add(node);
        }
    }

    /**
     * @see stream.runtime.AbstractProcess#finish()
     */
    @Override
    public void finish() throws Exception {
        log.info("Finishing process...");
        super.finish();

        String xml = this.createXMLOutput();
        PrintStream out = System.out;
        if (file != null) {
            log.info("Writing profiling information to {}", file);
            out = new PrintStream(new FileOutputStream(file));
        }

        out.print(xml);
        out.flush();
    }

    public String createXMLOutput() throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.newDocument();

        Element app = doc.createElement("application");
        app.setAttribute("id", this.parentContext.getId());
        doc.appendChild(app);

        Element perfs = doc.createElement("process");

        perfs.setAttribute("id", this.getId());
        perfs.setAttribute("input", this.getInput().getId());

        for (ProxyNode proxy : proxies) {
            Element p = createElements(proxy, doc);
            perfs.appendChild(p);
        }
        app.appendChild(perfs);

        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        StringWriter writer = new StringWriter();

        tf.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    protected Element createElements(ProxyNode proxy, Document doc) throws Exception {

        String name = proxy.delegate.getClass().getName();
        Element p = doc.createElement(name);
        // p.setAttribute("class", proxy.node.delegate.getClass().getName());
        Map<String, String> params = ParameterInjection.extract(proxy.delegate);
        for (String k : params.keySet()) {
            p.setAttribute(k, params.get(k));
        }

        Element perf = doc.createElement("performance");
        perf.setAttribute("items", proxy.items + "");
        perf.setAttribute("nanos", proxy.nanos + "");

        DecimalFormat fmt = new DecimalFormat("0.00");
        Double costs = (proxy.nanos * 1.0) / (1.0 * proxy.items);
        perf.setAttribute("costs", fmt.format(costs) + " ns/item");

        Element fields = doc.createElement("fields");
        p.appendChild(fields);

        for (String field : proxy.types().keySet()) {
            String[] kv = field.split(":", 2);
            String type = proxy.types().get(field);
            log.debug("Field '{}' => key = '{}'", field, kv[1]);

            Element f = doc.createElement(kv[0]);
            f.setAttribute("key", kv[1]);
            f.setAttribute("type", type);
            fields.appendChild(f);
        }

        p.appendChild(perf);

        if (!proxy.children.isEmpty()) {
            for (ProxyNode ch : proxy.children) {
                Element node = createElements(ch, doc);
                p.appendChild(node);
            }
        }

        return p;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file
     *            the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }
}