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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;
import stream.io.Sink;

/**
 * @author chris
 * 
 */
public class Unfold implements Processor {

    static Logger log = LoggerFactory.getLogger(Unfold.class);
    String key = null;
    Sink<Data>[] output;

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {

        if (key == null || output == null) {
            return input;
        }

        Serializable value = input.get(key);
        if (value == null)
            return input;

        if (value.getClass().isArray()) {

            for (int i = 0; i < Array.getLength(value); i++) {

                Object obj = Array.get(value, i);
                if (obj == null) {
                    continue;
                }

                if (obj instanceof Serializable) {
                    Data iteration = input.createCopy();
                    iteration.put(key, (Serializable) obj);
                    emit(iteration);
                } else {
                    log.warn("Cannot unfold item on key '{}' -- value '{}' for key is not serializable!", key, obj);
                }
            }

            return input;
        }

        if (value instanceof Collection) {

            Collection<?> col = (Collection<?>) value;
            Iterator<?> it = col.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj == null)
                    continue;

                if (obj instanceof Serializable) {
                    Data iteration = input.createCopy();
                    iteration.put(key, (Serializable) obj);
                    emit(iteration);
                } else {
                    log.warn("Cannot unfold item on key '{}' -- value '{}' for key is not serializable!", key, obj);
                }
            }

            return input;
        }

        emit(input.createCopy());
        return input;
    }

    public void emit(Data item) {
        if (output == null) {
            return;
        }

        for (Sink<Data> sink : output) {
            if (sink != null) {
                try {
                    sink.write(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the output
     */
    public Sink<Data>[] getOutput() {
        return output;
    }

    /**
     * @param output
     *            the output to set
     */
    public void setOutput(Sink<Data>[] output) {
        this.output = output;
    }
}
