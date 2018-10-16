/**
 * 
 */
package stream.data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.AbstractProcessor;
import stream.Data;
import stream.Keys;

/**
 * This processor keeps a state of attributes, which will result in filling up
 * missing values of data items as they are processed. New values of an incoming
 * item will update the current state of the attribute within the processor.
 * 
 *
 * @author Christian Bockermann
 */
public class SteadyState extends AbstractProcessor {

    final Map<String,Serializable> states = new LinkedHashMap<String,Serializable>();
    Keys keys = new Keys("*");

    
    /**
     * @return the keys
     */
    public Keys getKeys() {
        return keys;
    }

    /**
     * @param keys the keys to set
     */
    public void setKeys(Keys keys) {
        this.keys = keys;
    }

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data item) {
        
        for( String key : keys.select( item)) {
            states.put( key, item.get( key));
        }
        
        for( String key : states.keySet()) {
            if( ! item.containsKey( key )) {
                item.put( key, states.get( key));
            }
        }
        
        return item;
    }
}