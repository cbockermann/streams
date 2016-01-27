/**
 * 
 */
package streams.net;

import java.util.concurrent.LinkedBlockingQueue;

import stream.Data;
import stream.io.AbstractStream;

/**
 * @author chris
 *
 */
public class ThroughputStream extends AbstractStream {

    final LinkedBlockingQueue<Data> items = new LinkedBlockingQueue<Data>();

    String host;
    int port = 10000;
    PerfClient.Client client;

    /**
     * @see stream.io.AbstractStream#init()
     */
    @Override
    public void init() throws Exception {
        super.init();

        client = new PerfClient.Client(host, port);
        client.addListener(new DataListener() {
            @Override
            public void dataArrived(Data item) {
                items.add(item);
            }
        });

        client.setDaemon(true);
        client.start();
    }

    /**
     * @see stream.io.AbstractStream#readNext()
     */
    @Override
    public Data readNext() throws Exception {
        return items.take();
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
}