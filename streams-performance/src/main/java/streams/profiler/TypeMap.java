/**
 * 
 */
package streams.profiler;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;

/**
 * @author chris
 *
 */

public class TypeMap extends LinkedHashMap<String, String> {

    private static final long serialVersionUID = 2987143663959753791L;
    static Logger log = LoggerFactory.getLogger(TypeMap.class);

    final Processor p;

    public TypeMap(Processor p) {
        this.p = p;
    }

    public Processor processor() {
        return p;
    }

    public void read(String key, Serializable value) {
        log.debug("marking 'read:{}'", key);
        put("read:" + key, typeOf(value));
    }

    public void write(String key, Serializable value) {
        log.debug("marking 'write:{}'", key);
        put("write:" + key, typeOf(value));
    }

    public void remove(String key, Serializable value) {
        log.debug("marking 'remove:{}'", key);
        put("remove:" + key, typeOf(value));
    }

    public void check(String key, Serializable value) {
        log.debug("marking 'check:{}'", key);
        put("check:" + key, typeOf(value));
    }

    public String typeOf(Serializable value) {
        if (value != null) {
            if (value.getClass().isArray()) {
                Class<?> comp = value.getClass().getComponentType();
                return comp.getCanonicalName() + "[" + Array.getLength(value) + "]";
            } else {
                return value.getClass().getCanonicalName();
            }
        }

        return "?";
    }
}
