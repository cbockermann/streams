/**
 * 
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
