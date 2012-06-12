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
package stream.runtime.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.data.Data;
import stream.service.Service;
import stream.util.SizeOf;

/**
 * @author chris
 * 
 */
public class MemorySize extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(MemorySize.class);
	String ref = null;
	String prefix = "@memory";

	/**
	 * @return the ref
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * @param ref
	 *            the ref to set
	 */
	public void setRef(String ref) {
		this.ref = ref;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		try {
			Service p = context.lookup(getRef(), Service.class );
			log.debug("checking memory of processor {}", p);
			Double size = SizeOf.sizeOf(p);
			input.put(prefix + ":" + getRef(), size);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return input;
	}

}
