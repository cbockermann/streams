/**
 * 
 */
package streams.net;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 *
 */
// TODO do we need this class?
public class PerfClient {

    static Logger log = LoggerFactory.getLogger(PerfClient.class);

    final static int messageSize = new Integer(System.getProperty("message.size", "100"));
    final static int every = new Integer(System.getProperty("every", "10000"));
    final static int clients = new Integer(System.getProperty("clients", "1"));

    final static AtomicLong bytes = new AtomicLong(0L);
    final static AtomicLong packets = new AtomicLong(0L);

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        String addr = System.getProperty("address", args[0]);
        int port = new Integer(System.getProperty("port", "10000"));

        log.info("Connecting to server at {}", addr);
        log.info("Using {} parallel connection(s).", clients);

        final List<Client> c = new ArrayList<Client>();

        for (int i = 0; i < clients; i++) {
            Client client = new Client(addr, port);
            log.info("Client {} connected", i);
            c.add(client);
        }

        log.info("Starting all clients...");
        for (Client client : c) {
            client.start();
        }

        log.info("Waiting for all clients to finish...");
        for (Client client : c) {
            client.join();
        }
    }

    public static class Client extends Thread {

        final List<DataListener> listener = new ArrayList<DataListener>();
        Socket socket;

        long start = 0L;
        long end = 0L;
        long bytesRead = 0L;
        final BufferedInputStream in;
        final DataOutputStream dos;

        public Client(String addr, int port) throws Exception {
            socket = new Socket(addr, port);
            in = new BufferedInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        }

        public void run() {

            start = System.currentTimeMillis();
            try {

                byte[] buf = new byte[messageSize];

                boolean eos = false;
                int read = 0;
                do {
                    read = readFully(buf, in);

                    eos = (buf[0] == 0xe) && (buf[1] == 0x0) && (buf[2] == 0xf);

                    long pkts = PerfClient.packets.incrementAndGet();
                    Long totalBytes = PerfClient.bytes.addAndGet(read);

                    if (read >= 0) {
                        bytesRead += read;
                    }

                    if ((pkts / clients) % every == 0) {
                        long now = System.currentTimeMillis();
                        Long duration = (now - start);
                        Double seconds = duration.doubleValue() / 1000.0;
                        Double gbit = (totalBytes.doubleValue() * 8) / 1000.0 / 1000.0 / 1000.0;
                        Double mbit = (totalBytes.doubleValue() * 8) / 1000.0 / 1000.0;
                        Double mbyte = (totalBytes.doubleValue()) / 1024.0 / 1024.0;

                        if (listener.isEmpty()) {
                            log.info("total data rate ({} clients) is {} GBit/sec => {} MB/sec", clients,
                                    gbit / seconds, mbyte / seconds);
                        }

                        for (DataListener l : listener) {
                            Data measure = DataFactory.create();
                            measure.put("@timestamp", now);
                            measure.put("mbit/s", mbit / seconds);
                            measure.put("mb/s", mbyte / seconds);
                            l.dataArrived(measure);
                        }
                    }

                } while (!eos && read > 0);

                dos.writeLong(bytesRead);
                dos.flush();
                dos.close();
                in.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            end = System.currentTimeMillis();
            Long duration = (end - start);
            Double seconds = duration.doubleValue() / 1000.0;

            log.info("{} bytes read in {} seconds", bytesRead, (end - start) / seconds);
            log.info("data rate is {} GBit/sec", (8 * bytesRead / 1000.0 / 1000.0 / 1000.0) / seconds);
        }

        public int readFully(byte[] buf, BufferedInputStream in) throws Exception {
            int off = in.read(buf, 0, buf.length);

            while (off < buf.length) {
                int read = in.read(buf, off, buf.length - off);
                if (read >= 0) {
                    off += read;
                }

                if (read < 0) {
                    return -1;
                }
            }
            // log.info("Read {} bytes", off);
            return off;
        }

        public void addListener(DataListener l) {
            listener.add(l);
        }
    }

}
