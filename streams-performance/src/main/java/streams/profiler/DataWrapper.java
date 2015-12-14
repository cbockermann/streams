/**
 * 
 */
package streams.profiler;

import java.io.Serializable;
import java.util.Map;

import stream.Data;
import stream.data.DataImpl;

/**
 * @author chris
 *
 */

public class DataWrapper extends DataImpl {

    private static final long serialVersionUID = 8251825785784938452L;
    transient TypeMap types;

    public DataWrapper(Data item, TypeMap explorer) {
        super.putAll(item);
        this.types = explorer;
    }

    /**
     * @see stream.data.DataImpl#createCopy()
     */
    @Override
    public Data createCopy() {
        DataWrapper wrapper = new DataWrapper(this, types);
        return wrapper;
    }

    /**
     * @see java.util.LinkedHashMap#get(java.lang.Object)
     */
    @Override
    public Serializable get(Object key) {
        Serializable value = super.get(key);
        types.read(key.toString(), value);
        return value;
    }

    /**
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public Serializable put(String key, Serializable value) {
        types.write(key, value);
        return super.put(key, value);
    }

    /**
     * @see java.util.HashMap#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends String, ? extends Serializable> m) {
        for (String k : m.keySet()) {
            put(k, m.get(k));
        }
    }

    /**
     * @see java.util.HashMap#remove(java.lang.Object)
     */
    @Override
    public Serializable remove(Object key) {
        Serializable value = super.remove(key);
        if (types != null) {
            types.remove(key.toString(), value);
        }
        return value;
    }
}