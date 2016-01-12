/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.net;

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

	public RandomDataServer(int limit) throws IOException {
		socket = new ServerSocket(0);
		this.limit = limit;
		this.setDaemon(true);
	}

	public RandomDataServer(String host, int limit) throws IOException {
		socket = new ServerSocket(0, 1000, InetAddress.getByName(host));
		this.limit = limit;
		setDaemon(true);
	}

	public String getLocalAddress() {
		return socket.getInetAddress().getHostAddress();
	}

	public int getLocalPort() {
		return socket.getLocalPort();
	}

	public void run() {

		try {
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