/**
 * 
 */
package stream.util;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import stream.Measurable;

/**
 * @author chris
 * 
 */
public class SizeOf {

	public static double sizeOf(Object o) {

		if (o instanceof Measurable) {
			return ((Measurable) o).getByteSize();
		} else {
			if (o instanceof Serializable) {
				return sizeOf((Serializable) o);
			}
		}

		return Double.NaN;
	}

	private static double sizeOf(Serializable o) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			oos.close();
			return baos.size();
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		}
	}
}
