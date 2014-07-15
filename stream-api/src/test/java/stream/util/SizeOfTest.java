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

/**
 * @author chris
 * 
 */
public class SizeOfTest {

	/**
	 * Test method for {@link stream.util.SizeOf#sizeOf(java.lang.Object)}.
	 */
	@Test
	public void testSizeOf() {
		Assert.assertTrue(2.0 == SizeOf.sizeOf('c'));
	}

	@Test
	public void testSizeOfArray() {
		int len = 1024;
		int[] array = new int[len];
		Assert.assertTrue(4 * len == SizeOf.sizeOf(array));
	}

	@Test
	public void testSizeOfString() {
		Assert.assertTrue(4.0 == SizeOf.sizeOf("ABCD"));
	}

}
