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
package stream.script;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.BodyContent;
import stream.runtime.setup.ObjectCreator;
import stream.util.Variables;

/**
 * @author chris
 * 
 */
public class JavaScriptProcessorFactory implements ObjectCreator {

	static Logger log = LoggerFactory
			.getLogger(JavaScriptProcessorFactory.class);

	/**
	 * @see stream.runtime.setup.ObjectCreator#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return "js:";
	}

	/**
	 * @see stream.runtime.setup.ObjectCreator#create(java.lang.String,
	 *      java.util.Map)
	 */
	@Override
	public Object create(String className, Map<String, String> parameters,
			Variables local) throws Exception {

		log.info("Request for creating {}", className);
		String res = className.substring(3);
		log.info("  expecting resource: {}", res);

		JavaScript processor = new JavaScript();
		processor.setScript(new BodyContent(parameters.get(BodyContent.KEY)));
		return processor;
	}
}