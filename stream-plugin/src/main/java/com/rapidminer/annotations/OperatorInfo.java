/**
 * 
 */
package com.rapidminer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * This annotation provides information about a class being able to be wrapped
 * into a Generic RapidMiner Operator.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperatorInfo {

	/**
	 * A name for the DataProcessor implementation.
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * A descriptive text.
	 * 
	 * @return
	 */
	String text() default "";

	/**
	 * A URL reference for further documentation or description.
	 * 
	 * @return
	 */
	String url() default "";

	/**
	 * A group name to which the annotated class belongs.
	 * 
	 * @return
	 */
	String group() default "";

	String icon() default "";

}
