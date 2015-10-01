/**
 * 
 */
package streams.performance;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minidev.json.JSONObject;
import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class Rlog {

	public final static String DEFAULT_URL = "https://performance.sfb876.de/rlog/receiver";

	static Logger log = LoggerFactory.getLogger(Rlog.class);
	final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String sessionId;
	final String baseUrl;
	final String host;
	final String className;
	final String pid;

	String tag = null;

	// PrintStream out;
	// Socket socket;
	final LinkedBlockingQueue<String> msgs = new LinkedBlockingQueue<String>();

	public Rlog() {
		baseUrl = System.getProperty("rlog.url", DEFAULT_URL);
		sessionId = System.getProperty("rlog.trace", UUID.randomUUID().toString());
		String hostname = null;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			hostname = null;
		}
		this.host = hostname;
		className = null;

		pid = ManagementFactory.getRuntimeMXBean().getName();
	}

	public Rlog(Class<?> clazz) {
		baseUrl = System.getProperty("rlog.url", DEFAULT_URL);

		className = clazz.getName();
		this.sessionId = className;
		String hostname = null;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			hostname = null;
		}
		this.host = hostname;
		pid = ManagementFactory.getRuntimeMXBean().getName();
	}

	public Rlog(String sid) {
		baseUrl = System.getProperty("rlog.url", DEFAULT_URL + "/" + sid);

		className = null;
		this.sessionId = sid;
		String hostname = null;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			hostname = null;
		}
		this.host = hostname;
		pid = ManagementFactory.getRuntimeMXBean().getName();
	}

	public Rlog tag(String tag) {
		this.tag = tag;
		return this;
	}

	public Rlog session(String sessId) {
		this.sessionId = sessId + "";
		return this;
	}

	public void log(String msg) {
		send(msg);
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

	public void send(String msg) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("message", msg);
		send(m);
	}

	public void send(Map<String, ?> msg) {
		long time = System.currentTimeMillis();
		String json = "";
		try {
			Map<String, Object> m = new LinkedHashMap<String, Object>();
			m.put("time", time);

			if (host != null)
				m.put("host", host);

			if (pid != null) {
				m.put("pid", pid);
			}

			if (className != null)
				m.put("class", className);

			if (tag != null) {
				m.put("@tag", tag);
			}

			for (String k : msg.keySet()) {
				m.put(k, msg.get(k));
			}
			json = JSONObject.toJSONString(m);
		} catch (Exception e) {
			e.printStackTrace();
		}

		RlogSender.send(time, sessionId, json);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// System.setProperty("javax.net.debug", "ssl=handshake");
		System.setProperty("rlog.url", DEFAULT_URL + "/test");
		// System.setProperty("rlog.url", "http://localhost:8001/rlog/test");

		Rlog rlog = new Rlog("cb");
		String out = rlog.format("test {}", "A");
		System.out.println("test:    " + out);
		rlog.send("Dies ist ein test!");

		Data map = DataFactory.create();
		map.put("@timestamp", System.nanoTime());
		new Rlog("cb").send(map);

	}
}