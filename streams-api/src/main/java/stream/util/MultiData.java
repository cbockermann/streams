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
package stream.util;

import java.io.Serializable;
import java.util.Set;

import stream.Data;
import stream.data.DataFactory;

public class MultiData {

	
	protected Data[] data;
	protected int size;

	public MultiData(int size) {
		this.size = size;
		this.data = new Data[size];
		for (int i = 0; i < size; i++) {
			data[i] = DataFactory.create();
		}
	}

	public int size() {
		return data.length;
	}

	public boolean isEmpty() {
		return data.length == 0;
	}

	public Serializable get(Object key) {
		Serializable[] result = new Serializable[data.length];
		for (int i = 0; i < data.length; i++) {
			result[i] = data[i].get(key);
		}
		return result;

	}

	// TODO
	public Serializable put(String key, Serializable value) {
		for (int i = 0; i < data.length; i++) {
			data[i].put(key, value);
		}
		return null;
	}

	// TODO
	public Serializable remove(Object key) {
		for (int i = 0; i < data.length; i++) {
			data[i].remove(key);
		}
		return null;
	}

	public Set<String> keySet() {
		return data[0].keySet();
	}

	public Data[] get() {
		return data;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("#Data=");
		sb.append(data.length);
		sb.append(" : ");
		if(data.length>0)
			sb.append(data[0].toString());
		return sb.toString();
	}


}
