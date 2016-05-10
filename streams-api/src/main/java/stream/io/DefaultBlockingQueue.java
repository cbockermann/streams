/**
 * 
 */
package stream.io;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 *
 */
public class DefaultBlockingQueue implements Queue {

    static Logger log = LoggerFactory.getLogger(DefaultBlockingQueue.class);

    final Data END_OF_STREAM = DataFactory.create();

    String id;
    final AtomicBoolean closed = new AtomicBoolean(false);
    final LinkedBlockingQueue<Data> queue = new LinkedBlockingQueue<Data>();

    Object lock = new Object();

    /**
     * @see stream.io.Barrel#clear()
     */
    @Override
    public int clear() {
        synchronized (queue) {
            int size = queue.size();
            queue.clear();
            return size;
        }
    }

    /**
     * @see stream.io.Sink#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @see stream.io.Sink#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @see stream.io.Sink#init()
     */
    @Override
    public void init() throws Exception {
    }

    /**
     * @see stream.io.Sink#write(stream.Data)
     */
    @Override
    public boolean write(Data item) throws Exception {
        log.trace("Receiving item {}", item);
        synchronized (closed) {
            if (closed.get()) {
                log.error("Attempt to write into closed queue '{}'!", id);
                return false;
            }
            log.trace("Adding data item to queue");
            boolean added = queue.add(item);
            if (added) {
                closed.notifyAll();
            } else {
                log.error("Failed to add item to queue: {}", item);
            }
            return added;
        }
    }

    /**
     * @see stream.io.Sink#write(java.util.Collection)
     */
    @Override
    public boolean write(Collection<Data> data) throws Exception {
        synchronized (closed) {
            log.trace("Writing {} data items into queue", data.size());
            if (closed.get()) {
                log.error("Attempt to write into closed queue '{}'!", id);
                return false;
            }
            return queue.addAll(data);
        }
    }

    /**
     * @see stream.io.Sink#close()
     */
    @Override
    public void close() throws Exception {
        log.debug("Closing queue {}", this.toString());
        // Thread.dumpStack();

        synchronized (closed) {
            closed.set(true);
            queue.add(END_OF_STREAM);
            closed.notifyAll();
        }
    }

    /**
     * @see stream.io.Source#read()
     */
    @Override
    public Data read() throws Exception {
        synchronized (closed) {
            if (closed.get() && queue.isEmpty()) {
                log.debug("queue '{}' closed, read() => null", this.getId());
                return null;
            }

            while (queue.isEmpty()) {
                log.trace("Waiting for new data to arrive {}", Thread.currentThread());
                closed.wait();

                if (closed.get()) {
                    log.debug("queue '{}' closed, read() => null", this.getId());
                    return null;
                }
            }

            log.trace("calling .take() on queue[closed={}] with {} elements", closed.get(), queue.size());
            Data item = queue.take();
            if (item == END_OF_STREAM) {
                log.debug("Found EOF!");
                return null;
            }
            return item;
        }
    }

    /**
     * @see stream.io.Queue#setCapacity(java.lang.Integer)
     */
    @Override
    public void setCapacity(Integer limit) {

    }

    /**
     * @see stream.io.Queue#getSize()
     */
    @Override
    public Integer getSize() {
        return queue.size();
    }

    /**
     * @see stream.io.Queue#getCapacity()
     */
    @Override
    public Integer getCapacity() {
        return Integer.MAX_VALUE;
    }

    public String toString() {
        return super.toString() + "#['" + getId() + "']";
    }
}
