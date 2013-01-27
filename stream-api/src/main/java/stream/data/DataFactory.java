/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.data;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.Serializer;
import stream.util.JavaSerializer;

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

	final Serializer serializer = new JavaSerializer();

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
	public Data createDataItem() {
		//
		// The DataImpl constructor is only marked as deprecated to make
		// developers move to this factory class.
		//
		return new DataImpl();
	}

	public Data clone(Data item) {
		try {
			return (Data) serializer.clone(item);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
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

	public static Data copy(Data item) {
		return defaultDataFactory.clone(item);
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

	public static long getDataItemsCreated() {
		return dataItemsCreated;
	}
}
