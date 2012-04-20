/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
 * <p>
 * This annotation can be used to specify documentation settings for the 
 * annotated data processor implementation.
 * </p>
 * 
 * @author Christan Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
public @interface Description {

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
	 * @return
	 */
	String group() default "";
	
	
	
	String icon() default "";
}