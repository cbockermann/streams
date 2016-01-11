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

import stream.Data;
import stream.Processor;

/**
 * Test processor for xmlAnnotations Created by bruegge on 3/5/14.
 */

public class AnnotatedProcessorWithDoubleAnnotations implements Processor {

    @Parameter(name = "differentXMLName")
    private String namedParameter;

    @Parameter(required = true)
    private String nonoptional;

    @Parameter
    private String optional;

    @Override
    public Data process(Data input) {
        System.out.println("Annotation test processor:  ");
        System.out.println("nonoptional: " + nonoptional);
        System.out.println("optional: " + optional);
        return input;
    }

    // getter and setter crap for our fields

    public String getNonoptional() {
        return nonoptional;
    }

    public void setNonoptional(String nonoptional) {
        this.nonoptional = nonoptional;
    }

    public String getOptional() {
        return optional;
    }

    @Parameter
    public void setOptional(String optional) {
        this.optional = optional;
    }

    public String getNamedParameter() {
        return namedParameter;
    }

    public void setNamedParameter(String namedParameter) {
        this.namedParameter = namedParameter;
    }
}
