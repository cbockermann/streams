/**
 * 
 */
package streams.logging;

import java.util.LinkedHashMap;
import java.util.Map;

import streams.net.MessageQueue;

/**
 * @author chris
 *
 */

public class Message extends LinkedHashMap<String, Object> {
	/** The unique class ID */
	private static final long serialVersionUID = 2193782766563687626L;
	final Rlog rlog;

	public Message() {
		this.rlog = null;
	}

	public Message(Rlog rlog) {
		this.rlog = rlog;
		if (rlog != null) {
			this.putAll(rlog.defaults);
		}
	}

	public Message add(String key, Object o) {
		if (o != null) {
			put(key, o);
		}
		return this;
	}

	public Message add(Map<String, ?> map) {
		if (map != null) {
			this.putAll(map);
		}
		return this;
	}

	public void send() {
		MessageQueue.add(this);
	}
}
