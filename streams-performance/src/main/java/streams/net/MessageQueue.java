/**
 * 
 */
package streams.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import stream.Data;
import stream.data.DataFactory;
import stream.io.Codec;
import stream.io.JavaCodec;
import streams.io.BobCodec;
import streams.logging.Message;

/**
 * @author chris
 *
 */
public class MessageQueue {

	final static LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<Message>();

	private static Sender sender = new Sender();

	static {
		System.out.println("Initializing global MessageQueue");

		String host = System.getProperty("rlog.host");
		if (host == null) {
			System.out.println("'rlog.host' not set, disabling rlog-sender");
			sender = null;
		} else {
			sender.setDaemon(true);
			sender.start();
		}
	}

	public static void add(Message m) {
		if (sender != null) {
			messages.offer(m);
		}
	}

	protected static class Sender extends Thread {

		final Codec<Data> mc = new JavaCodec<Data>();

		DataOutputStream out;
		BufferedReader in;

		public Sender() {
			setDaemon(false);
		}

		public void run() {
			while (true) {
				try {
					Message m = messages.take();
//					System.out.println("Sending message " + m);
					send(m);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		protected Socket connect() throws Exception {
			Socket socket = SecureConnect.connect();
			out = new DataOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return socket;
		}

		public void send(Message m) {
			try {
				if (out == null || in == null) {
					connect();
				}

				byte[] bytes = mc.encode(DataFactory.create(m));
//				System.out.println("Encoded message to " + bytes.length + " bytes");
				int written = BobCodec.writeBlock(bytes, out);
//				System.out.println(written + " bytes written to socket...");
				out.flush();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
