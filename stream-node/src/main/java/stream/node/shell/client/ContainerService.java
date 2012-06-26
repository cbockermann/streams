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

	public List<ContainerInfo> list();

}
