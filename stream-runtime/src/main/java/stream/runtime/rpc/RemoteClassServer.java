/**
 * 
 */
package stream.runtime.rpc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class RemoteClassServer extends Thread {

	static Logger log = LoggerFactory.getLogger(RemoteClassServer.class);
	ServerSocket socket;

	public RemoteClassServer() throws Exception {
		this(0);
	}

	public RemoteClassServer(int port) throws Exception {
		socket = new ServerSocket(port);
	}

	public void run() {
		while (true) {
			try {
				Socket client = socket.accept();
				log.info(
						"creating handler for client {}",
						client.getInetAddress().getHostAddress() + ":"
								+ client.getPort());
				RequestHandler handler = new RequestHandler(client);
				handler.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class RequestHandler extends Thread {

		Logger log = LoggerFactory.getLogger(RequestHandler.class);
		Socket socket;

		public RequestHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));

				Map<String, String> headers = new LinkedHashMap<String, String>();
				StringBuffer header = new StringBuffer();

				String line = reader.readLine();
				String[] req = line.split("\\s+");
				String resource = req[1];

				while (line != null && !line.trim().isEmpty()) {

					if (line.indexOf(":") > 0) {
						String[] tok = line.split(":", 2);
						headers.put(tok[0].trim(), tok[1].trim());
					}

					header.append(line + "\n");
					line = reader.readLine();
				}
				log.info("header:\n{}", header);

				URL url = RemoteClassServer.class.getResource(resource);
				log.info("Resource {} is at {}", resource, url);
				byte[] data = readResource(resource);

				OutputStream rawOut = socket.getOutputStream();

				PrintWriter out = new PrintWriter(rawOut);
				if (data != null) {
					log.info("{} bytes read", data.length);

					out.print("HTTP/1.0 200 OK\r\n");
					out.print("Content-Length: " + data.length + "\r\n");
					out.print("Content-Type: text/html\r\n\r\n");
					out.flush();
					rawOut.write(data);
					rawOut.flush();
					/*
					 * out.print("HTTP 200 Ok\r\n");
					 * out.print("Content-length: " + data.length + "\r\n");
					 * out.print("Content-Type: application/java\r\n\r\n");
					 * out.flush();
					 * 
					 * rawOut.write(data);
					 */
					log.info("{} bytes sent", data.length);
					rawOut.flush();
					rawOut.close();
				} else {
					out.print("HTTP 404 Not Found\r\n\r\n");
					out.flush();
				}
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	public static byte[] readResource(String path) throws Exception {

		byte[] bytes = new byte[64 * 1024];
		int off = 0;
		int total = 0;
		URL url = RemoteClassServer.class.getResource(path);
		System.out.println("URL: " + url);
		if (url != null) {
			InputStream source = url.openStream();
			int read = 0;
			do {
				read = source.read(bytes, off, 4096);
				System.out.println(read + " bytes read.");
				if (read > 0) {
					off += read;
					total += read;
				}
			} while (read > 0);
			source.close();
			ByteBuffer buffer = ByteBuffer.allocate(total);
			buffer.put(bytes, 0, total);
			return buffer.array();
		} else {
			return null;
		}
	}

	public static void main(String[] args) throws Exception {

		byte[] data = readResource("/stream/data/Data.class");
		System.out.println("data: " + data + " (" + data.length + " bytes)");
	}
}