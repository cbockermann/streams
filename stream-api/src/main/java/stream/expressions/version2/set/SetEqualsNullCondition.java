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
package stream.expressions.version2.set;

import java.io.Serializable;
import java.util.Iterator;

import stream.Context;
import stream.Data;
import stream.expressions.version2.Condition;
import stream.expressions.version2.Expression;

/**
 * <p>
 * </p>
 * 
 * @author Hendrik Blom
 * 
 */
public class SetEqualsNullCondition extends Condition {

	public SetEqualsNullCondition(String e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Boolean get(Context ctx, Data item) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
