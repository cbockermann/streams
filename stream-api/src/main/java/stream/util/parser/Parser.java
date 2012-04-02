package stream.util.parser;

import java.util.Map;

/**
 * <p>
 * This interface defines a simple parser definition.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 * @param <E>
 */
public interface Parser<E> {

	public E parse(String str) throws ParseException;

	public Map<String, String> getDefaults();

	public void setDefaults(Map<String, String> defaults);
}