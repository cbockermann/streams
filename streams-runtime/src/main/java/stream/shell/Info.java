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

import java.lang.reflect.Method;

import stream.DebugShell;
import stream.service.NamingService;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public class Info extends Command {

	/**
	 * @param shell
	 */
	public Info(DebugShell shell) {
		super(shell);
	}

	/**
	 * @see stream.shell.Command#execute(java.lang.String[])
	 */
	@Override
	public String execute(String[] args) {
		try {

			NamingService ns = shell.getNamingService();

			Service service = ns.lookup(args[0], Service.class);
			println(":> " + service);

			for (Class<?> intf : service.getClass().getInterfaces()) {
				println("[" + intf.getCanonicalName() + "]");
				for (Method m : intf.getMethods()) {
					String rt = "void";
					if (m.getReturnType() != null) {
						rt = m.getReturnType().getCanonicalName();
					}

					print("  -> public " + rt + " " + m.getName() + "(");
					if (m.getParameterTypes().length > 0) {
						for (int i = 0; i < m.getParameterTypes().length; i++) {
							print(m.getParameterTypes()[i].getCanonicalName()
									+ "");
							if (i + 1 < m.getParameterTypes().length) {
								print(",");
							}
						}
					}
					println(")");
				}

			}

		} catch (Exception e) {
			println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		return "";
	}
}
