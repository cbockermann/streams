/**
 * 
 */
package stream.shell;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Shell;
import stream.runtime.rpc.ContainerAnnouncement;
import stream.runtime.rpc.Discovery;
import stream.runtime.rpc.RMIClient;
import stream.service.NamingService;
import stream.service.ServiceInfo;

/**
 * @author chris
 * 
 */
public class List extends Command {

	static Logger log = LoggerFactory.getLogger(List.class);

	Discovery discovery;

	/**
	 * @param shell
	 */
	public List(Shell shell) throws Exception {
		super(shell);
		this.discovery = new Discovery();
	}

	/**
	 * @see stream.shell.Command#execute(java.lang.String[])
	 */
	@Override
	public String execute(String[] args) {

		println("\nServices Available:\n----------\n");
		int id = 1;
		try {
			discovery.discover();
			for (String key : discovery.getAnnouncements().keySet()) {
				ContainerAnnouncement ref = discovery.getAnnouncements().get(
						key);

				NamingService remote;

				// log.debug("Adding RMI connection for container {}", ref);
				if (shell.getNamingService().getContainer(key) != null) {
					log.debug("Container already known: {}", shell
							.getNamingService().getContainer(key));
					remote = shell.getNamingService().getContainer(key);
				} else {
					remote = new RMIClient(ref.getHost(), ref.getPort());
					shell.getNamingService().addContainer(key, remote);
				}

				Map<String, ServiceInfo> infos = remote.list();
				for (String name : infos.keySet()) {
					if (!name.startsWith("//" + key + "/.")) {
						ServiceInfo info = infos.get(name);
						StringBuffer services = new StringBuffer("[");
						for (int i = 0; i < info.getServices().length; i++) {
							services.append(info.getServices()[i]
									.getCanonicalName());
							if (i + 1 < info.getServices().length)
								services.append(", ");
						}
						services.append("]");
						print("  #" + id);
						println("    " + name + "   " + services.toString());
						shell.set("#" + id, name);
						id++;
					}
				}
			}
			println("");
		} catch (Exception e) {
			println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		return "";
	}
}
