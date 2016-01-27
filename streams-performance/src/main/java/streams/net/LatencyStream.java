/**
 * 
 */
package streams.net;

import java.util.concurrent.LinkedBlockingQueue;

import stream.Data;
import stream.annotations.Parameter;
import stream.io.AbstractStream;

/**
 * @author chris
 *
 */
public class LatencyStream extends AbstractStream {

    LinkedBlockingQueue<Data> items = new LinkedBlockingQueue<Data>();
    LatencyClient client;

    @Parameter
    String host;

    @Parameter
    int port = 10001;

    @Parameter
    Integer interval = 10000;

    /**
     * @see stream.io.AbstractStream#init()
     */
    @Override
    public void init() throws Exception {
        super.init();
        client = new LatencyClient(host, port);
        client.setDaemon(true);
        client.addListener(new DataListener() {
            @Override
            public void dataArrived(Data item) {
                items.add(item);
            }
        });
        client.interval(interval);
        client.start();
    }

    /**
     * @see stream.io.AbstractStream#readNext()
     */
    @Override
    public Data readNext() throws Exception {
        return items.take();
    }
}