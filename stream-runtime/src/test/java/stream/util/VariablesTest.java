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
package stream.util;

import junit.framework.Assert;

import org.junit.Test;

public class VariablesTest {

	@Test
	public void baseTest() {
		// test1
		// <properties>
		// <property

		Variables v = new Variables();
		v.set("p1", "result");
		Assert.assertEquals("result", v.expand("${p1}"));

		v = new Variables();
		v.set("p1", "result");
		try {
			v.expand("${p2}");
			Assert.fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// no-op (pass)
		}

		// Hierachical Parameter
		v = new Variables();
		v.set("p1.v2_v5", "result");
		v.set("p3.v4", "v5");
		v.set("p2", "v2");
		v.set("p4", "v4");
		Assert.assertEquals("result", v.expand("${p1.${p2}_${p3.${p4}}}"));
		Assert.assertEquals("blah_result",
				v.expand("blah_${p1.${p2}_${p3.${p4}}}"));
		Assert.assertEquals("result_blah",
				v.expand("${p1.${p2}_${p3.${p4}}}_blah"));
		Assert.assertEquals("blah_result_blah",
				v.expand("blah_${p1.${p2}_${p3.${p4}}}_blah"));
	}

}
