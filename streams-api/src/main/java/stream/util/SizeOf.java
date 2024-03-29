/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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
package stream.util;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import stream.Measurable;

/**
 * @author chris
 * 
 */
public class SizeOf {

	public final static Map<Class<?>, Double> basicSizes = new HashMap<Class<?>, Double>();
	static {
		basicSizes.put(int.class, 4.0);
		basicSizes.put(Integer.class, 4.0);
		basicSizes.put(long.class, 8.0);
		basicSizes.put(Long.class, 8.0);
		basicSizes.put(double.class, 8.0);
		basicSizes.put(Double.class, 8.0);
		basicSizes.put(char.class, 2.0);
		basicSizes.put(Character.class, 2.0);
	}

	public static double sizeOf(Object o) {

		Class<?> clazz = o.getClass();

		if (basicSizes.containsKey(clazz))
			return basicSizes.get(clazz);

		if (o.getClass().isArray()
				&& basicSizes.containsKey(clazz.getComponentType())) {
			return Array.getLength(o)
					* basicSizes.get(clazz.getComponentType());
		}

		if (o instanceof String) {
			String str = (String) o;
			return str.getBytes().length;
		}

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
