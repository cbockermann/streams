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

import stream.AbstractProcessor;
import stream.Context;
import stream.Data;
import stream.ProcessContext;
import stream.annotations.Description;
import stream.expressions.version2.Condition;
import stream.expressions.version2.ConditionFactory;
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
public class SimpleCopyValues extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(SimpleCopyValues.class);

	protected String[] keys;
	protected String sourceCtx;
	protected String targetCtx;
	protected String[] notEqual;

	public SimpleCopyValues() {
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

	public String[] getNotEqual() {
		return notEqual;
	}

	public void setNotEqual(String[] notEqual) {
		this.notEqual = notEqual;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);

		if (keys == null || sourceCtx == null || targetCtx == null)
			throw new IllegalArgumentException(
					"Keys, sourceCtx and targetCtx must be specified!");
	}

	@Override
	public Data process(Data data) {
		if (keys != null) {
			if (sourceCtx.equals(Context.PROCESS_CONTEXT_NAME)
					&& targetCtx.equals(Context.DATA_CONTEXT_NAME))
				return copyFromProcessCtx(data);

			if (sourceCtx.equals(Context.DATA_CONTEXT_NAME)
					&& targetCtx.equals(Context.PROCESS_CONTEXT_NAME)) {
				copyToProcessCtx(data);
				return data;
			}

			if (sourceCtx.equals(Context.PROCESS_CONTEXT_NAME)
					&& targetCtx.equals(Context.COPY_CONTEXT_NAME)) {
				Data copyCtx = DataFactory.create();
				return copyFromProcessCtx(copyCtx);
			}
		}
		return data;
	}

	private Data copyFromProcessCtx(Data data) {
		for (String key : keys) {
			Serializable s = (Serializable) this.context.get(key);
			s = getValue(s);
			if (s == null)
				continue;
			data.put(key, s);
		}
		return data;
	}

	private void copyToProcessCtx(Data data) {

		if (keys != null) {
			for (String key : keys) {
				Serializable s = data.get(key);
				s = getValue(s);
				if (s == null)
					continue;
				this.context.set(key, s);
			}
		}
	}

	private Serializable getValue(Serializable s) {
		if (s == null)
			return s;

		if (notEqual != null) {
			String st = s.toString();
			for (String neq : notEqual) {
				if (st.equals(neq)) {
					return null;
				}
			}
		}
		return s;
	}

}