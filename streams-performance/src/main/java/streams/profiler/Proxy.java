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
 * @author Christian Bockermann
 *
 */
public class Proxy implements StatefulProcessor {

    static Logger log = LoggerFactory.getLogger(Proxy.class);
    final Processor delegate;

    Long items = 0L;
    Long nanos = 0L;
    final TypeMap map;
    final boolean trackTime;
    final boolean trackKeys;

    public Proxy(Processor p) {
        this(p, true, true);
    }

    public Proxy(Processor p, boolean recordTime, boolean recordKeys) {
        this.delegate = p;
        this.trackTime = recordTime;
        this.trackKeys = recordKeys;
        log.debug("processor: {}, tracking keys? {}", p, trackKeys);
        this.map = new TypeMap(delegate);
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
        Data data = input;

        if (trackKeys)
            data = wrap(input);

        final Data result;

        if (trackTime) {
            final long t0 = System.nanoTime();
            result = delegate.process(data);
            final long t1 = System.nanoTime();
            nanos += t1 - t0;
        } else {
            result = delegate.process(data);
        }

        if (result == null) {
            return null;
        }

        if (trackKeys)
            return unwrap(result);
        else
            return result;
    }

    public Data wrap(Data item) {
        log.debug("Wrapping item to record attribute accesses");
        return new DataWrapper(item, map);
    }

    public Data unwrap(Data item) {
        return DataFactory.create(item);
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