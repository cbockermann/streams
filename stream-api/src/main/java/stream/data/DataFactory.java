/**
 * 
 */
package stream.data;

/**
 * <p>
 * This factory class is used to create instances of the data item class.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class DataFactory {

	/**
	 * <p>
	 * This method will create a new Data item.
	 * </p>
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Data create() {
		//
		// The DataImpl constructor is only marked as deprecated to make
		// developers move to this factory class.
		//
		return new DataImpl();
	}

	/***
	 * <p>
	 * This method will create a new Data item and will add the contents of the
	 * specified item to the newly created item.
	 * </p>
	 * 
	 * @param data
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Data create(Data data) {
		//
		// The DataImpl constructor is only marked as deprecated to make
		// developers move to this factory class.
		//
		return new DataImpl(data);
	}
}
