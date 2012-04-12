/**
 * 
 */
package stream.io;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;

/**
 * @author chris
 * 
 */
public class JSONStream extends AbstractDataStream {

	static Logger log = LoggerFactory.getLogger(JSONStream.class);
	JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

	/**
	 * @param in
	 * @throws Exception
	 */
	public JSONStream(InputStream in) throws Exception {
		super(in);
	}

	public JSONStream(URL url) throws Exception {
		super(url);
	}

	public JSONStream(URL url, String user, String password) throws Exception {
		super(url, user, password);
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * @see stream.io.AbstractDataStream#readHeader()
	 */
	@Override
	public void readHeader() throws Exception {
	}

	/**
	 * @see stream.io.AbstractDataStream#readItem(stream.data.Data)
	 */
	@Override
	public Data readItem(Data instance) throws Exception {

		if (reader == null)
			this.initReader();

		String line = reader.readLine();
		log.debug("line: {}", line);
		if (line == null) {
			return null;
		}

		log.debug("Parsing item from {}", line);
		JSONObject object = parser.parse(line, JSONObject.class);
		if (object != null) {
			for (String key : object.keySet()) {
				Object val = object.get(key);
				if (val instanceof Serializable)
					instance.put(key, (Serializable) val);
				else
					instance.put(key, val.toString());
			}
		} else {
			log.debug("Failed to parse item, object = {}", object);
		}

		log.debug("returning instance: {}", instance);
		return instance;
	}
}