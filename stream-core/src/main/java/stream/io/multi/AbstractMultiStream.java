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
package stream.io.multi;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.AbstractStream;
import stream.io.SourceURL;
import stream.io.Stream;
import stream.io.active.ActiveStream;
import stream.io.active.SimpleActiveStream;

/**
 * <p>
 * A simple multi stream implementation.
 * </p>
 * 
 * @author Hendrik Blom
 * 
 */
public abstract class AbstractMultiStream extends AbstractStream implements
		MultiStream {

	protected Logger log = LoggerFactory.getLogger(AbstractMultiStream.class);
	protected List<String> additionOrder;
	protected Map<String, Stream> streams;
	protected Boolean activate = false;
	protected ActiveStream activeWrapper;
	ExecutorService pool;

	public AbstractMultiStream(SourceURL url) {
		super(url);
	}

	public AbstractMultiStream(InputStream in) {
		super(in);
	}

	public AbstractMultiStream() {
		super();
	}

	public Boolean getActivate() {
		return activate;
	}

	public void setActivate(Boolean activate) {
		this.activate = activate;
	}

	@Override
	public void addStream(String id, Stream stream) {
		if (streams == null) {
			this.streams = new HashMap<String, Stream>();
			this.additionOrder = new ArrayList<String>();
		}

		streams.put(id, stream);
		additionOrder.add(id);
		log.debug("added Stream {}", stream);
	}

	@Override
	public Map<String, Stream> getStreams() {
		return streams;
	}

	/**
	 * @see stream.io.Stream#init()
	 */
	@Override
	public void init() throws Exception {

		

		for (Stream s : streams.values()) {
			s.init();
		}
		log.info("initialized all Streams.");
		if (activate) {
			// TODO CentralThreadPool?
			this.pool = Executors.newFixedThreadPool(1);
			this.activeWrapper = new SimpleActiveStream(this, pool);
			this.activeWrapper.activate();
			log.info("Activated this multiStream.");
		}

	}

	public void close() throws Exception {
		for (Stream s : streams.values()) {
			try {
				s.close();
			} catch (Exception e) {
				log.error("Failed to close stream {}: {}", s, e.getMessage());
			}
		}
	}

}