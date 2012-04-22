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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.data.Data;

public class NGrams extends AbstractProcessor {
	static Logger log = LoggerFactory.getLogger(NGrams.class);
	String key = null;
	Integer n = 3;

	@Override
	public Data process(Data data) {

		if (key != null && n != null && n >= 0) {

			Map<String, Double> counts = new LinkedHashMap<String, Double>();

			Serializable val = data.get(key);
			if (val != null) {

				String str = val.toString();
				for (int i = 0; i < str.length() - n; i++) {
					String ngram = str.substring(i, i + n);

					Double freq = counts.get(ngram);
					if (freq != null) {
						freq = freq + 1.0d;
					} else {
						freq = 1.0d;
					}
					counts.put(ngram, freq);
				}

				for (String key : counts.keySet()) {
					data.put(key, counts.get(key));
				}

				log.debug("Added {} {}-grams to item", counts.size(), n);
			}
		}

		return data;
	}

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
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the n
	 */
	public Integer getN() {
		return n;
	}

	/**
	 * @param n
	 *            the n to set
	 */
	public void setN(Integer n) {
		this.n = n;
	}
}