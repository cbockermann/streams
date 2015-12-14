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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Keys;
import stream.ProcessorList;
import stream.annotations.Parameter;

/**
 * @author chris
 * 
 */
public class WithKeys extends ProcessorList {

    static Logger log = LoggerFactory.getLogger(WithKeys.class);
    Keys keys = null;

    Set<String> selected = new HashSet<String>();
    private Boolean merge = true;

    public WithKeys() {
        super();
        this.merge = true;
    }

    @Parameter(description = "A list of filter keys selecting the attributes that should be provided to the inner processors.")
    public void setKeys(Keys keys) {
        this.keys = keys;
    }

    public Keys getKeys() {
        return keys;
    }

    public Boolean getMerge() {
        return merge;
    }

    @Parameter(description = "Indicates whether the outcome of the inner processors should be merged into the input data item, defaults to true.")
    public void setMerge(Boolean join) {
        this.merge = join;
    }

    /**
     * @see stream.DataProcessor#process(stream.Data)
     */
    @Override
    public Data process(Data data) {

        Data innerItem = DataFactory.create();

        Set<String> ks = keys.select(data);
        for (String k : ks) {
            innerItem.put(k, data.get(k));
        }

        Data processed = super.process(innerItem);
        if (merge != null && !merge) {
            return processed;
        }

        if (merge == null || (merge && processed != null)) {
            for (String key : processed.keySet()) {
                data.put(key, processed.get(key));
            }

            Set<String> k = data.keySet();
            Iterator<String> it = k.iterator();
            while (it.hasNext()) {
                String str = it.next();
                if (!processed.containsKey(str) && ks.contains(str)) {
                    it.remove();
                }
            }
        }
        return data;
    }
}