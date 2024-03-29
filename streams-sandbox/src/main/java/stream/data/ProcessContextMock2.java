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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import stream.Context;
import stream.ProcessContext;

/**
 * @author chris
 * @deprecated Why do we have this class here?????
 */
public class ProcessContextMock2 implements ProcessContext {

	final String id = UUID.randomUUID().toString();
	final Map<String, Object> ctx = new HashMap<String, Object>();

	/**
	 * @see stream.Context#resolve(java.lang.String)
	 */
	@Override
	public Object resolve(String variable) {
		if (variable.startsWith("process."))
			return get(variable.substring("process.".length()));
		return null;
	}

	// /**
	// * @see stream.service.NamingService#lookup(java.lang.String)
	// */
	// @SuppressWarnings("unchecked")
	// @Override
	// public <T extends Service> T lookup(String ref, Class<T> serviceClass)
	// throws Exception {
	// return (T) services.get(ref);
	// }

	/**
	 * @see stream.ProcessContext#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		return ctx.get(key);
	}

	/**
	 * @see stream.ProcessContext#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, Object o) {
		if (key != null)
			ctx.put(key, o);
	}

	@Override
	public void clear() {
		ctx.clear();
	}

	@Override
	public boolean contains(String key) {
		return ctx.containsKey(key);
	}

	/**
	 * @see stream.Context#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.Context#getParent()
	 */
	@Override
	public Context getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see stream.Context#path()
	 */
	@Override
	public String path() {
		if (getParent() != null) {
			return this.getParent().path() + Context.PATH_SEPARATOR + "process:" + getId();
		} else {
			return "process:" + getId();
		}
	}
}
