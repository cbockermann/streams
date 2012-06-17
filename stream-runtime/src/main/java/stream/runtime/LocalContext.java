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
package stream.runtime;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.ProcessContext;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public class LocalContext implements ProcessContext {

	final Map<String, Service> lookupService = new HashMap<String, Service>();
	final Map<String, Object> context = new HashMap<String, Object>();

	/**
	 * @see stream.service.NamingService#lookup(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Service> T lookup(String ref, Class<T> serviceClass ) throws Exception {
		return (T) lookupService.get(ref);
	}

	/**
	 * @see stream.service.NamingService#register(java.lang.String,
	 *      stream.Processor)
	 */
	@Override
	public void register(String ref, Service p) throws Exception {
		lookupService.put(ref, p);
	}

	/**
	 * @see stream.service.NamingService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		lookupService.remove(ref);
	}

	/**
	 * @see stream.ProcessContext#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		return context.get(key);
	}

	/**
	 * 
	 * @see stream.ProcessContext#set(java.lang.String,
	 *      java.lang.Object)
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
		return get(variable);
	}


	@Override
	public Map<String, String> list() throws Exception {
		Map<String,String> classes = new LinkedHashMap<String,String>();
		for( String key : lookupService.keySet() ){
			classes.put( key, lookupService.get( key ).getClass().getSimpleName() );
		}
		return classes;
	}
}
