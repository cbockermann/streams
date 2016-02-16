/**
 * 
 */
package stream.annotations;

import static org.junit.Assert.fail;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.setup.ParameterValidator;

/**
 * @author Christian Bockermann
 *
 */
public class ParameterAnnotationsTest {

    static Logger log = LoggerFactory.getLogger(ParameterAnnotationsTest.class);

    @Test
    public void test() {

        Map<String, Object> vals = new LinkedHashMap<String, Object>();
        vals.put("nonoptional", "value");

        AnnotatedProcessor proc = new AnnotatedProcessor();

        try {
            ParameterValidator.check(proc, vals);
        } catch (ParameterException e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidProcessor() {
        Map<String, Object> vals = new LinkedHashMap<String, Object>();

        ParameterException expected = null;
        AnnotatedProcessorWithDoubleAnnotations proc = new AnnotatedProcessorWithDoubleAnnotations();

        try {
            ParameterValidator.check(proc, vals);
        } catch (ParameterException e) {
            expected = e;
        }

        if (expected == null) {
            fail("Test failed: expected 'ParameterException' not thrown!");
        }
    }

    @Test
    public void testMissingRequiredParam() {
        ParameterException expected = null;

        try {
            URL url = ParameterAnnotationsTest.class.getResource("/annotations/missing-required-parameter.xml");
            stream.run.main(url);
        } catch (ParameterException p) {
            expected = p;
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }

        if (expected == null) {
            fail("Test failed: expected 'ParameterException' not thrown!");
        }
    }
}
