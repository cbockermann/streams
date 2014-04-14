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
		
	}

}
