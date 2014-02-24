/**
 * 
 */
package stream.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import stream.ComputeGraph;
import stream.Processor;
import stream.ProcessorList;
import stream.annotations.BodyContent;
import stream.io.Queue;
import stream.io.Sink;
import stream.io.Source;
import stream.io.Stream;
import stream.runtime.AbstractProcess;
import stream.util.XMLUtils;

/**
 * @author chris
 * 
 */
public class XMLFormatter {

	static Logger log = LoggerFactory.getLogger(XMLFormatter.class);

	public static String createXMLString(ComputeGraph graph) {
		try {
			return XMLUtils.toString(createXML(graph));
		} catch (Exception e) {
			e.printStackTrace();
			return "<error>failed to create XML representation from graph!</error>";
		}
	}

	public static Document createXML(ComputeGraph graph) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();

		Element container = doc.createElement("application");

		for (Object o : graph.allNodes()) {
			if (o instanceof AbstractProcess) {
				Element el = createProcessNode(graph, doc, (AbstractProcess) o);
				container.appendChild(el);
				continue;
			}

			if (o instanceof Stream) {
				container.appendChild(createStreamNode(graph, doc, (Stream) o));
				continue;
			}

			if (o instanceof Queue) {
				container.appendChild(createQueueNode(graph, doc, (Queue) o));
				continue;
			}
		}

		doc.appendChild(container);

		return doc;
	}

	private static Element createStreamNode(ComputeGraph graph, Document doc,
			Stream s) {
		Element el = doc.createElement("stream");
		Map<String, String> attr = new LinkedHashMap<String, String>();
		attr.put("id", s.getId());
		attr.putAll(getAttributes(s));
		attr.put("class", s.getClass().getCanonicalName());
		if (graph.isFinished(s)) {
			attr.put("finished", "true");
		}
		for (String key : attr.keySet()) {
			el.setAttribute(key, attr.get(key));
		}

		return el;
	}

	private static Element createQueueNode(ComputeGraph graph, Document doc,
			Queue q) {
		Element el = doc.createElement("queue");
		Map<String, String> attr = new LinkedHashMap<String, String>();
		attr.put("id", q.getId());
		attr.putAll(getAttributes(q));
		attr.put("class", q.getClass().getCanonicalName());
		if (graph.isFinished(q)) {
			attr.put("finished", "true");
		}
		for (String key : attr.keySet()) {
			el.setAttribute(key, attr.get(key));
		}
		return el;
	}

	private static Element createProcessNode(ComputeGraph graph, Document doc,
			AbstractProcess p) {
		Element proc = doc.createElement("process");

		proc.setAttribute("id", p.getId());
		Map<String, String> attr = getAttributes(p);
		for (String key : attr.keySet()) {
			proc.setAttribute(key, attr.get(key));
		}
		//
		// if (p.getInput() != null) {
		// proc.setAttribute("input", p.getInput().getId());
		// }
		//
		// if (p.getOutput() != null) {
		// proc.setAttribute("output", p.getOutput().getId());
		// }

		for (Processor pr : p.getProcessors()) {
			Element el = createProcessorNode(doc, pr);
			proc.appendChild(el);
		}

		if (graph.isFinished(p)) {
			proc.setAttribute("finished", "true");
		}

		return proc;
	}

	private static Element createProcessorNode(Document doc, Processor p) {
		Element proc = doc.createElement(p.getClass().getCanonicalName());

		Map<String, String> attr = getAttributes(p);
		for (String key : attr.keySet()) {
			proc.setAttribute(key, attr.get(key));
		}

		if (p instanceof ProcessorList) {
			ProcessorList ps = (ProcessorList) p;

			for (Processor np : ps.getProcessors()) {
				Element child = createProcessorNode(doc, np);
				proc.appendChild(child);
			}
		}

		return proc;
	}

	private static String toString(Object o) {
		if (o == null) {
			return "";
		}

		if (o.getClass().isArray()) {

			StringBuffer s = new StringBuffer();
			int len = Array.getLength(o);
			for (int i = 0; i < len; i++) {
				Object val = Array.get(o, i);
				if (val == null) {
					continue;
				}

				if (s.length() > 0) {
					s.append(",");
				}

				if (val instanceof Stream) {
					val = ((Stream) val).getId();
				}

				if (val instanceof Sink) {
					val = ((Sink) val).getId();
				}

				if (val instanceof Source) {
					val = ((Source) val).getId();
				}
				s.append(val.toString());
			}

			return s.toString();
		}
		if (o instanceof Stream) {
			return ((Stream) o).getId();
		}

		if (o instanceof Sink) {
			return ((Sink) o).getId();
		}

		if (o instanceof Source) {
			return ((Source) o).getId();
		}
		return o.toString();
	}

	private static boolean hasSetter(Object o, String name) {
		for (Method m : o.getClass().getMethods()) {
			if (m.getParameterTypes().length != 1)
				continue;

			if (m.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasBodyContentSetter(Object o, String name) {
		for (Method m : o.getClass().getMethods()) {
			if (m.getParameterTypes().length == 1
					&& m.getName().equalsIgnoreCase(name.replace("get", "set"))) {
				Class<?> type = m.getParameterTypes()[0];
				if (type.equals(BodyContent.class)) {
					return true;
				}
			}
		}

		return false;
	}

	private static Map<String, String> getAttributes(Object p) {
		Map<String, String> attr = new LinkedHashMap<String, String>();
		for (Method m : p.getClass().getMethods()) {

			if (m.getName().startsWith("get")) {

				if (!hasSetter(p, m.getName().replaceFirst("get", "set"))) {
					continue;
				}

				String name = m.getName().substring(3);
				name = Character.toLowerCase(name.charAt(0))
						+ name.substring(1);

				try {
					Object result = m.invoke(p);
					String str = toString(result);

					if (hasBodyContentSetter(p, m.getName())) {
						str = "...";
					}
					attr.put(name, str);

				} catch (Exception e) {
					log.error("No getter found for corresponding setter '{}'",
							m.getName());
				}
			}
		}

		return attr;
	}
}
