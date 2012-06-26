/**
 * 
 */
package stream.node.shell.server;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.node.shell.client.ContainerInfo;
import stream.node.shell.client.ContainerService;
import stream.runtime.rpc.ContainerAnnouncement;
import stream.runtime.rpc.Discovery;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author chris
 * 
 */
public class ContainerServiceImpl extends RemoteServiceServlet implements
		ContainerService {

	/** The unique class ID */
	private static final long serialVersionUID = -130342570031381107L;
	static Logger log = LoggerFactory.getLogger(ContainerServiceImpl.class);
	private Discovery discovery;

	public ContainerServiceImpl() throws Exception {
		discovery = new Discovery();
	}

	protected void discover() {
		try {

			log.info("auto-discovery of remote containers...");

			if (discovery == null)
				discovery = new Discovery();

			discovery.discover();

			for (String key : discovery.getAnnouncements().keySet()) {

				ContainerAnnouncement an = discovery.getAnnouncements()
						.get(key);

				log.info("Found container: {}", an);

				if ("rmi".equalsIgnoreCase(an.getProtocol())) {
					log.info("Adding container '{}' at rmi://{}/", key,
							an.getHost() + ":" + an.getPort());
				} else {
					log.error(
							"container-refs with protocol {} are not supported!",
							an.getProtocol());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws URISyntaxException
	 * @see stream.node.shell.client.ContainerService#list()
	 */
	@Override
	public List<ContainerInfo> list() {
		List<ContainerInfo> list = new ArrayList<ContainerInfo>();

		discover();

		for (ContainerAnnouncement an : discovery.getAnnouncements().values()) {
			list.add(new ContainerInfo(an.getName(), an.getProtocol() + "://"
					+ an.getHost() + ":" + an.getPort()));
		}

		return list;
	}
}
