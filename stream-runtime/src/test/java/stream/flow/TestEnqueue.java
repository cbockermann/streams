/**
 * 
 */
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.Queue;

/**
 * @author chris
 *
 */
public class TestEnqueue extends Enqueue {

    static Logger log = LoggerFactory.getLogger(TestEnqueue.class);
    long emitted = 0L;

    public void setQueue(Queue queue) {
        super.setSink(queue);
    }

    public void setQueues(Queue[] queues) {
        super.setSinks(queues);
    }

    /**
     * @see stream.flow.Emitter#emit(stream.Data)
     */
    @Override
    protected int emit(Data data) {
        int written = super.emit(data);
        emitted += written;
        return written;
    }

    /**
     * @see stream.flow.Emitter#emit(stream.Data[])
     */
    @Override
    protected int emit(Data[] data) {
        int written = super.emit(data);
        emitted += written;
        return written;
    }

    /**
     * @see stream.AbstractProcessor#finish()
     */
    @Override
    public void finish() throws Exception {
        super.finish();
        log.info("Wrote {} items to queues: {}", emitted, sinks);
    }
}
