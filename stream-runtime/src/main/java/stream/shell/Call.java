/**
 * 
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