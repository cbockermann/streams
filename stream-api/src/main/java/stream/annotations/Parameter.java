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
package stream.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to annotate class methods and define them as
 * parameter.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {

	/**
	 * An optional name for the parameter, which determines the <b>external</b>
	 * name of that parameter, i.e. within a configuration file.
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * An optional parameter for the parameter which states whether this
	 * parameter is optional or required. The default is <code>false</code>.
	 * 
	 * @return <code>true</code> if this parameter is required to be provided.
	 */
	boolean required() default false;

	/**
	 * The default minimum for an numerical parameters
	 * 
	 * @return The minimum value of a numerical parameter (0.0 is the default).
	 */
	double min() default 0.0d;

	/**
	 * The default maximum for any numerical parameters
	 * 
	 * @return The maximum value of a numerical parameter (Double.MAX_VALUE is
	 *         the default).
	 */
	double max() default Double.MAX_VALUE;

	/**
	 * A default list of possible values for string parameters
	 * 
	 * @return A list of possible values for this parameter, default is an empty
	 *         list (array).
	 */
	String[] values() default {};

	/**
	 * The default value as string for this parameter
	 * 
	 * @return
	 */
	String defaultValue() default "";

	/**
	 * A description of the parameter. This description is used for generating
	 * the API reference documentation and helps understanding your processor.
	 * 
	 * @return A parameter description (text).
	 */
	String description() default "";

	Class<?> type() default Object.class;

	/**
	 * The setting allows to specify runtime-expansion of parameters. This will
	 * result in the XML value for the attribute to be cached and the setter to
	 * be called with the evaluated string for each {@link stream.Data} item.
	 * 
	 * <b>Important: </b> This feature is not supported, yet!
	 * 
	 * @return <code>true</code> if the parameter should be evaluated at runtime
	 *         (for each processed item).
	 */
	boolean expandMacros() default false;
}