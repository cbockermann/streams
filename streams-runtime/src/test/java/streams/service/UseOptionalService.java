/**
 * 
 */
package streams.service;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Service;

/**
 * @author chris
 *
 */
public class UseOptionalService extends AbstractProcessor {

    static Logger log = LoggerFactory.getLogger(UseOptionalService.class);

    @Service(required = false)
    TestLookupService lookup;

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data item) {

        if (lookup != null) {
            log.info("Using lookup-service {}", lookup);
            Map<String, Serializable> addons = lookup.lookup(item);
            item.putAll(addons);
        } else {
            log.info("No lookup-service available!");
        }

        return item;
    }
}
