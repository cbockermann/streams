/**
 * 
 */
package stream.data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.Measurable;
import stream.util.SizeOf;

/**
 * @author chris
 * 
 */
public class DataImpl extends LinkedHashMap<String, Serializable> implements
		Data, Measurable {

	/** The unique class ID */
	private static final long serialVersionUID = -7751681008628413236L;

	/**
	 * @deprecated Creation of Data items should be done with
	 *             {@link stream.data.DataFactory#create()}
	 */
	public DataImpl() {
	}

	/**
	 * @param data
	 * @deprecated Creation of Data items should be done with
	 *             {@link stream.data.DataFactory#create()}
	 */
	public DataImpl(Map<String, Serializable> data) {
		super(data);
	}

	/**
	 * @see stream.Measurable#getByteSize()
	 */
	@Override
	public double getByteSize() {

		double size = 0.0d;

		for (String key : keySet()) {
			size += key.length() + 1; // provide the rough size of one byte for
										// each character + a single terminating
										// 0-byte

			// add the size of each value of this map
			//
			Serializable value = get(key);
			size += SizeOf.sizeOf(value);
		}

		return size;
	}
}
