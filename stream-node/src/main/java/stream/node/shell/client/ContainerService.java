/**
 * 
 */
package stream.node.shell.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author chris
 * 
 */
@RemoteServiceRelativePath("container-service")
public interface ContainerService extends RemoteService {

	public List<Configuration> getConfigurations();

	public void startContainer(String name) throws Exception;

	public Boolean shutdown(String containerName) throws Exception;

	public List<ContainerInfo> list();
}
