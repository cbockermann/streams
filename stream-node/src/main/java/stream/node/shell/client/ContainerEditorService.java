/**
 * 
 */
package stream.node.shell.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author chris
 * 
 */
@RemoteServiceRelativePath("container-editor")
public interface ContainerEditorService extends RemoteService {

	public Map<String, ElementDescription> getAvailableElements();

	public List<String> listFiles();

	public String readFile(String file) throws Exception;

	public boolean writeFile(String name, String xml) throws Exception;

	public String start(String file) throws Exception;

	public String stop(String id) throws Exception;
}
