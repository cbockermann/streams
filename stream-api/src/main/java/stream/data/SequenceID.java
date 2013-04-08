/**
 * 
 */
package stream.data;

import java.io.Serializable;
import java.util.UUID;

/**
 * This class implements a sequence element which is internally represented as a
 * chunk of bytes (byte[] array) with ascending significance.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public final class SequenceID implements Serializable, Comparable<SequenceID> {

	/** The unique class ID */
	private static final long serialVersionUID = -5395865079744504092L;

	final static int OVERFLOW = 256; // Byte.MAX_VALUE;
	private Integer digits = 1;
	private byte[] value = new byte[1];

	public SequenceID() {
		this(8);
	}

	public SequenceID(int bytes) {
		value = new byte[bytes];
		for (int i = 0; i < value.length; i++) {
			value[i] = 0;
		}

		digits = value.length;
	}

	public SequenceID(byte[] value) {
		this.value = value;
		this.value = new byte[value.length];
		for (int i = 0; i < value.length; i++) {
			this.value[i] = value[i];
		}
		digits = this.value.length;
	}

	public synchronized SequenceID nextValue() {
		return new SequenceID(value).increment();
	}

	public SequenceID increment() {
		synchronized (this) {
			for (int d = 0; d < value.length; d++) {

				if (((int) value[d] & 0xff) + 1 < OVERFLOW) {
					value[d]++;
					return this;
				} else {

					value[d] = 0;
					if (d + 1 == value.length) {

						// append a new digit '0' to the counters...
						byte[] nv = new byte[digits + 1];
						for (int i = 0; i < digits; i++) {
							nv[i] = 0;
						}

						value = nv;
						value[digits] = 1; // the value of the newly created
											// digit...
						digits = value.length;
					}
				}
			}

			value[0]++;
		}

		return this;
	}

	public byte[] value() {

		synchronized (this) {
			byte[] vals = new byte[value.length];
			for (int i = 0; i < vals.length; i++) {
				vals[i] = value[i];
			}
			return vals;
		}
	}

	public UUID uuid() {
		return UUID.nameUUIDFromBytes(value);
	}

	public SequenceID advance(byte steps) {

		for (byte l = 0; l < steps - 1; l++) {
			nextValue();
		}

		return nextValue();
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SequenceID other) {

		if (this == other)
			return 0;

		if (this.value.length == other.value.length) {

			for (int i = 0; i < value.length; i++) {

				if (value[i] < other.value[i])
					return -1;

				if (value[i] > other.value[i])
					return 1;
			}

		} else {
			return this.digits.compareTo(other.digits);
		}

		return 0;
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		boolean foundNonZero = false;
		for (int i = digits; i > 0; i--) {
			int val = ((int) value[i - 1]) & 0xff;
			foundNonZero = foundNonZero || val > 0;
			b.append(Integer.toHexString(val));
			if (i > 1)
				b.append(":");
		}
		return b.toString();
	}

	public static SequenceID parseValue(String str) {
		String[] tok = str.split("\\.");
		byte[] digits = new byte[tok.length];
		for (int i = tok.length; i >= 0; i--) {
			digits[tok.length - i] = Byte.parseByte(tok[i]);
		}

		return new SequenceID(digits);
	}
}
