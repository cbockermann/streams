/**
 * 
 */
package stream.data;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.service.AssertionService;

/**
 * @author chris
 * 
 */
public class AssertTypes extends AbstractProcessor implements AssertionService {

	static Logger log = LoggerFactory.getLogger(AssertTypes.class);
	String id = "AssertTypes";
	String types[];
	ExpectedDataTypes expected;
	Long assertions = 0L;
	Long assertionErrors = 0L;

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);

		if (types != null && types.length > 0) {
			expected = new ExpectedDataTypes();

			for (String typeDef : types) {
				log.info("Parsing type-def {}", typeDef);
				String[] tok = typeDef.split("::");
				String key = tok[0];
				String val = tok[1];
				String type = val;
				int len = -1;
				int idx = val.lastIndexOf("[");
				if (idx > 0) {
					int end = val.indexOf("]", idx);
					if (end > 0) {
						len = new Integer(val.substring(idx + 1, end));
						type = val.substring(0, idx);
					}
				}

				Class<?> valType = Class.forName(type);

				log.info("   {} ~> {}", key, valType);

				if (len > 0) {
					//
					// need to create array of type 'type'
					//
					// Object array = Array.newInstance(valType, len);
					expected.addArray(key, valType, len);
					// expected.addType(key, array.getClass());
				} else {
					expected.addType(key, valType);
				}
			}
		}
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		if (types == null || expected == null) {
			return input;
		} else {
			assertions++;
			Boolean typesOk = expected.check(input);
			if (!typesOk)
				assertionErrors++;

			if (this.id != null) {
				input.put(id, typesOk);
			}
		}

		return input;
	}

	/**
	 * @return the types
	 */
	public String[] getTypes() {
		return types;
	}

	/**
	 * @param types
	 *            the types to set
	 */
	public void setTypes(String[] types) {
		this.types = types;
	}

	/**
	 * @author chris
	 * 
	 */
	public class ExpectedDataTypes {

		Logger log = LoggerFactory.getLogger(ExpectedDataTypes.class);
		Map<String, Class<?>> types = new LinkedHashMap<String, Class<?>>();
		Map<String, Integer> arrayLength = new LinkedHashMap<String, Integer>();

		public ExpectedDataTypes() {
		}

		public void addType(String key, Class<?> typeValue) {
			types.put(key, typeValue);
			arrayLength.remove(key);
		}

		public void addArray(String key, Class<?> typeValue, int len) {
			types.put(key, typeValue);
			arrayLength.put(key, len);
		}

		public boolean check(Data item) {
			log.info("Checking {} types for item {}", types.keySet().size(),
					item);
			for (String key : types.keySet()) {
				if (!item.containsKey(key)) {
					log.error("Missing key '{}' in item {}!", key, item);
					return false;
				}

				if (!checkType(key, item)) {
					log.error("Value type mismatch for key '" + key
							+ "', expected type: {} but found type: {}",
							types.get(key), item.get(key).getClass());
					return false;
				}
			}

			return true;
		}

		protected boolean checkType(String key, Data item) {

			if (!types.containsKey(key)) {
				return true;
			}

			if (!item.containsKey(key)) {
				log.info("No attribute found for key '{}'", key);
				return false;
			}

			Class<?> type = item.get(key).getClass();
			Class<?> expType = types.get(key);
			Integer expLength = this.arrayLength.get(key);
			if (expLength == null) {
				log.info("Checking type {} against {}", expType, item.get(key)
						.getClass());
				return expType.equals(item.get(key).getClass());
			} else {

				if (!type.isArray()) {
					log.error("Expected array type!");
					return false;
				}

				if (!type.getComponentType().equals(expType)) {
					log.error("Found array of type {} but {} was expected!",
							type, expType);
					return false;
				}

				if (expLength >= 0
						&& expLength != Array.getLength(item.get(key))) {
					int len = Array.getLength(item.get(key));
					log.error(
							"Array length mismatch! Found array of length {} but {} was expected!",
							len, expLength);
					return false;
				}
			}

			return true;
		}

		protected boolean checkValues(String key, Serializable exp,
				Serializable found) {

			if (!valuesMatch(exp, found)) {
				log.error("Value mismatch for key '{}'", key);
				return false;
			}

			return true;
		}

		private boolean valuesMatch(Serializable val1, Serializable val2) {

			if (val1 == val2)
				return true;

			if (val1 == null || val2 == null)
				return false;

			if (val1.getClass().isArray() && val2.getClass().isArray()) {
				return arraysMatch(val1, val2);
			}

			if (!val1.getClass().equals(val2.getClass())) {
				log.error("Value types differ: {} and {}", val1.getClass(),
						val2.getClass());
				return false;
			}

			return val1.equals(val2);
		}

		private boolean arraysMatch(Object array1, Object array2) {

			Class<?> ct1 = array1.getClass().getComponentType();
			Class<?> ct2 = array2.getClass().getComponentType();

			if (!array1.getClass().getComponentType()
					.equals(array2.getClass().getComponentType())) {
				log.error("Component type mismatch for arrays: {}[] and {}[]",
						ct1, ct2);
				return false;
			}

			if (Array.getLength(array1) != Array.getLength(array2)) {
				log.error("Array length mismatch!");
				return false;
			}

			log.info("Arrays equal.");
			return true;
		}
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		this.assertions = 0L;
		this.assertionErrors = 0L;
	}

	/**
	 * @see stream.service.AssertionService#getAssertions()
	 */
	@Override
	public Long getAssertions() {
		return new Long(this.assertions);
	}

	/**
	 * @see stream.service.AssertionService#getAssertionErrors()
	 */
	@Override
	public Long getAssertionErrors() {
		return new Long(this.assertionErrors);
	}
}
