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
package stream;


/**
 * <p>
 * A processor is a simple function that acts on a piece of data. This interface
 * defines the most basic form, simply providing a {@link #process(Data)}
 * method. A processor implementing this interface may keep a state over
 * multiple calls to the {@link #process(Data)} method.
 * </p>
 * <p>
 * Processor implementations that require initialization should implement the
 * {@link StatefulProcessor} interface, which implements a 3-phase lifecycle
 * (init, process and finish).
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface Processor {

	/**
	 * This is the main method for processing items. This method is called
	 * numerous times - once for each incoming data item.
	 * 
	 * @param input
	 * @return
	 */
	public Data process(Data input);

}
