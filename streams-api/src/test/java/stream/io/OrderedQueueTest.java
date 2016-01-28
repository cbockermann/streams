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
package stream.io;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.data.SequenceID;

/**
 * @author chris
 * 
 */
public class OrderedQueueTest {

    static Logger log = LoggerFactory.getLogger(OrderedQueueTest.class);

    public ArrayList<Data> generateDataItems() {

        SequenceID sequence = new SequenceID();
        ArrayList<Data> list = new ArrayList<Data>();

        for (int i = 0; i < 100; i++) {
            Data item = DataFactory.create();
            item.put("@id", i);
            item.put("@source:item", sequence.getAndIncrement());
            list.add(item);
        }

        Collections.shuffle(list);
        return list;

    }

    @Test
    public void test() {

        try {
            final ArrayList<Data> results = new ArrayList<Data>();
            final ArrayList<Data> inputs = generateDataItems();

            final OrderedQueue queue = new OrderedQueue();
            queue.init();

            Consumer consumer = new Consumer(queue, results);
            consumer.start();

            for (Data item : inputs) {
                log.info("Writing item '{}' to queue", item);
                queue.write(item);
            }

            log.info("Closing queue...");
            queue.close();

            log.info("Waiting for consumer to finish...");
            consumer.join();

            log.info("Consumer finished.");
            log.info("Results: {}", results);

            Data last = null;
            for (int i = 0; i < results.size(); i++) {

                if (last == null) {
                    last = results.get(i);
                    continue;
                }

                Data cur = results.get(i);

                SequenceID lastId = (SequenceID) last.get("@source:item");
                SequenceID curId = (SequenceID) cur.get("@source:item");

                SequenceID lastPlusOne = lastId.nextValue();

                log.info(" {}  ==?==  {}", lastPlusOne, curId);
                Assert.assertTrue(lastPlusOne.compareTo(curId) == 0);
                last = cur;
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("Error: " + e.getMessage());
        }
    }

    public class Consumer extends Thread {
        Logger log = LoggerFactory.getLogger(Consumer.class);
        final Source<Data> source;
        final ArrayList<Data> result;

        public Consumer(Source<Data> source, ArrayList<Data> results) {
            this.source = source;
            this.result = results;
        }

        public void run() {
            try {
                log.info("Starting consumer...");
                Data item = source.read();
                log.info("First item: {}", item);
                while (item != null) {
                    log.info("Read item {} from queue.", item);
                    result.add(item);
                    item = source.read();
                }
                log.info("Read 'null', finishing up...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
