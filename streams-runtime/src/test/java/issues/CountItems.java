/**
 * 
 */
package issues;

import java.util.concurrent.atomic.AtomicInteger;

import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Parameter;

/**
 * @author chris
 *
 */
public class CountItems extends AbstractProcessor {

    final static AtomicInteger countProcessors = new AtomicInteger(0);
    final static AtomicInteger finishCalls = new AtomicInteger(0);

    final AtomicInteger count = new AtomicInteger(0);

    @Parameter
    String id = "";

    public CountItems() {
        countProcessors.incrementAndGet();
    }

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data item) {
        if (item != null) {
            count.incrementAndGet();
        }
        return item;
    }

    /**
     * @see stream.AbstractProcessor#finish()
     */
    @Override
    public void finish() throws Exception {
        super.finish();
        System.out.println("#### " + id + " ==> " + count.get() + " items counted");

        int finishes = finishCalls.incrementAndGet();
        System.out.println(finishes + " times 'finish' was called for " + countProcessors.get()
                + " processors of class 'issues.CountItems'");
    }

    public static int numberOfProcessors() {
        return countProcessors.get();
    }

    public static int numberOfFinishCalls() {
        return finishCalls.get();
    }
}