/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
package stream.learner.evaluation;

import java.io.Serializable;

/**
 * @author chris,Hendrik Blom
 * 
 */
public class ZeroOneLoss<T extends Serializable> implements LossFunction<T> {
	/* The loss function used to assess the prediction error */
	@Override
	public double loss(T x1, T x2) {
		if (x1 == x2 || x1.toString().equals(x2.toString())) {
			return 0.0d;
		} else
			return 1.0d;
		/*
		 * 
		 * if( x1 instanceof Double && x2 instanceof Double ){ Double d1 =
		 * (Double) x1; Double d2 = (Double) x2; return Math.abs(d1 - d2); }
		 * else {
		 * 
		 * if( !x1.toString().equals( x2 + "" ) ){ return 1.0d; }
		 * 
		 * }
		 * 
		 * if( x1.equals( x2 ) ) return 0.0d;
		 * 
		 * return 1.0d;
		 */
	}
}
