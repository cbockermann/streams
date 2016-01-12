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

import org.junit.Assert;
import org.junit.Test;

import stream.CopiesUtils;
import stream.Copy;

public class CopiesUtilsTest {

	@Test
	public void baseTest() {
		String copiesString = "8";
		Copy[] copies = CopiesUtils.parse(copiesString);
		Assert.assertEquals(8, copies.length);

		copiesString = "[1]";
		copies = CopiesUtils.parse(copiesString);
		Assert.assertEquals(1, copies.length);

		copiesString = "1,2,3,4";
		copies = CopiesUtils.parse(copiesString);
		Assert.assertEquals(4, copies.length);

		copiesString = "blah:[8]:[8]:blah";
		copies = CopiesUtils.parse(copiesString);
		Assert.assertEquals(64, copies.length);

		copiesString = "[8]:[8]:blah";
		copies = CopiesUtils.parse(copiesString);
		Assert.assertEquals(64, copies.length);

		copiesString = "blah:[8]:[8]";
		copies = CopiesUtils.parse(copiesString);
		Assert.assertEquals(64, copies.length);

		copiesString = "[8]:[8]";
		copies = CopiesUtils.parse(copiesString);
		Assert.assertEquals(64, copies.length);

		copiesString = "[8]:[8]:[2]";
		copies = CopiesUtils.parse(copiesString);
		Assert.assertEquals(128, copies.length);

		copiesString = "[1,2,3,4]:[1,2,3,4]:[1,2,3,4]";
		copies = CopiesUtils.parse(copiesString);
		Assert.assertEquals(64, copies.length);

		copiesString = "[1,2,3,4]:[1,2,3,4]:[0]";
		copies = CopiesUtils.parse(copiesString);
		Assert.assertNull(copies);

		copiesString = "[1,2,3,4]:[0]:[1,2,3,4]";
		copies = CopiesUtils.parse(copiesString);
		Assert.assertNull(copies);

		copiesString = "[0]:[1,2,3,4]:[1,2,3,4]";
		copies = CopiesUtils.parse(copiesString);
		Assert.assertNull(copies);

	}

}
