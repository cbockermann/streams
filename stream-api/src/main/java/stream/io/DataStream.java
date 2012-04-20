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
package stream.io;

import java.util.List;
import java.util.Map;

import stream.Processor;
import stream.data.Data;

/**
 * <p>
 * A simple data stream interface, producing data items.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public interface DataStream {

	/**
	 * This method returns a mapping of attributes to types.
	 * 
	 * @return
	 */
	public Map<String, Class<?>> getAttributes();

	public void init() throws Exception;

	/**
	 * Returns the next datum from this stream.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Data readNext() throws Exception;

	public Data readNext(Data datum) throws Exception;

	public void close();

	public void addPreprocessor(Processor proc);

	public void addPreprocessor(int idx, Processor proc);

	public List<Processor> getPreprocessors();
}