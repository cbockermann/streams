/**
 * 
 */
package streams.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * 
 * @author chris
 *
 */
public class LatencyServer {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        final byte[] buf = new byte[64];
        int count = new Integer(System.getProperty("probes", "10"));
        final DatagramPacket packet = new DatagramPacket(buf, 0, buf.length);

        count = -1;
        DatagramSocket socket = new DatagramSocket(10001);
        socket.setTrafficClass(0x10);

        while (count-- != 0) {
            socket.receive(packet);
            socket.send(packet);
        }

        socket.close();
    }
}