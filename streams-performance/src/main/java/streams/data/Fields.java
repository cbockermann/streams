/**
 * 
 */
package streams.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.LinkedHashMap;
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
import stream.data.DataImpl;
import stream.runtime.setup.ParameterInjection;

/**
 * @author chris
 *
 */
public class Fields extends ProcessorList {

    Logger log = LoggerFactory.getLogger(Fields.class);
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
            types[i] = new TypeMap(processors.get(i));
        }
    }

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {
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
        return data;
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
            proc.setAttribute("class", map.p.getClass().getName());
            Map<String, String> params = ParameterInjection.extract(map.p);
            for (String param : params.keySet()) {
                proc.setAttribute(param, params.get(param));
            }
            root.appendChild(proc);
            Element fields = doc.createElement("fields");
            proc.appendChild(fields);

            for (String field : map.keySet()) {
                String[] kv = field.split(":");
                String type = map.get(field);

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

    public class DataWrapper extends DataImpl {

        private static final long serialVersionUID = 8251825785784938452L;
        transient TypeMap types;

        public DataWrapper(Data item, TypeMap explorer) {
            super.putAll(item);
            this.types = explorer;
        }

        /**
         * @see stream.data.DataImpl#createCopy()
         */
        @Override
        public Data createCopy() {
            DataWrapper wrapper = new DataWrapper(this, types);
            return wrapper;
        }

        /**
         * @see java.util.LinkedHashMap#get(java.lang.Object)
         */
        @Override
        public Serializable get(Object key) {
            Serializable value = super.get(key);
            types.read(key.toString(), value);
            return value;
        }

        /**
         * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
         */
        @Override
        public Serializable put(String key, Serializable value) {
            types.write(key, value);
            return super.put(key, value);
        }

        /**
         * @see java.util.HashMap#putAll(java.util.Map)
         */
        @Override
        public void putAll(Map<? extends String, ? extends Serializable> m) {
            for (String k : m.keySet()) {
                put(k, m.get(k));
            }
        }

        /**
         * @see java.util.HashMap#remove(java.lang.Object)
         */
        @Override
        public Serializable remove(Object key) {
            Serializable value = super.remove(key);
            if (types != null) {
                types.remove(key.toString(), value);
            }
            return value;
        }
    }

    public static class TypeMap extends LinkedHashMap<String, String> {

        /**
         * 
         */
        private static final long serialVersionUID = 2987143663959753791L;

        Processor p;

        public TypeMap(Processor p) {
            this.p = p;
        }

        public void read(String key, Serializable value) {
            put("read:" + key, typeOf(value));
        }

        public void write(String key, Serializable value) {
            put("write:" + key, typeOf(value));

        }

        public void remove(String key, Serializable value) {
            put("remove:" + key, typeOf(value));
        }

        public String typeOf(Serializable value) {
            if (value != null) {
                if (value.getClass().isArray()) {
                    Class<?> comp = value.getClass().getComponentType();
                    return comp.getCanonicalName() + "[" + Array.getLength(value) + "]";
                } else {
                    return value.getClass().getCanonicalName();
                }
            }

            return "?";
        }
    }
}