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
package stream.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;
import stream.data.stats.CountService;

/**
 * @author chris
 * 
 */
public class PrintCounter implements Processor {

    static Logger log = LoggerFactory.getLogger(PrintCounter.class);
    CountService counter;

    /**
     * @return the counter
     */
    public CountService getCounter() {
        return counter;
    }

    /**
     * @param counter
     *            the counter to set
     */
    public void setCounter(CountService counter) {
        log.info("Received reference to counter: {}", counter);
        this.counter = counter;
    }

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {

        if (counter != null) {
            System.out.println("Counter has value: " + counter.getCount());
            if (input != null) {
                input.put("counter", counter.getCount());
            }
        }

        return input;
    }
}
