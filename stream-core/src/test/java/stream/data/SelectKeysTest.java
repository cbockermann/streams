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

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Keys;
import stream.Processor;

/**
 * @author chris
 * 
 */
public class SelectKeysTest {

    static Logger log = LoggerFactory.getLogger(SelectKeysTest.class);

    @Test
    public void test() {

        WithKeys selector = new WithKeys();
        selector.setKeys(new Keys("x1,!x2,user:*,!user:name"));

        Processor check = new Processor() {
            @Override
            public Data process(Data input) {
                boolean ok = input.containsKey("x1") && !input.containsKey("x2") && input.containsKey("user:id")
                        && !input.containsKey("testKey");
                if (!ok)
                    fail("Test failed. Unexpected set of keys found in data item: " + input.keySet());
                else
                    log.info("Selection works!");

                input.put("processed", "true");

                return input;
            }
        };

        selector.getProcessors().add(check);

        Data item = DataFactory.create();
        item.put("x1", 1.0);
        item.put("x2", 2.0);
        item.put("testKey", "streams for the world!");
        item.put("user:id", "chris");
        item.put("user:name", "Christian");

        item = selector.process(item);
        log.info("Data item: {}", item);
        // fail("Not yet implemented");
    }

}
