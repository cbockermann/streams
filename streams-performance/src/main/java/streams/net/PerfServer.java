/**
 * 
 */
package streams.net;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class PerfServer {

	static Logger log = LoggerFactory.getLogger(PerfServer.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Random rnd = new Random();

		int port = new Integer(System.getProperty("port", "10000"));
		int payload = new Integer(System.getProperty("message.size", "100"));
		int messages = new Integer(System.getProperty("messages", "1000000"));
		int clients = new Integer(System.getProperty("clients", "1"));

		log.info("Waiting for {} clients to connect...", clients);

		List<ClientHandler> handlers = new ArrayList<ClientHandler>();
		byte[] data = new byte[payload];
		rnd.nextBytes(data);
		data[0] = 0xf;

		ServerSocket socket = new ServerSocket(port);

		while (handlers.size() < clients) {
			Socket client = socket.accept();

			ClientHandler handler = new ClientHandler(client, data, messages);
			handlers.add(handler);
			log.info("{} clients still need to connect...", clients - handlers.size());
		}

		log.info("all expected clients connected, starting to send...");
		for (ClientHandler handler : handlers) {
			handler.start();
		}

		log.info("Waiting for client to finish...");
		for (ClientHandler handler : handlers) {
			handler.join();
		}

		socket.close();
	}

	public static class ClientHandler extends Thread {
		Socket client;
		byte[] data;
		long messages;

		long bytes = 0L;
		long start = 0L;
		long end = 0L;

		public ClientHandler(Socket client, byte[] data, long messages) {
			this.client = client;
			this.data = data;
			this.messages = messages;
		}

		public long bytesSent() {
			return bytes;
		}

		public long startTime() {
			return start;
		}

		public long endTime() {
			return end;
		}

		public void run() {
			try {
				BufferedInputStream in = new BufferedInputStream(client.getInputStream());
				OutputStream out = client.getOutputStream();

				bytes = 0L;
				start = System.currentTimeMillis();

				for (int m = 0; m < messages; m++) {

					if (m + 1 == messages) {
						data[0] = 0xe;
						data[1] = 0x0;
						data[2] = 0xf;
					}
					out.write(data);
					bytes += data.length;
				}
				end = System.currentTimeMillis();

				DataInputStream din = new DataInputStream(in);
				long accepted = din.readLong();
				log.info("{} packets accepted by client.", accepted);
				out.close();
				din.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
