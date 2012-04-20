package stream.service;

import stream.data.Data;

/**
 * @author Hendrik Blom
 * 
 */
public interface LookupService extends Service {

	public Data lookup(String key);
}
