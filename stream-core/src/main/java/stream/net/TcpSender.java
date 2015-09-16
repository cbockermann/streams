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

import java.io.PrintStream;
import java.net.Socket;
import java.util.Set;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.Keys;
import stream.ProcessContext;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class TcpSender extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(TcpSender.class);
	Socket socket = null;
	PrintStream out = null;
	Keys keys;

	String address;
	int port;

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
	}

	protected void connect() throws Exception {
		log.debug("Opening socket connection to {}:{}... ", address, port);
		try {
			socket = new Socket(address, port);
			out = new PrintStream(socket.getOutputStream());
		} catch (Exception e) {
			socket = null;
			throw e;
		}
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (input == null)
			return input;
		try {
			if (socket == null)
				connect();

			String json;
			if (keys != null) {

				Set<String> selected = keys.select(input);
				Data item = DataFactory.create();
				for (String k : selected) {
					item.put(k, input.get(k));
				}
				log.debug("Sending item {}", item);
				json = JSONObject.toJSONString(item);
			} else {
				json = JSONObject.toJSONString(input);
			}

			out.println(json);
		} catch (Exception e) {
			log.error("Faild to send item: {}", e.getMessage());
			e.printStackTrace();
		}
		return input;
	}

	/**
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();
		if (out != null)
			out.close();

		if (socket != null)
			socket.close();
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}



	/**
	 * @param keys
	 *            the keys to set
	 */
	public void setKeys(Keys keys) {
		this.keys = keys;
	}
}
