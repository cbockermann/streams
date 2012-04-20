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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.expressions.ExpressionResolver;
import stream.service.LookupService;

/**
 * 
 * @author Hendrik Blom &lt;hendrik.blom@udo.edu&gt;
 * 
 */
public class Lookup extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(Lookup.class);

	protected LookupService lookup;
	protected String key;

	/**
	 * @return lookupService
	 */
	public LookupService getLookup() {
		return lookup;
	}

	/**
	 * @param lookup
	 */
	public void setLookup(LookupService lookup) {
		this.lookup = lookup;
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
	@Parameter(defaultValue = "@id")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data item) {
		if (lookup == null) {
			log.error("No LookupService injected!");
			return item;
		}
		String varib = ExpressionResolver.resolve(key, context, item)
				.toString();
		Data lookupData = lookup.lookup(varib);
		item.putAll(lookupData);
		return item;
	}
}