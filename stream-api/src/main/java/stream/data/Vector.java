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

/**
 * 
 * @author chris
 * 
 */
public interface Vector {

	/**
	 * This method returns a sorted array of vector indices.
	 * 
	 * @return
	 */
	public int[] indexes();

	/**
	 * This method returns an array of values that has the same size as the
	 * index array returned by {@link #indexes()}. The value at entry
	 * <code>i</code> corresponds to the vector component at
	 * <code>getIndexes()[i]</code>.
	 * 
	 * @return
	 */
	public double[] getValues();

	/**
	 * Returns the
	 * 
	 * @param idx
	 * @return
	 */
	public Double getValue(int idx);

	public void setValue(int idx, Double d);
}
