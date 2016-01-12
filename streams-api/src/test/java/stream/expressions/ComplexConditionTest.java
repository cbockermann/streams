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
package stream.expressions;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Context;
import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class ComplexConditionTest {

	static Logger log = LoggerFactory.getLogger(ComplexConditionTest.class);

	// An empty dummy-context
	final Context ctx = new Context() {

		// @Override
		// public <T extends Service> T lookup(String ref, Class<T>
		// serviceClass)
		// throws Exception {
		// return null;
		// }

		@Override
		public Object resolve(String variable) {
			return null;
		}

		@Override
		public boolean contains(String key) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Context getParent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String path() {
			return "context";
		}
	};

	/**
	 * 
	 */
	@Test
	public void test() {

		try {
			String cond = "( %{data.@flight} != 39 ) OR ( %{data.@flight} == 39 and %{data.time} < 1040 )";

			Data item = DataFactory.create();
			item.put("@flight", "39");

			Expression exp = ExpressionCompiler.parse(cond);
			log.info("condition:\t{}", exp);
			boolean match = exp.matches(ctx, item);
			Assert.assertFalse(match);

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}