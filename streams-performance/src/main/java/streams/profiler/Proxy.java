/**
 * 
 */
package streams.profiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.Processor;
import stream.StatefulProcessor;
import stream.data.DataFactory;

/**
 * @author chris
 *
 */

public class Proxy implements StatefulProcessor {

    static Logger log = LoggerFactory.getLogger(Proxy.class);
    final Processor delegate;

    Long items = 0L;
    Long nanos = 0L;
    final TypeMap map;

    public Proxy(Processor p) {
        this.delegate = p;
        map = new TypeMap(delegate);
    }

    public TypeMap types() {
        return map;
    }

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {
        items++;
        Data data = new DataWrapper(input, map);

        // log.trace("processing delegate {} ({}) (items processed: {})",
        // delegate, input, items);
        final long t0 = System.nanoTime();
        final Data result = delegate.process(data);
        final long t1 = System.nanoTime();
        nanos += t1 - t0;
        if (result == null) {
            return null;
        }

        return DataFactory.create(result);
    }

    /**
     * @see stream.StatefulProcessor#init(stream.ProcessContext)
     */
    @Override
    public void init(ProcessContext context) throws Exception {
        if (delegate instanceof StatefulProcessor) {
            ((StatefulProcessor) delegate).init(context);
        }
    }

    /**
     * @see stream.StatefulProcessor#resetState()
     */
    @Override
    public void resetState() throws Exception {
        if (delegate instanceof StatefulProcessor) {
            ((StatefulProcessor) delegate).resetState();
        }
    }

    /**
     * @see stream.StatefulProcessor#finish()
     */
    @Override
    public void finish() throws Exception {
        if (delegate instanceof StatefulProcessor) {
            ((StatefulProcessor) delegate).finish();
        }
    }
}