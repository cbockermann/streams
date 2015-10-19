/**
 * 
 */
package streams.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import net.minidev.json.JSONObject;
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
		sender.setDaemon(true);
		sender.start();
	}

	public static void add(Message m) {
		messages.offer(m);
	}

	protected static class Sender extends Thread {

		PrintStream out;
		BufferedReader in;

		public Sender() {
			setDaemon(false);
		}

		public void run() {
			while (true) {
				try {
					Message m = messages.take();
					System.out.println("Sending message " + m);
					send(m);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		protected Socket connect() throws Exception {
			Socket socket = SecureConnect.connect();
			out = new PrintStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return socket;
		}

		public void send(Message m) {
			try {
				if (out == null || in == null) {
					connect();
				}

				out.println(JSONObject.toJSONString(m));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
