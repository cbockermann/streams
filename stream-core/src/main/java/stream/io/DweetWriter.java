package stream.io;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;

public class DweetWriter extends AbstractProcessor {

	static Logger logger = LoggerFactory.getLogger(DweetWriter.class);
	protected String machine;
	protected String id;
	protected String thing;
	protected String baseUrl;
	protected String[] keys;
	protected String prefix;
	protected String[] postKeys;

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getThing() {
		return thing;
	}

	public void setThing(String thing) {
		this.thing = thing;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public DweetWriter() {

	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);

		this.baseUrl = "https://dweet.io:443/dweet/for/";
		String prefix = this.machine + "_" + id;
		this.postKeys = new String[keys.length];

		for (int i = 0; i < keys.length; i++) {
			postKeys[i] = prefix + "_" + keys[i] + "=";
			postKeys[i] = postKeys[i].replace(":", "_");
			postKeys[i] = postKeys[i].replace("@", "_");
			postKeys[i] = postKeys[i].replace("-", "_");
		}

	}

	@Override
	public Data process(Data data) {

		StringBuilder b = new StringBuilder();
		b.append(baseUrl);
		b.append(this.thing);
		int count = 0;
		boolean first = true;
		for (int i = 0; i < keys.length; i++) {
			Serializable dk = data.get(keys[i]);
			if (dk != null) {
				if (first) {
					b.append("?");
					first = false;
				} else
					b.append("&");
				b.append(postKeys[i]);

				count++;
				// if (dk instanceof String) {
				// b.append("\"");
				// b.append(dk.toString());
				// b.append("\"");
				// continue;
				// }
				b.append(dk.toString());
			}
		}
		if (count > 0) {
			String surl = b.toString();

			System.out.println(surl);

			URL url = null;
			try {
				url = new URL(surl);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type",
						"application/json");
				logger.info("dweet.io says {}", connection.getResponseCode());
				connection.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else
			logger.info("nothing to dweet!");
		return data;
	}
}
