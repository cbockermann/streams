/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ProcessorList;
import stream.util.WildcardPattern;

/**
 * @author chris
 * 
 */
public class WithKeys extends ProcessorList {

	static Logger log = LoggerFactory.getLogger(WithKeys.class);
	String[] keys = null;

	Set<String> selected = new HashSet<String>();
	private Boolean remove;

	public WithKeys() {
		super();
		this.remove = true;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
		for (String key : keys)
			selected.add(key);
	}

	/*
	 * public void setKeys(Set<String> keys) { this.keys = keys.toArray(new
	 * String[keys.size()]); selected = keys; }
	 */

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

		Data result = DataFactory.create();
		for (String key : data.keySet()) {
			if (isSelected(key)) {
				result.put(key, data.get(key));
			}
		}

		Data processed = super.process(result);
		for (String key : processed.keySet()) {
			data.put(key, processed.get(key));
		}
		return data;
	}

	public boolean isSelected(String key) {

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
