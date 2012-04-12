package stream;

/**
 * This interface is implemented by all processors that provide a memory
 * measurement in number of bytes. This is used for estimating the memory
 * consumption of processors over time.
 * 
 * @author Christian Bockermann
 */
public interface Measurable {

	/**
	 * Returns the size of this object in bytes as double.<br />
	 * In case of an error Double.NaN will be returned.
	 * 
	 * @return Number of bytes if successful, Double.NaN if an error occurs
	 */
	public double getByteSize();
}