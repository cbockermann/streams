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

import java.net.URL;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;
import stream.test.CounterTestService;

/**
 * @author chris
 * 
 */
public class EveryTest {

    static Logger log = LoggerFactory.getLogger(EveryTest.class);

    @Test
    public void test() throws Exception {

        String uuid = UUID.randomUUID().toString();
        System.setProperty("test-every-uuid", uuid);
        URL url = EveryTest.class.getResource("/test-every.xml");
        ProcessContainer c = new ProcessContainer(url);

        long time = c.execute();
        log.info("Container required {} ms for running.", time);

        CounterTestService s = c.getContext().lookup("counter-" + uuid, CounterTestService.class);
        log.info("# of counted items: {} / expected: {}", s.getCount(), 10);
        Assert.assertEquals(10, s.getCount());
    }
}