/**
 * 
 */
package streams.net;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minidev.json.JSONObject;
import streams.logging.Message;

/**
 * @author chris
 *
 */
public class SSLSender {

	static Logger log = LoggerFactory.getLogger(SSLSender.class);

	public static Message message(String trace) {
		return new Message(null).add("@rlog.token", "ab09cfe1d60b602cb7600b5729da939f").add("trace", trace);
	}

	public static Message message() {
		return new Message(null).add("@rlog.token", "ab09cfe1d60b602cb7600b5729da939f");
	}

	public static String format(Map<String, ?> msg) {
		return JSONObject.toJSONString(msg);
	}

	@Test
	public void testSSLSender() throws Exception {

		Socket client = SecureConnect.connect("performance.sfb876.de", 6001);

		Message m = message("test").add("text", "Hello, world!");
		PrintStream out = new PrintStream(client.getOutputStream());
		List<String> outgoing = new ArrayList<String>();
		int sent = 0;
		for (int i = 0; i < 1000; i++) {
			String msg = format(m.add("count", i));
			outgoing.add(msg);
			out.println(msg);
			sent++;
		}
		out.flush();
		log.info("Sent message.");

		BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		String re = null;
		int acked = 0;
		do {
			if (reader.ready()) {
				re = reader.readLine();
				if (re != null) {
					acked++;
				}
			}
		} while (re != null && acked < sent);

		out.close();
		reader.close();

		Assert.assertSame(acked, sent);

	}

}
