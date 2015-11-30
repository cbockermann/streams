/**
 * 
 */
package streams.codec;

import static org.junit.Assert.fail;

import org.junit.Test;

import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 *
 */
public class KryoTest {

    @Test
    public void test() {

        Data item = DataFactory.create();
        item.put("message", "Hello, world!");

        Codec<Data> kryo = new Kryo();

        try {
            byte[] data = kryo.encode(item);

            Data out = kryo.decode(data);

            if (!item.get("message").toString().equals(out.get("message").toString())) {
                fail("Data mis-match after decoding!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }

}
