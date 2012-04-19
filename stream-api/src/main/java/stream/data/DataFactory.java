/**
 * 
 */
package stream.data;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This factory class is used to create instances of the data item class.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class DataFactory {

	static Logger log = LoggerFactory.getLogger(DataFactory.class);
	public static DataFactory defaultDataFactory = new DataFactory();
	private static long dataItemsCreated = 0L;

	protected DataFactory() {
	}

	/**
	 * This method allows for providing a customer DataFactory to produce data
	 * items. The factory cannot be changed after data items have been created
	 * within this runtime, therefore, this method needs to be called before any
	 * data items are created and used.
	 * 
	 * @param factory
	 */
	public static void setDefaultDataFactory(DataFactory factory)
			throws Exception {
		if (dataItemsCreated > 0)
			throw new Exception(
					"Data items have already been created with the current DataFactory '"
							+ defaultDataFactory
							+ "'. The DataFactory cannot be changed anymore!");

		defaultDataFactory = factory;
	}

	/**
	 * <p>
	 * This method will create a new Data item.
	 * </p>
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Data createDataItem() {
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
	public Data createDataItem(Map<String, Serializable> item) {
		//
		// The DataImpl constructor is only marked as deprecated to make
		// developers move to this factory class.
		//
		return new DataImpl(item);
	}

	/**
	 * <p>
	 * This method will create a new Data item.
	 * </p>
	 * 
	 * @return
	 */
	public static Data create() {
		dataItemsCreated++;
		return defaultDataFactory.createDataItem();
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
	public static Data create(Map<String, Serializable> data) {
		dataItemsCreated++;
		return defaultDataFactory.createDataItem(data);
	}
}
