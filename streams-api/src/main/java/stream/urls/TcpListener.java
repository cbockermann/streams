/**
 * 
 */
package stream.urls;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.SourceURL;
import streams.runtime.Hook;
import streams.runtime.Signals;

/**
 * @author chris
 * 
 */
public class TcpListener extends Connection {

    static Logger log = LoggerFactory.getLogger(TcpListener.class);
    int port;
    TcpInputStream inputStream;

    /**
     * @param url
     */
    public TcpListener(SourceURL url) {
        super(url);
        port = url.getPort();

        try {
            inputStream = new TcpInputStream(port);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        Signals.register(new Hook() {
            @Override
            public void signal(int flags) {
                log.info("Closing tcp conneection...");
            }
        });
    }

    /**
     * 
     * @see stream.urls.Connection#getSupportedProtocols()
     */
    @Override
    public String[] getSupportedProtocols() {
        return new String[] { "tcpd" };
    }

    /**
     * @see stream.urls.Connection#connect()
     */
    @Override
    public InputStream connect() throws IOException {
        return inputStream;
    }

    /**
     * @see stream.urls.Connection#disconnect()
     */
    @Override
    public void disconnect() throws IOException {
        inputStream.socket.close();
    }

    public class TcpInputStream extends InputStream {

        Logger log = LoggerFactory.getLogger(TcpInputStream.class);
        InputStream in = null;
        ServerSocket socket;

        public TcpInputStream(int port) throws Exception {
            socket = new ServerSocket(port);
        }

        /**
         * @see java.io.InputStream#read()
         */
        @Override
        public int read() throws IOException {

            if (in == null) {
                log.info("Waiting for client to connect...");
                Socket client = socket.accept();
                log.info("Client connected:  {}", client.getRemoteSocketAddress());
                in = client.getInputStream();
            }

            return in.read();
        }
    }
}