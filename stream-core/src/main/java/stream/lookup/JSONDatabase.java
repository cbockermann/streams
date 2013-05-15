/**
 * 
 */
package stream.lookup;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.JSONStream;
import stream.io.SourceURL;

/**
 * <p>
 * This implementation of the AbstractDatabase lookup service uses a JSON stream
 * to populate the lookup table.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public class JSONDatabase extends AbstractDatabase {

	static Logger log = LoggerFactory.getLogger(JSONDatabase.class);

	protected void populateDatabase(SourceURL url, Map<String, Data> database)
			throws Exception {
		JSONStream stream = new JSONStream(url);
		stream.init();
		readDatabase(stream, database);
		stream.close();
	}
}