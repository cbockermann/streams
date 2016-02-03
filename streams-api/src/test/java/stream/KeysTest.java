/**
 * 
 */
package stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

/**
 * @author chris
 * 
 */
public class KeysTest {

    static Logger log = LoggerFactory.getLogger(KeysTest.class);

    public final List<String> split(String s) {
        final ArrayList<String> list = new ArrayList<String>();
        String[] ks = s.split(",");

        for (String k : ks) {
            if (k.trim().isEmpty()) {
                continue;
            }
            list.add(k.trim());
        }
        return list;
    }

    /**
     * Test method for {@link stream.Keys#Keys(java.lang.String)}.
     */
    @Test
    public void testKeysString() {
        Keys keys = new Keys("A,B,C*");

        Set<String> selected = keys.select(split("A,B,C1,C2,D"));
        Assert.assertEquals(4, selected.size());
    }

    /**
     * Test method for {@link stream.Keys#Keys(java.lang.String[])}.
     */
    @Test
    public void testKeysStringArray() {
        Keys keys = new Keys("A,B,C*".split(","));

        Set<String> selected = keys.select(split("A,B,C1,C2,D"));
        Assert.assertEquals(4, selected.size());
    }

    /**
     * Test method for {@link stream.Keys#select(java.util.Set)}.
     */
    @Test
    public void testSelectSetOfString() {

        List<String> features = split(
                "frame:size,frame:data,pixel:max,pixel:min,pixel:center,avg:red,avg:green,avg:blue");

        Keys keys = new Keys("frame:*,avg:*,!avg:red");

        Set<String> selected = keys.select(features);

        log.info("Full set is: {}", features);
        log.info("Keys instance is: {}", keys);
        log.info("Selected by keys: {}", selected);

        Assert.assertTrue(selected.contains("frame:size"));
        Assert.assertTrue(selected.contains("frame:data"));
        Assert.assertFalse(selected.contains("pixel:max"));
        Assert.assertFalse(selected.contains("pixel:min"));
        Assert.assertFalse(selected.contains("pixel:center"));

        Assert.assertFalse(selected.contains("avg:red"));
        Assert.assertTrue(selected.contains("avg:green"));
        Assert.assertTrue(selected.contains("avg:blue"));
    }

    @Test
    public void testRegex() {

        Keys keys = new Keys("*,!/pixel:\\d+/");
        List<String> features = split("frame:size,frame:data,pixel:0,pixel:1,pixel:2,pixel:3,avg:blue");

        Set<String> selected = keys.select(features);

        log.info("Feature list:  {}", features);
        log.info("Keys pattern:  '{}'", keys);
        log.info("Features selected with regex pattern:  {}", selected);

        Assert.assertTrue(selected.contains("frame:size"));
        Assert.assertTrue(selected.contains("frame:data"));
        Assert.assertTrue(selected.contains("avg:blue"));

        Assert.assertFalse(selected.contains("pixel:0"));
        Assert.assertFalse(selected.contains("pixel:1"));
        Assert.assertFalse(selected.contains("pixel:2"));
        Assert.assertFalse(selected.contains("pixel:3"));
    }
}
