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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.expressions.Condition;
import stream.io.Sink;

/**
 * <p>
 * A split-point that randomly distributes elements uniformly among the list of
 * registered sink.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class SplitByRandom extends AbstractSplit {

    static Logger log = LoggerFactory.getLogger(SplitByRandom.class);
    protected final ArrayList<Sink<Data>> sinks = new ArrayList<Sink<Data>>();
    protected final Random random = new Random();

    /**
     * @see stream.io.Sink#write(stream.Data)
     */
    @Override
    public boolean write(Data item) throws Exception {

        int idx = random.nextInt(sinks.size());
        Sink<Data> sink = sinks.get(idx);
        if (sink != null) {
            log.debug("Sending item to sink {}", sink.getId());
            return sink.write(item);
        } else {
            log.error("No sink found for index '{}'", idx);
        }
        return false;
    }

    /**
     * @see stream.flow.Split#getConditions()
     */
    @Override
    public List<Condition> getConditions() {
        return Collections.unmodifiableList(new ArrayList<Condition>());
    }

    /**
     * @see stream.flow.Split#add(stream.expressions.Condition, stream.io.Sink)
     */
    @Override
    public void add(Condition condition, Sink<Data> sink) {
        sinks.add(sink);
    }

    @Override
    public void close() throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * @see stream.io.Sink#init()
     */
    @Override
    public void init() throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * @see stream.io.Sink#write(java.util.Collection)
     */
    @Override
    public boolean write(Collection<Data> data) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }
}
