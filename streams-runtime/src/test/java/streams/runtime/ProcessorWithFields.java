/**
 * 
 */
package streams.runtime;

import stream.Data;
import stream.Processor;
import stream.annotations.Parameter;

/**
 * @author chris
 *
 */
public class ProcessorWithFields implements Processor {

    @Parameter
    String key = "answer";

    @Parameter(required = true)
    Double answer;

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {
        input.put(key, answer);
        return input;
    }
}
