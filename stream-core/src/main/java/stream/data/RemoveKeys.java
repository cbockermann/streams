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
package stream.data;

import stream.Processor;
import stream.annotations.Description;
import stream.data.Data;

/**
 * This class implements a data-processor that removes a bunch of keys from each
 * processed data item. Keys can be specified as a list:
 * 
 * <pre>
 *    &lt;RemoveAttributes keys="a,b,c" /&gt;
 * </pre>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Attributes")
public class RemoveKeys implements Processor {

	String[] keys = new String[0];

	public RemoveKeys() {
	}

	public RemoveKeys(String[] keys) {
		setKeys(keys);
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String[] getKeys() {
		return keys;
	}

	@Override
	public Data process(Data data) {

		if (keys == null)
			return data;

		for (String key : keys)
			data.remove(key);
		return data;
	}
}