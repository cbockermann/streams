/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
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
