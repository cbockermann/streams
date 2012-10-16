package stream.data.storage;

import java.io.Serializable;

import stream.service.Service;

/**
 * @author Hendrik Blom
 * 
 */
public interface DataService<T extends Serializable> extends Service {

	public T getData(String key);

}