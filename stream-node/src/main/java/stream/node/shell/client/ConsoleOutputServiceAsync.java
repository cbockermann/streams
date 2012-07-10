/**
 * 
 */
package stream.node.shell.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author chris
 * 
 */
public interface ConsoleOutputServiceAsync {

	void getLines(String processId, Long since, AsyncCallback<String[]> callback);

}
