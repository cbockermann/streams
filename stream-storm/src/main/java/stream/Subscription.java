/**
 * 
 */
package stream;

import java.io.Serializable;

import stream.util.MD5;

/**
 * <p>
 * This class represents a simple source<->subscriber connection.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public class Subscription implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 8854045123111020160L;
	private final String source;
	private final String subscriber;

	public Subscription(String subscriber, String source) {
		this.source = source;
		this.subscriber = subscriber;
	}

	public String source() {
		return source;
	}

	public String subscriber() {
		return subscriber;
	}

	public String id() {
		return MD5.md5(source + "<->" + subscriber);
	}

	public String toString() {
		return "Subscription[" + subscriber + " ~> " + source + "]";
	}

	public int hashCode() {
		return id().hashCode();
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (o instanceof Subscription) {
			Subscription other = (Subscription) o;
			return id().equals(other.id());
		}

		return false;
	}
}