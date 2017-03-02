/**
 * 
 */
package streams.test;

import stream.AbstractProcessor;
import stream.Data;

/**
 * @author chris
 *
 */
public class ProduceErrorAfterNitems extends AbstractProcessor {

    int count = 0;
    Integer n = 10;

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data item) {
        count++;

        if (count == n) {
            throw new RuntimeException("Synthetic error initiated on purpose!");
        }

        return item;
    }
}
