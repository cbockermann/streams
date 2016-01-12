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
package stream.data.storage;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;

/**
 * @author Hendrik Blom
 * 
 * @param <T>
 */
public abstract class Store<T extends Serializable> extends AbstractProcessor
		implements DataService<T> {
	static Logger log = LoggerFactory.getLogger(Store.class);
	protected String[] keys;
	protected Integer capacity;
	protected Map<String, T> data;

	public Store() {
		capacity = 2000;
	}

	@Override
	public T getData(String key) {
		return data.get(key);
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		data = new ConcurrentHashMap<String, T>(capacity);
	}

	protected abstract void addData(String[] keys, Data item);

	@Override
	public Data process(Data input) {
		if (input != null) {
			addData(keys, input);
		}
		return input;
	}

	@Override
	public void reset() throws Exception {
		data.clear();
	}

}