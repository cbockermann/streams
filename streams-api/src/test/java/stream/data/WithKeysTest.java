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

import org.junit.Test;

import stream.Data;
import stream.Keys;
import stream.Processor;

/**
 * @author chris
 * 
 */
public class WithKeysTest {

    @Test
    public void test() {

        WithKeys wk = new WithKeys();
        wk.setKeys(new Keys("*,!@fertig"));

        Data item = DataFactory.create();
        item.put("x1", 1.0);
        item.put("@fertig", "true");

        RenameKey rk = new RenameKey();
        rk.setFrom("x1");
        rk.setTo("x2");
        wk.getProcessors().add(rk);

        Processor p = new Processor() {
            public Data process(Data item) {
                if (item.containsKey("@fertig"))
                    throw new RuntimeException("assertion failed! '@fertig' must not be present!");
                return item;
            }
        };
        wk.getProcessors().add(p);

        item = wk.process(item);
        System.out.println("item: " + item);
    }
}
