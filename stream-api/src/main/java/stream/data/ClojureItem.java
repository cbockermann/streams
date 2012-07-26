/**
 * 
 */
package stream.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import clojure.lang.PersistentHashMap;

/**
 * @author chris
 * 
 */
public class ClojureItem implements Data {

	/** The unique class ID */
	private static final long serialVersionUID = 3268302524899938729L;
	private PersistentHashMap map = PersistentHashMap.create(new Object[0]);

	public ClojureItem() {
	}

	public ClojureItem(ClojureItem other) {
		map = other.map; // (PersistentHashMap)
							// PersistentHashMap.create(other.map);
	}

	/**
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		map.clear();
	}

	/**
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object arg0) {
		return map.containsKey(arg0);
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	/**
	 * @see java.util.Map#entrySet()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Set<java.util.Map.Entry<String, Serializable>> entrySet() {
		return (Set<java.util.Map.Entry<String, Serializable>>) map.entrySet();
	}

	/**
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public Serializable get(Object arg0) {
		return (Serializable) map.get(arg0);
	}

	/**
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Set<String> keySet() {
		return (Set<String>) map.keySet();
	}

	/**
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Serializable put(String key, Serializable value) {
		map = (PersistentHashMap) map.assoc(key, value);
		return (Serializable) value;
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends String, ? extends Serializable> arg0) {
		map.putAll(arg0);
	}

	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public Serializable remove(Object arg0) {
		Serializable value = (Serializable) map.get(arg0);
		map = (PersistentHashMap) map.without(arg0);
		return value;
	}

	/**
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return map.size();
	}

	/**
	 * @see java.util.Map#values()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<Serializable> values() {
		return (Collection<Serializable>) map.values();
	}

	public Data createCopy() {
		return new ClojureItem(this);
	}
}
