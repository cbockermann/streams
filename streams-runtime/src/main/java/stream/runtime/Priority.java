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
package stream.runtime;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * This class defines a thread priority level which can be instantiated using an
 * integer value or a string of <code>lowest</code>, <code>low</code>,
 * <code>normal</code>, <code>high</code> or <code>highest</highest>.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public class Priority {

	static final Map<String, Integer> PRIORITY_NAMES = new LinkedHashMap<String, Integer>();
	static {
		PRIORITY_NAMES.put("lowest", Thread.MIN_PRIORITY);
		PRIORITY_NAMES.put("low", 2);
		PRIORITY_NAMES.put("normal", Thread.NORM_PRIORITY);
		PRIORITY_NAMES.put("high", 7);
		PRIORITY_NAMES.put("highest", Thread.MAX_PRIORITY);
	}

	private Integer value = Thread.NORM_PRIORITY;

	public Priority() {
		init(Thread.NORM_PRIORITY);
	}

	public Priority(Integer value) {
		init(value);
	}

	public Priority(String val) {
		init(parse(val));
	}

	private static Integer parse(String prioValue) {
		Integer prio = Thread.NORM_PRIORITY;
		try {
			if (prioValue == null)
				prioValue = "normal";

			if (PRIORITY_NAMES.containsKey(prioValue)) {
				prioValue = PRIORITY_NAMES.get(prioValue).toString();
			}

			prio = new Integer(prioValue);
		} catch (Exception e) {
			prio = Thread.NORM_PRIORITY;
		}
		return prio;
	}

	private void init(Integer prio) {

		if (prio == null) {
			value = Thread.NORM_PRIORITY;
			return;
		}

		if (prio > Thread.MAX_PRIORITY) {
			value = Thread.MAX_PRIORITY;
			return;
		}

		if (prio < Thread.MIN_PRIORITY) {
			value = Thread.MIN_PRIORITY;
		}
	}

	public Integer value() {
		return value;
	}
}
