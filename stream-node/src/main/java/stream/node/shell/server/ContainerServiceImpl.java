/**
 * 
 */
package stream.node.shell.server;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.node.StreamNodeContext;
import stream.node.runtime.RuntimeManager;
import stream.node.shell.client.Configuration;
import stream.node.shell.client.ContainerInfo;
import stream.node.shell.client.ContainerService;
import stream.runtime.Controller;
import stream.runtime.rpc.ContainerAnnouncement;
import stream.runtime.rpc.Discovery;
import stream.runtime.rpc.RMIClient;

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
			ContainerInfo info = new ContainerInfo(an.getName(),
					an.getProtocol() + "://" + an.getHost() + ":"
							+ an.getPort());
			info.setStatus("running");
			list.add(info);
		}

		log.info("Found {} active containers", list.size());
		return list;
	}

	/**
	 * @see stream.node.shell.client.ContainerService#shutdown(java.lang.String)
	 */
	@Override
	public Boolean shutdown(String containerName) throws Exception {

		for (ContainerAnnouncement an : discovery.getAnnouncements().values()) {
			if (an.getName().equals(containerName)) {

				try {
					RMIClient client = new RMIClient(an.getHost(), an.getPort());
					log.info("Created new RMI client connection...");
					Controller controller = client.lookup(".ctrl",
							Controller.class);
					log.info("controller is: {}", controller);
					controller.shutdown();
					log.info("shutdown signal sent.");
					return true;
				} catch (Exception e) {
					log.error("Failed to initiate shutdown: " + e.getMessage());
					e.printStackTrace();
					throw new Exception("Failed to initiate shutdown: "
							+ e.getMessage());
				}
			}
		}

		throw new Exception("No container found for name '" + containerName
				+ "'!");
	}

	/**
	 * @see stream.node.shell.client.ContainerService#getConfigurations()
	 */
	@Override
	public List<Configuration> getConfigurations() {

		List<Configuration> configs = new ArrayList<Configuration>();

		File[] files = StreamNodeContext.getConfigDirectory().listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".xml")) {
				Configuration config = new Configuration();
				config.setName(file.getName());
				config.setFile(file.getAbsolutePath());
				configs.add(config);
			}
		}

		return configs;
	}

	/**
	 * @see stream.node.shell.client.ContainerService#startContainer(java.lang.String)
	 */
	@Override
	public void startContainer(String filename) throws Exception {

		File file = new File(filename);
		log.info("call to start of container '{}', file should be {}",
				filename, file);
		if (file.isFile()) {
			log.info("Deploying file {}", file);
			RuntimeManager.getInstance().deploy(file);
		} else {
			throw new Exception("No container configuration found for '"
					+ filename + "'!");
		}
	}
}