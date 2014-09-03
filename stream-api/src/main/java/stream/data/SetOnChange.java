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

import stream.ProcessContext;
import stream.flow.OnChange;

/**
 * @author Hendrik Blom
 *
 */
public class SetOnChange extends OnChange {

	private SetValue sv;

	String setKey;
	String[] scope;
	String value;

	public SetOnChange() {
		this.sv = new SetValue();
	}

	public String getSetKey() {
		return sv.getKey();
	}

	public void setSetKey(String setKey) {
		sv.setKey(setKey);
	}

	public String[] getScope() {
		return sv.getScope();
	}

	public void setScope(String[] scope) {
		sv.setScope(scope);
	}

	public String getValue() {
		return sv.getValue();
	}

	public void setValue(String value) {
		sv.setValue(value);
	}

	@Override
	public void init(ProcessContext context) throws Exception {
		super.getProcessors().add(sv);
		super.init(context);
	}

}
