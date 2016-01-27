/**
 * 
 */
package stream.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author chris
 *
 */
public class IntervalTest {

    /**
     * Test method for {@link stream.util.Interval#contains(double)}.
     */
    @Test
    public void testContains() {

        Interval i = new Interval("(30,40)");

        Assert.assertTrue(i.contains(35));
        Assert.assertFalse(i.contains(30.0));
        Assert.assertFalse(i.contains(29.9));

        Assert.assertFalse(i.contains(40));
        Assert.assertFalse(i.contains(40.1));

        i = new Interval("[30,40]");
        Assert.assertTrue(i.contains(30.0));
    }
}
