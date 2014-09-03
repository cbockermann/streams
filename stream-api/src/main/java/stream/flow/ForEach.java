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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessorList;
import stream.annotations.Parameter;

/**
 * <p>
 * This processor applies a list of nested processors to each item found in a
 * collection of data items in some attribute of the current item. This
 * processor is generally used in combination with the {@link Collect}
 * processor, which creates a collection of data items of a given number.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class ForEach extends ProcessorList {

	static Logger log = LoggerFactory.getLogger(ForEach.class);
	String key;

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
	@Parameter(description = "The name of the attribute containing the collection of items that should be processed.")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.ProcessorList#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (key == null)
			return input;

		Serializable value = input.get(key);
		if (value == null)
			return input;

		ArrayList<Data> incol = new ArrayList<Data>();
		if (value instanceof Collection) {
			Iterator<?> it = ((Collection<?>) value).iterator();
			while (it.hasNext()) {
				Data item = (Data) it.next();
				incol.add(item);
			}
		} else {
			if (value.getClass().isArray()) {
				int len = Array.getLength(value);
				for (int i = 0; i < len; i++) {
					Data item = (Data) Array.get(value, i);
					if (item != null) {
						incol.add(item);
					}
				}
			} else {
				log.debug("Collection of type {} not supported!",
						value.getClass());
			}
		}

		ArrayList<Data> outcol = new ArrayList<Data>();
		for (Data item : incol) {

			item = super.process(item);
			if (item != null) {
				outcol.add(item);
			}
		}

		input.put(key, outcol.toArray(new Data[outcol.size()]));
		return input;
	}
}
