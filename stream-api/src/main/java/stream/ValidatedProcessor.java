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
package stream;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract processor allows for type-checking the input data.
 * 
 * @author Christian Bockermann
 * 
 */
public abstract class ValidatedProcessor extends AbstractProcessor {

	final static Logger log = LoggerFactory.getLogger(ValidatedProcessor.class);

	final Map<String, Class<?>> types = new HashMap<String, Class<?>>();

	String[] requires;

	public final void requires(String key) {
		requires(key, Object.class);
	}

	public final void requires(String key, Class<?> type) {
		types.put(key, type);
	}

	/**
	 * @return the requires
	 */
	public final String[] getRequires() {
		return requires;
	}

	/**
	 * @param requires
	 *            the requires to set
	 */
	public final void setRequires(String[] requires) {
		this.requires = requires;

		for (String rq : requires) {

			Class<?> type = Object.class;
			String name = rq;

			String[] tok = rq.split(":", 2);
			if (tok.length == 2) {
				name = tok[0].trim();
				type = findClassForName(tok[1]);
			}

			requires(name, type);
		}
	}

	protected Class<?> findClassForName(String name) {

		String[] packages = new String[] { "", "java.lang" };

		for (String pkg : packages) {
			try {

				String cn = name.trim();
				if (!pkg.trim().isEmpty()) {
					cn = pkg.trim() + "." + name.trim();
				}

				Class<?> clazz = Class.forName(cn);
				if (clazz != null) {
					log.debug("Found '{}' => {}", name, clazz);
					return clazz;
				}
			} catch (Exception e) {
				log.debug("Failed to find class for '{}'", name);
			}
		}

		return null;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public final Data process(Data input) {

		for (String key : types.keySet()) {

			if (!input.containsKey(key)) {
				throw new RuntimeException("Required key '" + key
						+ "' not present in input data!");
			}

			Serializable val = input.get(key);
			Class<?> type = types.get(key);

			if (!type.isAssignableFrom(val.getClass())) {
				throw new RuntimeException("");
			}
		}

		return processItem(input);
	}

	public abstract Data processItem(Data input);
}
