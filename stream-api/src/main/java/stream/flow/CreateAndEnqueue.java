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
package stream.flow;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataFactory;
import stream.expressions.ExpressionResolver;

/**
 * @author Hendrik Blom
 * 
 */
public class CreateAndEnqueue extends Enqueue {

	static Logger log = LoggerFactory.getLogger(CreateAndEnqueue.class);
	String ref = null;

	protected String[] keys;
	protected Boolean stringOnly;

	public CreateAndEnqueue() {
		super();
		keys = null;
		stringOnly = false;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String[] getKeys() {
		return keys;
	}

	public Boolean isStringOnly() {
		return stringOnly;
	}

	public void setStringOnly(Boolean stringOnly) {
		this.stringOnly = stringOnly;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (queues == null) {
			log.error("No QueueService injected!");
			return data;
		}

		Data result = DataFactory.create();
		for (String key : keys) {
			Object o = ExpressionResolver.resolve(key, context, data);
			if (o != null) {
				String[] s = ExpressionResolver.extractName(key);
				Serializable ser = create(o);
				if (ser != null)
					result.put(s[1], ser);
			}
		}
		if (!result.isEmpty()) {
			enqueue(result);
		}
		return data;
	}

	private Serializable create(Object object) {
		if (!stringOnly && isNumeric(object))
			return new Double(object.toString());
		return object.toString();
	}

	public boolean isNumeric(Object val) {

		if (val instanceof Double) {
			return true;
		}

		if (val == null)
			return false;

		try {
			new Double(val.toString());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}