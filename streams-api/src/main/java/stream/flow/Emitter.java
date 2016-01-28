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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.expressions.version2.ConditionedProcessor;
import stream.io.Sink;

/**
 * @author Hendrik Blom
 *
 */
public class Emitter extends ConditionedProcessor {

    static Logger log = LoggerFactory.getLogger(Enqueue.class);

    protected Sink<Data>[] sinks;

    protected String[] keys;
    protected boolean skip = false;

    @Override
    public void init(ProcessContext ctx) throws Exception {
        super.init(ctx);
        if (sinks == null)
            throw new IllegalArgumentException("sinks are not set");
    }

    /**
     * @throws Exception
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data processMatchingData(Data data) throws Exception {
        emit(data);
        return data;
    }

    protected int emit(Data data) {
        int written = 0;
        for (Sink<Data> sink : sinks) {
            Data d = data.createCopy();
            try {
                log.debug("emitting to {}", sink.getId());
                if (sink.write(d)) {
                    written++;
                } else {
                    log.warn("Failed to write item '{}' to queue {}", d, sink.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return written;
    }

    protected int emit(Data[] data) {
        int written = 0;
        for (Sink<Data> sink : sinks) {
            try {
                if (sink.write(Arrays.asList(data))) {
                    written += data.length;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return written;
    }

    @Override
    public String toString() {
        return "Emitter [sinks=" + Arrays.toString(sinks) + ", keys=" + Arrays.toString(keys) + "]";
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    @SuppressWarnings("unchecked")
    public void setSink(Sink<Data> sink) {
        if (sink != null) {
            this.keys = new String[] {};
            this.sinks = (Sink<Data>[]) new Sink<?>[] { sink };
        }
    }

    public void setSinks(Sink<Data>[] sinks) {
        if (sinks != null) {
            this.sinks = sinks;
        }
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }
}
