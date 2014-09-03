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
package stream.runtime.rpc;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.MD5;

public class RMIServiceDelegator implements InvocationHandler, Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -6450480644005423871L;

	static Logger log = LoggerFactory.getLogger(RMIServiceDelegator.class);

	/** The final remote endpoint for invocation */
	final RemoteEndpoint endpoint;

	public RMIServiceDelegator(RemoteEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		try {

			if (method.getName().equals("toString") && args == null) {
				return this.toString();
			}

			log.trace("received invoke-request, method: {}, args: {}",
					method.getName(), args);
			log.trace("   object reference is: {}", proxy);
			log.trace("   arg-types: {}", (Object[]) method.getParameterTypes());

			if (args != null
					&& !(args.getClass().getComponentType() instanceof Serializable)) {
				log.error("Arguments are not serializable!");
			}

			List<Serializable> params = new ArrayList<Serializable>();
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					params.add((Serializable) args[i]);
				}
			}

			log.trace("Calling endpoint {} with {}", endpoint, method);
			Object result;
			String signature = computeSignature(method);

			if (method.getParameterTypes().length == 0) {
				result = endpoint.call(method.getName(), signature,
						new ArrayList<Serializable>());
			} else
				result = endpoint.call(method.getName(), signature, params);
			return result;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String computeSignature(Method m) {
		StringBuffer s = new StringBuffer();

		s.append(m.getName() + "(");
		Class<?> types[] = m.getParameterTypes();
		for (int i = 0; i < types.length; i++) {
			s.append(types[i].toString());
			if (i + 1 < types.length)
				s.append(",");
		}
		s.append(")");

		log.trace("Method {} signature string is: {}", m, s);

		return MD5.md5(s.toString());
	}
}