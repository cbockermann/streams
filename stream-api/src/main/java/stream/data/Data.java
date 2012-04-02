/**
 * 
 */
package stream.data;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * This interface defines a single data item.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public interface Data extends Map<String, Serializable>, Serializable {

	/**
	 * attributes starting with this prefix are considered special and will not
	 * be regarded for training classifiers
	 */
	public final static String SPECIAL_PREFIX = "_";

	/**
	 * attributes starting with this prefix are considered as hidden and must
	 * not be processed (neither removed nor modified) by data mappers.
	 */
	public final static String HIDDEN_PREFIX = "._";

	/**
	 * Attributes starting with an '@' character are regarded as annotations,
	 * i.e. referring to data transformation parameters (normalization,...)
	 */
	public final static String ANNOTATION_PREFIX = "@";

	/**
	 * Attributes that refer to predicted values, i.e. the class of an instance
	 * predicted by some learning/prediction-model are prefixed with this
	 * string.
	 */
	public final static String PREDICTION_PREFIX = ANNOTATION_PREFIX
			+ "prediction";
}