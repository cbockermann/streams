package stream.counter;

import java.io.Serializable;
import java.util.Set;

/**
 * <p>
 * Extension of the {@link PredictionModel} interface to be able to provide
 * additional information which seems to be common for count algorithms.<br />
 * Currently this is the total number of elements counted so far, available
 * through {@link #getTotalCount()} and the "key set" the counting (so far)
 * happened for, available through {@link #keySet()}.
 * 
 * @author Benedikt Kulmann, office@kulmann.biz
 */
public interface Counter<T> extends Serializable {

	/**
	 * Returns the total number of elements counted so far.
	 * 
	 * @return the total number of elements counted so far.
	 */
	public Long getTotalCount();

	/**
	 * Returns the current "key set" of the counting algorithm which means the
	 * different elements that have occurred so far.
	 * 
	 * @return The set of different elements which have occurred so far.
	 */
	public Set<T> keySet();

	public void count(T element);

	public Long getCount(T element);
}
