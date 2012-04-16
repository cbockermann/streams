/**
 * 
 */
package stream.plugin;

import com.rapidminer.operator.OperatorDescription;

/**
 * @author chris
 * 
 */
public class GenericStreamOperator extends DataStreamOperator {

	/**
	 * @param description
	 * @param clazz
	 */
	public GenericStreamOperator(OperatorDescription description, Class<?> clazz) {
		super(description, clazz);
	}
}
