/**
 * 
 */
package streams.io;

import java.util.concurrent.atomic.AtomicLong;

import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractStream;

/**
 * @author chris
 *
 */
public class TestStream extends AbstractStream {

    final AtomicLong counter = new AtomicLong(0L);

    public TestStream() {
    }

    /**
     * @see stream.io.AbstractStream#readNext()
     */
    @Override
    public Data readNext() throws Exception {
        Data item = DataFactory.create();
        item.put("@id", new Long(counter.incrementAndGet()));
        item.put("value", Math.random());
        return item;
    }
}