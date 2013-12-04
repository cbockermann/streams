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

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ConditionedProcessor;
import stream.Context;
import stream.Data;
import stream.ProcessContext;
import stream.annotations.Description;
import stream.expressions.version2.Expression;
import stream.expressions.version2.SerializableExpression;

/**
 * <p>
 * This class implements a processor to copy a set of values from one context to
 * the other.
 * </p>
 * 
 * @author Hendrik Blom
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Data")
public class CopyValues extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(CopyValues.class);

	protected String[] keys;
	protected Expression<Serializable>[] expressions;
	protected String sourceCtx;
	protected String targetCtx;

	public CopyValues() {
		super();
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String getSourceCtx() {
		return sourceCtx;
	}

	public void setSourceCtx(String sourceCtx) {
		this.sourceCtx = sourceCtx;
	}

	public String getTargetCtx() {
		return targetCtx;
	}

	public void setTargetCtx(String targetCtx) {
		this.targetCtx = targetCtx;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		if (keys == null)
			throw new IllegalArgumentException("Keys are not set!");
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			Expression<Serializable> e = null;
			if (Context.DATA_CONTEXT_NAME.equals(sourceCtx))
				e = new SerializableExpression("%{" + Context.DATA_CONTEXT_NAME
						+ "." + key + "}");
			if (Context.PROCESS_CONTEXT_NAME.equals(sourceCtx))
				e = new SerializableExpression("%{" + Context.DATA_CONTEXT_NAME
						+ "." + key + "}");
			if (e != null)
				expressions[i] = e;
		}
	}

	/**
     * 
     */
	@Override
	public Data processMatchingData(Data data) {

		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				Expression<Serializable> e = expressions[i];
				if (e == null)
					continue;
				Serializable val = null;
				try {
					val = e.get(context, data);
				} catch (Exception e1) {
					e1.printStackTrace();
					continue;
				}
				if (val == null)
					continue;
				if (Context.DATA_CONTEXT_NAME.equals(targetCtx))
					data.put(key, val);
				if (Context.PROCESS_CONTEXT_NAME.equals(targetCtx))
					context.set(key, val);
			}

		}

		return data;
	}
}