/**
 * 
 */
package stream.urls;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.MD5;

/**
 * @author chris
 * 
 */
public class RandomDataServer extends Thread {
	static Logger log = LoggerFactory.getLogger(RandomDataServer.class);
	ServerSocket socket;
	int limit = 100;

	public RandomDataServer(String addr, int limit) throws IOException {
		socket = new ServerSocket(0, 1000, InetAddress.getByName(addr));
		this.limit = limit;
		this.setDaemon(true);
	}

	public RandomDataServer(int limit) throws IOException {
		this("127.0.0.1", limit);
	}

	public String getLocalAddress() {
		return socket.getInetAddress().getHostAddress();
	}

	public int getLocalPort() {
		return socket.getLocalPort();
	}

	public void run() {

		try {
			log.info("RandomDataServer started, listening on {}", socket);
			Socket client = socket.accept();
			log.info("Accepted client connection from {}:{}", client
					.getInetAddress().getHostAddress(), client.getPort());

			PrintStream out = new PrintStream(client.getOutputStream());
			int cnt = 0;
			while (cnt < limit) {
				String rnd = MD5.md5(System.nanoTime());
				out.println(rnd);
				log.info("Sent random line {}", cnt);
				cnt++;
			}

			log.info("Closing client connection...");
			client.close();

		} catch (Exception e) {
			log.error("Error in RandomDataServer: {}", e.getMessage());
			e.printStackTrace();
		}
	}
}