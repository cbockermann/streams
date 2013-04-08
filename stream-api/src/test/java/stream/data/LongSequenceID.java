/**
 * 
 */
package stream.data;

import java.io.Serializable;

/**
 * @author chris
 * 
 */
public class LongSequenceID implements Serializable, Comparable<LongSequenceID> {

	/** The unique class ID */
	private static final long serialVersionUID = -5395865079744504092L;

	final static Long OVERFLOW = Long.MAX_VALUE;
	private Integer digits = 1;
	private long[] value = new long[1];

	public LongSequenceID() {
		value = new long[] { 0L };
		digits = value.length;
	}

	private LongSequenceID(long[] value) {
		this.value = value;
		this.value = new long[value.length];
		for (int i = 0; i < value.length; i++) {
			this.value[i] = value[i];
		}
		digits = this.value.length;
	}

	public LongSequenceID increment() {
		return nextValue();
	}

	public synchronized LongSequenceID nextValue() {

		for (int d = 0; d < value.length; d++) {

			if (value[d] + 1 < OVERFLOW) {
				value[d]++;
				return new LongSequenceID(value);
			} else {

				value[d] = 0L;
				if (d + 1 == value.length) {

					// append a new digit '0' to the counters...
					long[] nv = new long[digits + 1];
					for (int i = 0; i < digits; i++) {
						nv[i] = 0L;
					}

					value = nv;
					value[digits] = 1L; // the value of the newly created
										// digit...
					digits = value.length;
					return new LongSequenceID(value);
				}
			}
		}

		value[0]++;
		return new LongSequenceID(value);

		//
		// if (value[0] + 1 < OVERFLOW) {
		// value[0]++;
		// return new SequenceID(value);
		// } else {
		// //
		// // first digit is about to overflow...
		// //
		//
		// // append a new digit '0' to the counters...
		// Long[] nv = new Long[digits + 1];
		// for (int i = 0; i < digits; i++) {
		// nv[i + 1] = value[i];
		// }
		// nv[0] = 0L;
		//
		// value = nv;
		// digits = nv.length;
		// return new SequenceID(value);
		// }

		// Long[] next = new Long[] { value[0] + 1 };
		// return new SequenceID(next);
	}

	public LongSequenceID advance(Long steps) {

		for (long l = 0; l < steps - 1; l++) {
			nextValue();
		}

		return nextValue();
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(LongSequenceID other) {

		if (this == other)
			return 0;

		if (this.value.length == other.value.length) {

			for (int i = 0; i < value.length; i++) {

				if (value[i] != other.value[i]) {
					if (value[i] < other.value[i])
						return -1;
					else
						return 1;
				}
			}

		} else {
			return this.digits.compareTo(other.digits);
		}

		return 0;
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = digits; i > 0; i--) {
			b.append(value[i - 1]);
			if (i > 1)
				b.append(".");
		}
		return b.toString();
	}

	public static LongSequenceID parseValue(String str) {
		String[] tok = str.split("\\.");
		long[] digits = new long[tok.length];
		for (int i = tok.length; i >= 0; i--) {
			digits[tok.length - i] = Long.parseLong(tok[i]);
		}

		return new LongSequenceID(digits);
	}
}
