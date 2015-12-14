/**
 * 
 */
package streams.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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

import stream.Data;
import stream.ProcessContext;
import stream.Processor;
import stream.ProcessorList;
import stream.data.DataFactory;
import stream.runtime.setup.ParameterInjection;
import streams.profiler.DataWrapper;
import streams.profiler.TypeMap;

/**
 * @author chris
 *
 */
public class Fields extends ProcessorList {

    static Logger log = LoggerFactory.getLogger(Fields.class);
    String currentProcessor = "";
    TypeMap types[];

    int current = -1;
    File file;

    /**
     * @see stream.ProcessorList#init(stream.ProcessContext)
     */
    @Override
    public void init(ProcessContext context) throws Exception {
        super.init(context);

        int size = this.processors.size();
        types = new TypeMap[size];
        for (int i = 0; i < processors.size(); i++) {
            Processor p = processors.get(i);
            types[i] = new TypeMap(p);
        }
    }

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {
        log.info("Executing...");
        current = 0;
        Data data = input;

        if (data != null) {
            for (Processor p : processors) {
                data = p.process(new DataWrapper(data, types[current++]));
                // If any nested processor returns null we stop further
                // processing.
                //
                if (data == null)
                    return null;
            }

        }
        return DataFactory.create(data);
    }

    /**
     * @see stream.ProcessorList#finish()
     */
    @Override
    public void finish() throws Exception {
        super.finish();

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("processors");
        doc.appendChild(root);
        // doc.getDocumentElement().appendChild(root);

        for (TypeMap map : types) {
            Element proc = doc.createElement("processor");
            proc.setAttribute("class", map.processor().getClass().getName());
            Map<String, String> params = ParameterInjection.extract(map.processor());
            for (String param : params.keySet()) {
                proc.setAttribute(param, params.get(param));
            }
            root.appendChild(proc);
            Element fields = doc.createElement("fields");
            proc.appendChild(fields);

            for (String field : map.keySet()) {
                String[] kv = field.split(":", 2);
                String type = map.get(field);
                log.debug("Field '{}' => key = '{}'", field, kv[1]);

                Element f = doc.createElement(kv[0]);
                f.setAttribute("key", kv[1]);
                f.setAttribute("type", type);
                fields.appendChild(f);
            }
        }

        OutputStream out = System.out;
        if (file != null) {
            out = new FileOutputStream(file);
        }

        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        tf.transform(new DOMSource(doc), new StreamResult(out));
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