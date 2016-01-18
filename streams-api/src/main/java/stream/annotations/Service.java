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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark fields as service reference. A field marked
 * as service reference will be populated by looking up the property value of
 * the annotation 'name' field and inject the field with the service that is
 * returned using this 'name' value.
 * 
 * If the 'name' value is empty, the field-name will be used.
 * 
 * 
 * @author Christian Bockermann
 * 
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    /**
     * An optional name for the parameter, which determines the <b>external</b>
     * name of that parameter, i.e. within a configuration file.
     * 
     * @return
     */
    String name() default "";

    /**
     * By default, all service annotations define required references. This
     * attribute allow marking a service reference as optional.
     */
    boolean required() default true;

    /**
     * An optional description, e.g. the use of the service referenced by this.
     */
    String description() default "";
}