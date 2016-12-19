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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.expressions.ExpressionResolver;

/**
 * @author chris
 * 
 */
public class LogData extends AbstractProcessor {

    static Logger log = LoggerFactory.getLogger(LogData.class);

    String message;

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @see stream.AbstractProcessor#init(stream.ProcessContext)
     */
    @Override
    public void init(ProcessContext ctx) throws Exception {
        super.init(ctx);
    }

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {

        if (message != null) {
            String text = ExpressionResolver.expand(message, context, input);
            log.info("Message: '{}'", text);
        }
        log.info("  Data: {}", input);
        return input;
    }
}
