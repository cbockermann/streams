/**
 * 
 */
package streams.tikz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chris
 * 
 */
public abstract class Shape extends ArrayList<Path> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7626578691365947838L;

	public final Map<String, String> opts = new HashMap<String, String>();

	public abstract Shape scale(double x, double y);
}
