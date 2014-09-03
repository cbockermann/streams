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
import stream.Data;
import stream.ProcessContext;

/**
 * @author Hendrik Blom
 * 
 */
public class CheckDataAvailability extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(CheckDataAvailability.class);

	protected String[] keys;
	protected String scope;

	public CheckDataAvailability() {
		super();
		scope = "data";
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public Data process(Data input) {
		StringBuilder sb = new StringBuilder();
		boolean complete = true;

		if (scope.equals("data")) {
			for (String key : keys) {
				if (input.get(key) == null) {
					sb.append(key);
					sb.append("\n");
					complete = false;
				}
			}

		}
		if (scope.equals("process")) {
			for (String key : keys) {
				if (context.get(key) == null) {
					sb.append(key);
					sb.append("\n");
					complete = false;
				}
			}

		}
		if (!complete) {
			log.info("Not all data with the defined keys are aavailable. Missing keys:\n"
					+ sb.toString());
			input.put("dataavailable", false);
		} else {
			log.info("All data are available.");
			input.put("dataavailable", true);

		}
		return input;
	}
}