/**
 * 
 */
package stream.node.shell.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author chris
 * 
 */
public interface ContainerServiceAsync {

	void list(AsyncCallback<List<ContainerInfo>> callback);

}
