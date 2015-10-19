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
package stream.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Context;
import stream.ProcessContext;
import stream.service.NamingService;
import stream.service.Service;
import stream.service.ServiceInfo;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class ProcessContextImpl implements ProcessContext {

	static Logger log = LoggerFactory.getLogger(ProcessContextImpl.class);
	final ContainerContext containerContext;
	final Map<String, Object> context = new HashMap<String, Object>();

	String processId;

	public ProcessContextImpl() {
		this(UUID.randomUUID().toString());
	}

	public ProcessContextImpl(String id) {
		this.processId = id;
		containerContext = null;
	}

	public ProcessContextImpl(String id, Context ctx) {
		this(id);
	}

	public ProcessContextImpl(String id, ContainerContext ctx) {
		this.processId = id;
		containerContext = ctx;
		log.debug("Creating new ProcessContext, parent context is {}", ctx);
	}

	/**
	 * @see stream.service.NamingService#register(java.lang.String,
	 *      stream.Processor)
	 */
	public void register(String ref, Service p) throws Exception {
		if (containerContext == null)
			throw new Exception("No parent context exists!");
		containerContext.register(ref, p);
	}

	/**
	 * @see stream.service.NamingService#unregister(java.lang.String)
	 */
	public void unregister(String ref) throws Exception {
		if (containerContext == null)
			throw new Exception("No parent context exists!");
		containerContext.unregister(ref);
	}

	public Map<String, ServiceInfo> list() throws Exception {
		return containerContext.list();
	}

	/**
	 * @see stream.ProcessContext#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		return context.get(key);
	}

	/**
	 * @see stream.ProcessContext#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, Object o) {
		context.put(key, o);
	}

	/**
	 * @see stream.Context#resolve(java.lang.String)
	 */
	@Override
	public Object resolve(String variable) {
		if (!variable.startsWith("process.")) {
			if (containerContext == null)
				return null;
			log.debug("resolving '{}' with parent context {}", variable, containerContext);
			return containerContext.resolve(variable);
		}

		return get(variable.substring("process.".length()));
	}

	/**
	 * @see stream.service.NamingService#addContainer(java.lang.String,
	 *      stream.service.NamingService)
	 */
	public void addContainer(String key, NamingService remoteNamingService) throws Exception {
		throw new Exception("Addition of remote naming services is not supported by local context!");
	}

	@Override
	public void clear() {
		context.clear();
	}

	@Override
	public boolean contains(String key) {
		return context.containsKey(key);
	}

	/**
	 * @see stream.Context#getId()
	 */
	@Override
	public String getId() {
		return this.processId;
	}
}