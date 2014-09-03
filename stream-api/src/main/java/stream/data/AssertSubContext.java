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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Context;
import stream.Data;
import stream.ProcessContext;

public class AssertSubContext extends AbstractProcessor {

	Logger log = LoggerFactory.getLogger(AssertSubContext.class);
	private String[] keys;
	private String ctx;

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String getContext() {
		return ctx;
	}

	public void setContext(String ctx) {
		this.ctx = ctx;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		if (keys == null)
			keys = new String[0];
	}

	@Override
	public Data process(Data data) {
		if (Context.PROCESS_CONTEXT_NAME.equals(ctx))
			for (String key : keys) {
				if (context.get(key) == null) {
					data.put("@subContext:complete", false);
					// log.info("Key: {} is missing", key);
					return data;
				}
			}

		if (Context.DATA_CONTEXT_NAME.equals(ctx))
			for (String key : keys) {
				if (data.get(key) == null) {
					data.put("@subContext:complete", false);
					return data;
				}
			}

		data.put("@subContext:complete", true);
		return data;
	}
}
