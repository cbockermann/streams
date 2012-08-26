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
package stream.parser;

import stream.AbstractProcessor;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;

/**
 * @author chris
 * 
 */
@Description(group = "Streams.Processing.Transformations.Parsers", text = "Parses a double value from a string and replaces the attribute string value with the double object.")
public class ParseDouble extends AbstractProcessor {

	String[] keys = null;
	Double defaultValue = null;

	/**
	 * @return the keys
	 */
	public String[] getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	@Parameter(required = true, description = "The keys/attributes to perform parsing on")
	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	/**
	 * @return the defaultValue
	 */
	public Double getDefault() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	@Parameter(required = false, defaultValue = "0.0", description = "The default value to set if parsing fails")
	public void setDefault(Double defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		String[] ks = keys;
		if (ks == null) {
			ks = data.keySet().toArray(new String[data.keySet().size()]);
		}

		for (String key : ks) {
			Double value = defaultValue;
			try {
				value = new Double(data.get(key) + "");
				data.put(key, value);
			} catch (Exception e) {
				if (defaultValue != null)
					data.put(key, defaultValue);
			}
		}

		return data;
	}
}