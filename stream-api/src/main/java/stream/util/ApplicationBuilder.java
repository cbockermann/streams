/**
 * 
 */
package stream.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.Context;
import stream.Data;
import stream.Processor;
import stream.app.ComputeGraph;
import stream.io.AbstractStream;
import stream.io.Sink;
import stream.io.Source;
import stream.io.SourceURL;

/**
 * @author chris
 * 
 */
public class ApplicationBuilder {

	static Logger log = LoggerFactory.getLogger(ApplicationBuilder.class);

	public static ComputeGraph parseGraph(SourceURL url) throws Exception {
		Document doc = XMLUtils.parseDocument(url.openStream());
		return parseGraph(doc);
	}

	public static ComputeGraph parseGraph(Document doc) throws Exception {

		ComputeGraph graph = new ComputeGraph();

		NodeList streams = doc.getElementsByTagName("stream");
		for (int i = 0; i < streams.getLength(); i++) {

			Element stream = (Element) streams.item(i);
			String id = stream.getAttribute("id");

			StreamNode sn = new StreamNode();
			sn.putAll(XMLUtils.getAttributes(stream));
			graph.addStream(id, sn);
		}

		NodeList procs = doc.getElementsByTagName("process");
		for (int i = 0; i < procs.getLength(); i++) {
			Element proc = (Element) procs.item(i);
			String id = proc.getAttribute("id");
			if (id == null) {
				id = UUID.randomUUID().toString();
			}

			ProcessNode process = new ProcessNode();
			graph.addProcess(id, process);

			String input = proc.getAttribute("input");
			if (input == null) {
				throw new RuntimeException("Process '" + id
						+ "' is not connected to any input!");
			}

			Source src = graph.sources().get(input);
			if (src == null) {
				throw new RuntimeException("Process '" + id
						+ "' references unknown input '" + input + "'!");
			}

			Object last = src;

			NodeList inner = proc.getChildNodes();
			for (int j = 0; j < inner.getLength(); j++) {
				Node n = inner.item(j);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) n;

					ProcessorNode p = new ProcessorNode();
					p.put("class", el.getNodeName());

					Map<String, String> params = XMLUtils.getAttributes(el);
					for (String key : params.keySet()) {
						p.put(key, params.get(key));
					}
					graph.add(p);

					graph.add(last, p);

					process.add(p);
					last = p;
				}
			}

			// graph.add(src, process);
		}

		return graph;
	}

	public static class StreamNode extends AbstractStream implements
			stream.util.Node {
		final Map<String, String> attributes = new LinkedHashMap<String, String>();

		/**
		 * @see stream.io.AbstractStream#readNext()
		 */
		@Override
		public Data readNext() throws Exception {
			return null;
		}

		/**
		 * @param key
		 * @return
		 * @see java.util.Map#get(java.lang.Object)
		 */
		public String get(Object key) {
			return attributes.get(key);
		}

		/**
		 * @param key
		 * @param value
		 * @return
		 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
		 */
		public String put(String key, String value) {
			return attributes.put(key, value);
		}

		/**
		 * @return
		 * @see java.util.Map#keySet()
		 */
		public Set<String> keySet() {
			return attributes.keySet();
		}

		/**
		 * @param m
		 * @see java.util.Map#putAll(java.util.Map)
		 */
		public void putAll(Map<? extends String, ? extends String> m) {
			attributes.putAll(m);
		}

		/**
		 * @see stream.util.Node#set(java.lang.String, java.lang.String)
		 */
		@Override
		public stream.util.Node set(String key, String value) {
			if (value == null)
				this.attributes.remove(key);
			else
				this.attributes.put(key, value);
			return this;
		}

		/**
		 * @see stream.util.Node#attributes()
		 */
		@Override
		public Map<String, String> attributes() {
			return Collections.unmodifiableMap(attributes);
		}
	}

	public static class ProcessorNode implements Processor, stream.util.Node {
		final Map<String, String> attributes = new LinkedHashMap<String, String>();

		/**
		 * @see stream.Processor#process(stream.Data)
		 */
		@Override
		public Data process(Data input) {
			return input;
		}

		/**
		 * @param key
		 * @return
		 * @see java.util.Map#get(java.lang.Object)
		 */
		public String get(Object key) {
			return attributes.get(key);
		}

		/**
		 * @param key
		 * @param value
		 * @return
		 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
		 */
		public String put(String key, String value) {
			return attributes.put(key, value);
		}

		/**
		 * @return
		 * @see java.util.Map#keySet()
		 */
		public Set<String> keySet() {
			return attributes.keySet();
		}

		public Map<String, String> attributes() {
			return Collections.unmodifiableMap(attributes);
		}

		/**
		 * @see stream.util.Node#set(java.lang.String, java.lang.String)
		 */
		@Override
		public stream.util.Node set(String key, String value) {
			if (value == null)
				this.attributes.remove(key);
			else
				this.attributes.put(key, value);
			return this;
		}
	}

	public static class ProcessNode implements stream.Process, stream.util.Node {

		final ArrayList<Processor> processors = new ArrayList<Processor>();
		final Map<String, String> properties = new LinkedHashMap<String, String>();
		Source input;
		Sink output;

		/**
		 * @see stream.runtime.LifeCycle#init(stream.Context)
		 */
		@Override
		public void init(Context context) throws Exception {
		}

		/**
		 * @see stream.runtime.LifeCycle#finish()
		 */
		@Override
		public void finish() throws Exception {
		}

		/**
		 * @see stream.Process#setInput(stream.io.Source)
		 */
		@Override
		public void setInput(Source ds) {
			this.input = ds;
		}

		/**
		 * @see stream.Process#getInput()
		 */
		@Override
		public Source getInput() {
			return input;
		}

		/**
		 * @see stream.Process#setOutput(stream.io.Sink)
		 */
		@Override
		public void setOutput(Sink sink) {
			this.output = sink;
		}

		/**
		 * @see stream.Process#getOutput()
		 */
		@Override
		public Sink getOutput() {
			return output;
		}

		/**
		 * @see stream.Process#add(stream.Processor)
		 */
		@Override
		public void add(Processor p) {
			processors.add(p);
		}

		/**
		 * @see stream.Process#remove(stream.Processor)
		 */
		@Override
		public void remove(Processor p) {
			processors.remove(p);
		}

		/**
		 * @see stream.Process#getProcessors()
		 */
		@Override
		public List<Processor> getProcessors() {
			return processors;
		}

		/**
		 * @see stream.Process#execute()
		 */
		@Override
		public void execute() throws Exception {
		}

		/**
		 * @see stream.Process#getProperties()
		 */
		@Override
		public Map<String, String> getProperties() {
			return properties;
		}

		public Map<String, String> attributes() {
			return Collections.unmodifiableMap(properties);
		}

		/**
		 * @see stream.util.Node#set(java.lang.String, java.lang.String)
		 */
		@Override
		public stream.util.Node set(String key, String value) {
			if (value == null) {
				properties.remove(key);
			} else {
				properties.put(key, value);
			}
			return this;
		}

	}
}
