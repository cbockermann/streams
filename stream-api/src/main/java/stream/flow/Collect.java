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
package stream.flow;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;
import stream.annotations.Parameter;
import stream.data.DataFactory;

/**
 * This processor simply collects a number of items, returning <code>null</code>
 * until the number is reached. When the desired number of items has been
 * collected, this processor returns a new (empty) data item, that contains an
 * array of the collected items in the attribute specified by the
 * <code>key</code> parameter.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class Collect implements Processor {

	static Logger log = LoggerFactory.getLogger(Collect.class);

	String key = "@items";

	Integer count = 1;

	List<Data> items = new ArrayList<Data>();

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	@Parameter(description = "The key (name) of the attribute into which the collection (array) of items will be put, defaults to '@items'")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	@Parameter(description = "The number of items that should be collected before the processing continues.", required = true, min = 0.0)
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (items.size() < count) {
			log.debug("Collecting next item, {} already collected.",
					items.size());
			items.add(DataFactory.create(input));
			return null;
		} else {
			log.debug("Finished with my collection, emitting the item-array in a new item.");
			Data[] vals = new Data[items.size()];
			for (int i = 0; i < vals.length; i++) {
				vals[i] = items.get(i);
			}

			Data collection = DataFactory.create();
			collection.put(key, vals);
			items.clear();
			return collection;
		}
	}
}
