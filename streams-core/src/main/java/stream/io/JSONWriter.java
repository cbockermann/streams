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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minidev.json.JSONObject;
import stream.Data;
import stream.ProcessContext;
import stream.data.DataFactory;

/**
 * <p>
 * This is a simple JSON writer that will write all data items into JSON strings
 * (one line for each item).
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class JSONWriter extends AbstractWriter {

    static Logger log = LoggerFactory.getLogger(JSONWriter.class);

    PrintStream p;

    public JSONWriter() {
        super();
    }

    public JSONWriter(File file) throws IOException {
        this(new FileOutputStream(file));
    }

    public JSONWriter(OutputStream out) throws IOException {
        p = new PrintStream(out);
    }

    /**
     * @see stream.AbstractProcessor#init(stream.ProcessContext)
     */
    @Override
    public void init(ProcessContext ctx) throws Exception {
        super.init(ctx);
        if (p == null) {
            log.debug("Opening output URL {}", getUrl());
            String file = getUrl();
            if (file.startsWith("file:")) {
                file = file.substring("file:".length());
            }
            p = new PrintStream(new FileOutputStream(new File(this.getUrl())));
        }
    }

    /**
     * @see stream.io.CsvWriter#write(stream.Data)
     */
    @Override
    public void write(Data datum) {
        Data item = DataFactory.create();
        for (String key : this.selectedKeys(datum)) {
            if (datum.containsKey(key))
                item.put(key, datum.get(key));
        }
        if (p != null) {
            p.println(JSONObject.toJSONString(item));
        } else {
            log.error("JSONWriter has not been set right: PrintStream is null.");
        }
    }
}