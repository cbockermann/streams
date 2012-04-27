/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
package stream.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ConditionedProcessor;
import stream.ProcessContext;
import stream.annotations.Description;
import stream.data.Data;
import stream.expressions.Expression;
import stream.expressions.ExpressionResolver;
import stream.expressions.MacroExpander;
import stream.io.CsvWriter;

/**
 * @author chris
 * 
 */
@Description(text = "", group = "Data Stream.Monitoring")
public class Message extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(CsvWriter.class);

	protected Expression filter;
	protected String txt;
	protected String condition;
	protected MacroExpander macroExpander;

	public void setMessage(String msg) {
		if (msg == null)
			this.txt = "";
		else
			this.txt = msg;
	}

	public String getMessage() {
		return txt;
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.Context)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {

		if (filter == null || filter.matches(context, data)) {
			Object o = ExpressionResolver.expand(getMessage(), context, data);
			if (o != null)
				log.info(o.toString());
		}

		return data;
	}
}