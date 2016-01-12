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
package stream.expressions.version2;

import stream.Context;
import stream.Data;

/**
 * @author Hendrik Blom
 * 
 */
public class EqualsDoubleCondition extends OperatorCondition<Double> {

	private final static long EXP_BIT_MASK = 0x7FF0000000000000L;
	private final static long SIGNIF_BIT_MASK = 0x000FFFFFFFFFFFFFL;

	public EqualsDoubleCondition(Expression<Double> left,
			Expression<Double> right) {
		super(left, right, "==d");
	}

	@Override
	public Boolean get(Context ctx, Data item) throws Exception {
		Double l = left.get(ctx, item);
		if (l == null)
			return null;
		Double r = right.get(ctx, item);
		if (r == null)
			return null;
		return (doubleToLongBits(l.doubleValue()) == doubleToLongBits(r
				.doubleValue()));
	}

	public Class<Boolean> type() {
		return Boolean.class;
	}

	private static long doubleToLongBits(double value) {
		long result = Double.doubleToRawLongBits(value);
		// Check for NaN based on values of bit fields, maximum
		// exponent and nonzero significand.
		if (((result & EXP_BIT_MASK) == EXP_BIT_MASK)
				&& (result & SIGNIF_BIT_MASK) != 0L)
			result = 0x7ff8000000000000L;
		return result;
	}

}