/**
 * 
 */
package stream.net;

import java.io.PrintStream;
import java.net.Socket;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;

/**
 * @author chris
 * 
 */
public class TcpSender extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(TcpSender.class);
	Socket socket = null;
	PrintStream out = null;
	String[] keys;

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
			log.debug("Sending item {}", input);
			String json = JSONObject.toJSONString(input);
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

}
