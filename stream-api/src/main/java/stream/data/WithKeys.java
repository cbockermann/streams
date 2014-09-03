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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessorList;
import stream.annotations.Parameter;
import stream.util.WildcardPattern;

/**
 * @author chris
 * 
 */
public class WithKeys extends ProcessorList {

	static Logger log = LoggerFactory.getLogger(WithKeys.class);
	String[] keys = null;

	Set<String> selected = new HashSet<String>();
	private Boolean merge = true;

	public WithKeys() {
		super();
		this.merge = true;
	}

	@Parameter(description = "A list of filter keys selecting the attributes that should be provided to the inner processors.")
	public void setKeys(String[] keys) {
		this.keys = keys;
		for (String key : keys)
			selected.add(key);
	}

	public String[] getKeys() {
		return keys;
	}

	public Boolean getMerge() {
		return merge;
	}

	@Parameter(description = "Indicates whether the outcome of the inner processors should be merged into the input data item, defaults to true.")
	public void setMerge(Boolean join) {
		this.merge = join;
	}

	/**
	 * @see stream.DataProcessor#process(stream.Data)
	 */
	@Override
	public Data process(Data data) {

		Data innerItem = null;

		if (keys == null || keys.length == 0) {
			innerItem = DataFactory.create();
		} else {
			innerItem = DataFactory.create();

			for (String key : data.keySet()) {
				if (isSelected(key)) {
					innerItem.put(key, data.get(key));
				}
			}
			for (String key : keys) {
				if (!innerItem.containsKey(key))
					innerItem.remove(key);
			}
		}

		Data processed = super.process(innerItem);
		if (merge != null && !merge) {
			return processed;
		}

		if (merge == null || (merge && processed != null)) {
			for (String key : processed.keySet()) {
				data.put(key, processed.get(key));
			}

			Set<String> k = data.keySet();
			Iterator<String> it = k.iterator();
			while (it.hasNext()) {
				String str = it.next();
				if (!processed.containsKey(str) && isSelected(str)) {
					it.remove();
				}
			}
		}
		return data;
	}

	private boolean isSelected(String key) {

		if (keys == null || keys.length == 0)
			return false;

		boolean included = false;

		for (String k : keys) {
			if (k.startsWith("!")) {
				k = k.substring(1);
				if (included && WildcardPattern.matches(k, key))
					included = false;
				log.debug("Removing '{}' from selection due to pattern '!{}'",
						key, k);
			} else {

				if (!included && WildcardPattern.matches(k, key)) {
					included = true;
					log.debug("Adding '{}' to selection due to pattern '{}'",
							key, k);
				}
			}
		}

		return included;
	}
}
