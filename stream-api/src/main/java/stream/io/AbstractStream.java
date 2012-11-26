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
package stream.io;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.annotations.Parameter;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public abstract class AbstractStream implements Stream {
	static Logger log = LoggerFactory.getLogger(AbstractStream.class);

	protected SourceURL url;
	protected Long limit = -1L;
	protected Long count = 0L;
	protected String prefix = null;
	protected String id;
	protected InputStream in;

	public AbstractStream(SourceURL url) {
		this.url = url;
	}

	public AbstractStream(InputStream in) {
		this.in = in;
	}

	protected AbstractStream() {
		this.url = null;
	}

	protected InputStream getInputStream() throws Exception {
		if (in == null) {
			in = url.openStream();
		}
		return in;
	}

	public String getId() {
		return id;
	}

	@Parameter(required = true, description = "The ID of this stream for associating it with processes.")
	public void setId(String id) {
		this.id = id;
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
	@Parameter(required = false, description = "An optional prefix string to prepend to all attribute names.", defaultValue = "")
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Long getLimit() {
		return limit;
	}

	@Parameter(required = false, description = "The maximum number of items that this stream should deliver.", defaultValue = "-1", max = Long.MAX_VALUE)
	public void setLimit(Long limit) {
		this.limit = limit;
	}

	/**
	 * @see stream.io.Stream#read()
	 */
	public Data read() throws Exception {

		if (limit > 0 && count >= limit)
			return null;

		Data datum = null;
		while (datum == null) {

			//
			// If the source is empty (i.e. readItem(..) returned null), we
			// cannot continue, so we leave by returning null
			//
			datum = readNext();
			if (datum == null) {
				log.debug("End-of-stream reached!");
				return null;
			}
			datum.put("@stream", this.id);

			if (prefix != null && !prefix.trim().isEmpty()) {
				Data prefixed = DataFactory.create();
				for (String key : datum.keySet()) {
					prefixed.put(prefix + ":" + key, datum.get(key));
				}
				datum = prefixed;
			}
		}
		count++;
		return datum;
	}

	public abstract Data readNext() throws Exception;

	/**
	 * @see stream.io.Source#init()
	 */
	@Override
	public void init() throws Exception {
	}

	/**
	 * @see stream.io.Source#close()
	 */
	@Override
	public void close() throws Exception {
	}
}