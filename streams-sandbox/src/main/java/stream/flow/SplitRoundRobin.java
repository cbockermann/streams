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

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.Sink;

/**
 * <p>
 * A split-point following a round-robin strategy.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class SplitRoundRobin extends SplitByRandom {

    static Logger log = LoggerFactory.getLogger(SplitRoundRobin.class);

    protected AtomicInteger lastIndex = new AtomicInteger(0);

    /**
     * @see stream.flow.SplitByRandom#write(stream.Data)
     */
    @Override
    public boolean write(Data item) throws Exception {

        int idx = lastIndex.getAndIncrement();
        Sink<Data> destination = sinks.get(idx % sinks.size());
        log.debug("Current index '{}' ~> {}", idx % sinks.size(), destination);

        return destination.write(item);
    }
}
