/**
 * 
 */
package streams.service;

import java.io.Serializable;
import java.util.Map;

import stream.Data;
import stream.service.Service;

/**
 * This interface defines a generic lookup service that returns some data
 * (hashmap) based on a given data item as input.
 * 
 * @author Christian Bockermann
 *
 */
public interface TestLookupService extends Service {

    /**
     * Return new elements based on a given item.
     * 
     * @param item
     * @return
     */
    public Map<String, Serializable> lookup(Data item);
}
