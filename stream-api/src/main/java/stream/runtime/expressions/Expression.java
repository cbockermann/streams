package stream.runtime.expressions;

import java.io.Serializable;

import stream.data.Data;
import stream.runtime.Context;

/**
 * <p>
 * This interface defines a simple or complex filter expression.
 * <p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public interface Expression extends Serializable {

	/**
	 * Matches the expression against the given event.
	 * 
	 * @param evt
	 * @return
	 */
	public boolean matches(Context ctx, Data item);
}