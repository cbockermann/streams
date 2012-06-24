/**
 * 
 */
package stream.node.shell.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author chris
 * 
 */
public interface ContainerEditorServiceAsync {

	void getAvailableElements(
			AsyncCallback<Map<String, ElementDescription>> callback);

	void listFiles(AsyncCallback<List<String>> callback);

	void readFile(String file, AsyncCallback<String> callback);

	void writeFile(String name, String xml, AsyncCallback<Boolean> callback);

	void start(String file, AsyncCallback<String> callback);

	void stop(String id, AsyncCallback<String> callback);

}
