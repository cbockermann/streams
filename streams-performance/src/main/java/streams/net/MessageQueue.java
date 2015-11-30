/**
 * 
 */
package streams.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import streams.codec.Codec;
import streams.codec.DefaultCodec;
import streams.io.BobCodec;
import streams.logging.Message;
import streams.runtime.Hook;
import streams.runtime.Signals;

/**
 * @author chris
 *
 */
public class MessageQueue {

    static Logger log = LoggerFactory.getLogger(MessageQueue.class);

    final static LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<Message>();

    static List<Sender> senders = new ArrayList<Sender>();
    private static Sender sender;

    static {

        String host = System.getProperty("rlog.host");

        log.info("Initializing global MessageQueue rlog {}", host);

        if (host == null) {
            log.info("'rlog.host' not set, disabling rlog-sender");
            sender = null;
        } else {
            // TODO use default port number
            sender = new Sender(host, 6001, messages);
            log.info("rlog.host={} using port={}", host, 6001);
            sender.setDaemon(true);
            sender.start();
        }

        Signals.register(new Shutdown());
    }

    /**
     * Add a message to a queue of messages to be sent to performance receiver.
     *
     * @param m
     *            Message with performance statistics
     */
    public static void add(Message m) {
        if (sender != null) {
            messages.offer(m);
        }
    }

    public static class Sender extends Thread {

        final Codec<Data> mc = new DefaultCodec<Data>();
        private final int port;

        DataOutputStream out;
        final String host;
        BufferedReader in;
        final LinkedBlockingQueue<Message> messages;
        boolean running = false;

        /**
         * Create sender thread to be able to connect to performance receiver.
         * This constructor uses host address given as parameter.
         *
         * @param host
         *            String value of host address
         */
        public Sender(String host, int port) {
            this(host, port, new LinkedBlockingQueue<Message>());
        }

        /**
         * Create sender thread to be able to connect to performance receiver.
         * This constructor uses host address given as parameter.
         *
         * @param host
         *            String value of host address
         * @param messages
         *            linked blocking queue of messages to be sent
         */
        public Sender(String host, int port, LinkedBlockingQueue<Message> messages) {
            this.host = host;
            this.port = port;
            this.messages = messages;
            this.setDaemon(true);
        }

        /**
         * Connect to the specified host and port.
         *
         * @return Socket connection
         */
        protected boolean connect() {
            Socket socket;
            try {
                socket = SecureConnect.connect(host, port);
            } catch (Exception e) {
                log.error("Connection could have not been build to {}:{}\nError message: {}", host, port, e.toString());
                return false;
            }
            try {
                out = new DataOutputStream(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                log.error("Error while creating output and input readers using " + "socket connection: {}",
                        e.toString());
                return false;
            }
            return true;
        }

        public void run() {
            running = true;
            if (!senders.contains(this)) {
                senders.add(this);
            }

            while (running || !messages.isEmpty()) {
                try {
                    Message m = messages.take();
                    if (m != null) {
                        send(m);
                    }
                    log.debug("Sending message " + m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void send(Message m) {
            try {
                if (out == null || in == null) {
                    if (!connect()) {
                        log.error("Connection could not have been established.");
                        return;
                    }
                }

                byte[] bytes = mc.encode(DataFactory.create(m));
                log.debug("Encoded message to " + bytes.length + " bytes");
                int written = BobCodec.writeBlock(bytes, out);
                log.debug(written + " bytes written to socket...");
                out.flush();

            } catch (Exception e) {
                log.error("Error while writing message to output stream: {}", e.toString());
            }
        }

        public int messagesPending() {
            return messages.size();
        }

        public void add(Message m) {
            this.messages.add(m);
        }
    }

    /**
     * Shutdown thread that is used as ShutdownHook. In this case we wait for
     * senders to finish their message queues.
     */
    public static class Shutdown extends Thread implements Hook {
        public void run() {
            signal(Signals.SHUTDOWN);
        }

        /**
         * @see streams.runtime.Hook#signal(int)
         */
        @Override
        public void signal(int flags) {
            if (flags == Signals.SHUTDOWN) {
                log.info("Shutting down message queue...");
                for (Sender sender : senders) {
                    sender.running = false;

                    while (sender != null && !messages.isEmpty()) {
                        try {
                            log.info("Waiting for sender to finish ({} messages pending)...", messages.size());
                            sender.join(1000);
                        } catch (Exception e) {
                            log.error("Waiting for sender was interrupted: " + e);
                        }
                    }
                }
            }
        }
    }
}
