/**
 * 
 */
package stream;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author chris
 *
 */
public abstract class AbstractContext implements Context, Serializable {

    /** The unique class ID */
    private static final long serialVersionUID = -8655916624016379698L;

    protected final Context parent;
    protected final String id;
    protected final Map<String, Object> values = new LinkedHashMap<String, Object>();

    public AbstractContext(String id) {
        this(id, null);
    }

    public AbstractContext(String id, Context parent) {
        this.parent = parent;
        this.id = id;
    }

    /**
     * @see stream.Context#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @see stream.Context#resolve(java.lang.String)
     */
    @Override
    public Object resolve(String key) {
        if ("id".equals(key)) {
            return getId();
        }

        if (key.startsWith(scope() + ".")) {
            return values.get(key.substring(scope().length() + 1));
        }

        if (parent != null) {
            return parent.resolve(key);
        }

        return null; // values.get(key);
    }

    /**
     * @see stream.Context#contains(java.lang.String)
     */
    @Override
    public boolean contains(String key) {
        if ("id".equals(key)) {
            return true;
        }

        return values.containsKey(key);
    }

    /**
     * @see stream.Context#getParent()
     */
    @Override
    public Context getParent() {
        return parent;
    }

    public String name() {
        return this.getClass().getSimpleName().toLowerCase().replaceAll("context$", "");
    }

    public String scope() {
        return this.getClass().getSimpleName().toLowerCase().replaceAll("context$", "");
    }

    public String path() {
        if (parent != null) {
            return parent.path() + Context.PATH_SEPARATOR
                    + this.getClass().getSimpleName().toLowerCase().replaceAll("context$", "") + ":" + getId();
        } else {
            return this.getClass().getSimpleName().toLowerCase().replaceAll("context$", "") + ":" + getId();
        }
    }
}