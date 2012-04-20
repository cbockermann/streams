/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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

import stream.data.Data;

/**
 * A processor is a simple class that follows a 3-step lifecycle. The lifecycle
 * starts with <code>init()</code>, after which several calls to the
 * <code>process</code> method may follow.
 * 
 * At the end of the lifecycle, the <code>finish()</code> method is called to
 * release and open connections or the like.
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
