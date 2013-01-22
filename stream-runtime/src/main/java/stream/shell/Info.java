/**
 * 
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
