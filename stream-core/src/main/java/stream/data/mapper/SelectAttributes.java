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
package stream.data.mapper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import stream.Processor;
import stream.data.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class SelectAttributes implements Processor {

	String[] keys = null;

	Set<String> selected = new HashSet<String>();
	private Boolean remove;

	public SelectAttributes() {
		super();
		this.remove = true;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
		for (String key : keys)
			selected.add(key);
	}

	public Processor setKeys(Set<String> keys) {
		this.keys = keys.toArray(new String[keys.size()]);
		selected = keys;
		return this;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setRemove(Boolean remove) {
		this.remove = remove;
	}

	public Boolean getRemove() {
		return this.remove;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		if (keys == null || keys.length == 0)
			return data;

		Iterator<String> it = data.keySet().iterator();
		if (remove) {
			while (it.hasNext()) {
				String key = it.next();
				if (!selected.contains(key)) {
					it.remove();
				}
			}
			return data;
		} else {
			Data result = DataFactory.create();
			while (it.hasNext()) {
				String key = it.next();
				if (selected.contains(key)) {
					result.put(key, data.get(key));
				}
			}
			return result;
		}
	}
}
