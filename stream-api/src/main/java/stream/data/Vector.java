/**
 * 
 */
package stream.data;

/**
 * 
 * @author chris
 * 
 */
public interface Vector {

	/**
	 * This method returns a sorted array of vector indices.
	 * 
	 * @return
	 */
	public int[] indexes();

	/**
	 * This method returns an array of values that has the same size as the
	 * index array returned by {@link #indexes()}. The value at entry
	 * <code>i</code> corresponds to the vector component at
	 * <code>getIndexes()[i]</code>.
	 * 
	 * @return
	 */
	public double[] getValues();

	/**
	 * Returns the
	 * 
	 * @param idx
	 * @return
	 */
	public Double getValue(int idx);

	public void setValue(int idx, Double d);
}
