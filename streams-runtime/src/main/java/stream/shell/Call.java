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
package stream.shell;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.DebugShell;
import stream.service.NamingService;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public class Call extends Command {

	static Logger log = LoggerFactory.getLogger(Call.class);

	/**
	 * @param shell
	 */
	public Call(DebugShell shell) {
		super(shell);
	}

	/**
	 * @see stream.shell.Command#execute(java.lang.String[])
	 */
	@Override
	public String execute(String[] args) {

		if (args.length < 2) {
			return "Error: command 'call' requires at least name and method of the call!";
		}

		String name = args[0];
		String method = args[1];
		String[] params = new String[0];
		if (args.length > 3) {
			params = new String[args.length - 3];
			for (int i = 0; i < params.length; i++) {
				params[i] = args[i + 3];
			}
		}

		try {
			NamingService ns = shell.getNamingService();
			Service service = ns.lookup(name, Service.class);

			for (Class<?> intf : service.getClass().getInterfaces()) {
				println("[" + intf.getCanonicalName() + "]");
				for (Method m : intf.getMethods()) {
					print("  -> public " + m.getReturnType() + " "
							+ m.getName() + "(");

					if (m.getName().equals(method)) {
						Class<?>[] types = m.getParameterTypes();
						Object[] arguments = new Object[types.length];
						if (types.length == params.length) {

							for (int i = 0; i < types.length; i++) {
								log.info(
										"Creating argument of type {} from {}",
										types[i], params[i]);
								Constructor<?> con = types[i]
										.getConstructor(String.class);
								arguments[i] = con.newInstance(params[i]);
							}
							log.debug("Invoking method {}", m);
							Object value = m.invoke(service, arguments);
							println("RESULT: " + value);
							return "";
						} else {
						}
					}

					if (m.getParameterTypes().length > 0) {
						for (int i = 0; i < m.getParameterTypes().length; i++) {
							print(m.getParameterTypes()[i] + "");
							if (i + 1 < m.getParameterTypes().length) {
								print(",");
							}
						}
					}
					println(")");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
}