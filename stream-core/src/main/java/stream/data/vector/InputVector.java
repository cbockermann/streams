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
package stream.data.vector;

import java.util.HashMap;

public class InputVector extends Vector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1868992010251953558L;

	double y;
	
	public void setLabel(double y) {
		this.y = y;
	}
	
	public double getLabel() {
		return y;
	}
	
	public InputVector() {
		super();
	}
		
	/**
	 * Creates a dense vector from the given values
	 * 
	 * @param vals
	 * @param copy
	 * @param y
	 */
	public InputVector(double[] vals, boolean copy, double y) {
		super(vals, copy);
		this.y = y;
	}
	
	/**
	 * Creates a sparse vector from the given values
	 * 
	 * @param pairs
	 * @param copy
	 * @param y
	 */
	public InputVector(HashMap<Integer,Double> pairs, boolean copy, double y) {
		super(pairs, copy);
		this.y = y;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param x
	 */
	public InputVector(InputVector x) {
		super(x);
	}
}
