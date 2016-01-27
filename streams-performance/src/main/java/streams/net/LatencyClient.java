/**
 * 
 */
package streams.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 *
 */
public class LatencyClient extends Thread {

    static Logger log = LoggerFactory.getLogger(LatencyClient.class);

    final List<DataListener> listener = new ArrayList<DataListener>();
    final InetAddress dest;
    final String host;
    final int port;
    Integer probes = 10;

    final DatagramSocket client;
    Integer interval = 1000;

    public LatencyClient(String host, int port) throws Exception {
        this.host = host;
        this.port = port;

        dest = InetAddress.getByName(host);

        client = new DatagramSocket();
        client.setSoTimeout(100);
        client.setTrafficClass(0x10);
    }

    /**
     * @return the interval
     */
    public Integer interval() {
        return interval;
    }

    /**
     * @param interval
     *            the interval to set
     */
    public LatencyClient interval(Integer interval) {
        this.interval = interval;
        return this;
    }

    public void run() {

        final byte[] buf = new byte[256];
        final ByteBuffer buffer = ByteBuffer.wrap(buf);
        final DatagramPacket packet = new DatagramPacket(buf, buf.length, dest, port);

        while (true) {

            // String curDay = f.format(new Date(start));
            // if (args.length > 1 && !day.equals(curDay)) {
            // out.close();
            // out = new PrintStream(
            // new FileOutputStream(new File(args[1].replace(".csv", "") + "=" +
            // curDay + ".csv")));
            // out.println("#time(unixtime) latency(ms) min(latency)
            // max(latency)");
            // }

            Double sum = 0.0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            int count = 0;
            int lost = 0;

            long now = System.currentTimeMillis();
            long w = interval - (now % interval) - 5;
            // log.info("Sleeping {} ms for alignment, (now={})", w, now);
            doSleep(w);

            try {

                while (count++ < probes) {
                    now = System.currentTimeMillis();
                    // buffer.clear();
                    // buffer.putLong(now);

                    try {
                        client.send(packet);
                        client.receive(packet);
                    } catch (Exception ste) {
                        log.error("Receiver timeout - marking packet as lost.");
                        lost++;
                        sum = 0.0;
                        min = Double.MAX_VALUE;
                        max = Double.MAX_VALUE;
                        break;
                    }

                    // buffer.position(0);
                    // buffer.put(packet.getData(), 0, packet.getLength());
                    // buffer.flip();
                    long then = now; // buffer.getLong();
                    long recvTime = System.currentTimeMillis();

                    double latency = (recvTime - then) * 0.5;
                    min = Math.min(min, latency);
                    max = Math.max(max, latency);
                    sum += latency;
                }
                Double avgLatency = sum / probes.doubleValue();

                for (DataListener l : listener) {
                    Data output = DataFactory.create();
                    output.put("@time", System.currentTimeMillis());
                    output.put("destination", dest.getHostAddress());
                    if (probes > 1) {
                        output.put("avg:latency", new Double(avgLatency));
                        output.put("min:latency", new Double(min));
                        output.put("max:latency", new Double(max));
                    } else {
                        output.put("latency", new Double(sum));
                    }
                    output.put("lost", new Integer(lost));
                    l.dataArrived(output);
                }
            } catch (Exception e) {
                log.error("Error occurred: {}", e.getMessage());
                e.printStackTrace();
            }

            // doSleep(interval);
        }
    }

    public void doSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        }
    }

    public void addListener(DataListener l) {
        if (!listener.contains(l)) {
            listener.add(l);
        }
    }

    public void removeListener(DataListener l) {
        listener.remove(l);
    }

    public static void main(String args[]) throws Exception {

        List<String> hosts = new ArrayList<String>();
        for (String arg : args) {
            hosts.add(arg.trim());
        }

        if (hosts.isEmpty()) {
            System.err.println("No destination hosts for monitoring provided!");
            System.exit(-1);
        }

        // hosts.add("builder.sfb876.de");

        List<LatencyClient> clients = new ArrayList<LatencyClient>();

        for (String host : hosts) {
            LatencyClient client = new LatencyClient(host, 10001);
            client.addListener(new DataListener() {
                @Override
                public void dataArrived(Data item) {
                    System.out.println(item);
                }
            });

            client.interval(2000);
            client.start();
        }

        while (clients.size() > 0) {
            LatencyClient client = clients.get(0);
            try {
                client.join();
                clients.remove(client);
                log.info("client {} finished...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // LatencyClient client = new LatencyClient("192.168.128.97", 10001);
        // client.addListener(new DataListener() {
        // @Override
        // public void dataArrived(Data item) {
        // System.out.println(item);
        // }
        // });
        //
        // client.interval(500);
        // client.run();
    }
}