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
package stream.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;

/**
 * @author Christian Bockermann
 * 
 */
public class Collector implements Processor, CollectorService {

    static Logger log = LoggerFactory.getLogger(Collector.class);
    protected final ArrayList<Data> collected = new ArrayList<Data>();

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {
        collected.add(input);
        log.info("collector contains {} elements", collected.size());
        return input;
    }

    /**
     * @see stream.service.Service#reset()
     */
    @Override
    public void reset() throws Exception {
        log.info("Clearing collection (removing {} elements)", collected.size());
        collected.clear();
    }

    /**
     * @see stream.test.CollectorService#getCollection()
     */
    @Override
    public List<Data> getCollection() {
        log.info("Returning {} collected items", collected.size());
        return Collections.unmodifiableList(collected);
    }
}