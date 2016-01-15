/**
 * 
 */
package streams.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;

/**
 * This implementation of the lookup service interface will return a hashmap
 * with a sequential ID field (key '@ext:id') and internally increment this id.
 * 
 * IDs start with 1.
 * 
 * @author Christian Bockermann
 *
 */
public class ExternalDatabaseMock implements TestLookupService {

    static Logger log = LoggerFactory.getLogger(ExternalDatabaseMock.class);

    final AtomicInteger id = new AtomicInteger(1);

    public ExternalDatabaseMock() {
        log.info("Created ExternalDatabaseMock: {}", this);
    }

    /**
     * @see stream.service.Service#reset()
     */
    @Override
    public void reset() throws Exception {
    }

    /**
     * @see streams.service.TestLookupService#lookup(stream.Data)
     */
    @Override
    public Map<String, Serializable> lookup(Data item) {
        Map<String, Serializable> output = new HashMap<String, Serializable>();
        int next = id.getAndIncrement();
        output.put("@ext:id", next);
        return output;
    }
}