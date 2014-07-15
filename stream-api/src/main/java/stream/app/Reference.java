/**
 * 
 */
package stream.app;

/**
 * <p>
 * This class defines a reference for dependency injection.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public class Reference {

	/** The object to inject the resolved reference later on */
	final Object object;

	/** The property of the object into which the reference it to be injected */
	final String property;

	/** IDs of the referenced objects may be more than 1 for array injection */
	final String[] ids;

	public Reference(Object target, String property, String id) {
		this.object = target;
		this.property = property;
		this.ids = new String[] { id };
	}

	public Reference(Object target, String property, String[] id) {
		this.object = target;
		this.property = property;
		this.ids = id;
	}

	public Object object() {
		return object;
	}

	public String property() {
		return property;
	}

	public String[] ids() {
		return ids;
	}

	public String toString() {
		StringBuffer s = new StringBuffer(this.getClass().getSimpleName() + "["
				+ object + "]{ '" + property + "':[");
		for (int i = 0; i < ids.length; i++) {
			s.append(ids[i]);
			if (i + 1 < ids.length)
				s.append(", ");
		}
		s.append("] }");
		return s.toString();
	}
}
