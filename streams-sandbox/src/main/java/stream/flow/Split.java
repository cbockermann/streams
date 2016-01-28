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
package stream.flow;

import java.util.List;

import stream.Data;
import stream.expressions.Condition;
import stream.io.Sink;

/**
 * <p>
 * This interface defines an abstract split element. A split element is
 * essentially just like a queue, but allows for multiple consumers to connect
 * with a given condition.
 * </p>
 * <p>
 * Each consumer will then receive the items matching the condition it provided.
 * The exact behavior is left to the different implementations of {@link Split}.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public interface Split extends Sink<Data> {

    /**
     * List the conditions registered at this split point.
     * 
     * @return
     */
    public List<Condition> getConditions();

    /**
     * Adds a new sink with a given condition to the split.
     * 
     * @param condition
     * @param sink
     */
    public void add(Condition condition, Sink<Data> sink);
}
