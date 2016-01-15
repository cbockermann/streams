/**
 * 
 */
package streams.runtime;

import stream.Data;
import stream.Processor;
import stream.annotations.Parameter;
import stream.annotations.Service;
import stream.service.EchoService;

/**
 * Test processor for testing the injection of values and dependencies from a
 * test .xml into a processor.
 * 
 * @author chris, kai
 *
 */
public class ProcessorWithFields implements Processor {

    @Parameter
    String key = "answer";

    @Parameter(required = true)
    Double answer;

    @Service
    EchoService service;

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {
        assert service != null;
        input.put(key, answer);
        return input;
    }
}
