/**
 * 
 */
package streams.logging;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.Data;

/**
 * 
 * @author Christian Bockermann
 * 
 */
public class Rlog {

	// static Logger log = LoggerFactory.getLogger(Rlog.class);

	String host;
	String pid;

	final Map<String, Object> defaults = new LinkedHashMap<String, Object>();

	public Rlog() {
		init();
	}

	public Rlog(String trace) {
		define("trace", trace);
		init();
	}

	public Rlog define(String key, Object value) {
		defaults.put(key, value);
		return this;
	}

	private void init() {
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			host = null;
		}

		pid = ManagementFactory.getRuntimeMXBean().getName();

		String trace = System.getProperty("rlog.trace");
		if (trace != null) {
			this.defaults.put("trace", trace);
		}
	}

	public Rlog source(Class<?> clazz) {
		defaults.put("class", clazz.getName());
		return this;
	}

	public void log(String msg) {
		message("messsage", msg).send();
	}

	public void log(String fmt, Object... vals) {
		log(format(fmt, vals));
	}

	private String format(String fmt, Object... vals) {
		StringBuffer s = new StringBuffer();
		String[] splits = fmt.split("\\{\\}");
		for (int i = 0; i < splits.length; i++) {
			s.append(splits[i]);
			if (vals != null && i < vals.length) {
				s.append(vals[i] + "");
			} else {
				if (vals == null) {
					s.append("null");
				} else {
					s.append("{}");
				}
			}
		}
		return s.toString();
	}

	public Message message() {
		return new Message(this);
	}

	public Message message(Map<String, Object> vals) {
		return message().add(vals);
	}

	public Message message(Data item) {
		Message m = message();
		m.putAll(item);
		return m;
	}

	public Message message(String key, Object o) {
		return message().add(key, o);
	}

}