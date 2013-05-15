/**
 * 
 */
package stream.lookup;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Context;
import stream.Data;
import stream.annotations.Parameter;
import stream.io.SourceURL;
import stream.io.Stream;
import stream.runtime.LifeCycle;
import stream.service.LookupService;

/**
 * <p>
 * This class is an abstract implementation of the LookupService which provides
 * an in-memory lookup table that is populated from a data source. Specific
 * non-abstract implementations will need to implement the way to read the
 * in-memory lookup table (hashmap).
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public abstract class AbstractDatabase implements LookupService, LifeCycle {

	static Logger log = LoggerFactory.getLogger(AbstractDatabase.class);
	final Map<String, Data> database = new LinkedHashMap<String, Data>();
	String key = "@id";
	SourceURL url;

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		log.debug("Re-reading database...");
		Map<String, Data> db = new LinkedHashMap<String, Data>();
		populateDatabase(url, db);
		database.clear();
		database.putAll(db);
	}

	/**
	 * @see stream.runtime.LifeCycle#init(stream.Context)
	 */
	@Override
	public void init(Context context) throws Exception {

		if (url == null) {
			throw new Exception("No 'url' attribute specified!");
		}

		populateDatabase(url, database);
	}

	protected abstract void populateDatabase(SourceURL url,
			Map<String, Data> database) throws Exception;

	protected void readDatabase(Stream stream, Map<String, Data> database)
			throws Exception {

		Data item = stream.read();
		while (item != null) {

			Serializable value = item.get(key);
			if (value == null) {
				log.error("Missing attribute '{}' in item read from URL: {}",
						key, item);
			} else {
				log.debug("Adding item for key '{}': {}", value, item);
				database.put(value.toString(), item);
			}

			item = stream.read();
		}

		log.info("{} items read from source {}.", database.size(), stream);
	}

	/**
	 * @see stream.runtime.LifeCycle#finish()
	 */
	@Override
	public void finish() throws Exception {
	}

	/**
	 * @see stream.service.LookupService#lookup(java.lang.String)
	 */
	@Override
	public Data lookup(String key) {
		Data item = database.get(key);
		log.debug("Found item for key '{}': {}", key, item);
		return item;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	@Parameter(description = "The lookup-key that is used for storing items in the lookup table. This key must be present in the items that are being read from the datasource while populating the database. Default value is '@id'.", required = false)
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the url
	 */
	public SourceURL getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	@Parameter(description = "The source URL from which data should be read at startup. This data is then used for populating the lookup table.", required = true)
	public void setUrl(SourceURL url) {
		this.url = url;
	}
}