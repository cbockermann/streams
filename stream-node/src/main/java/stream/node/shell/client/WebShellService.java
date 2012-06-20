package stream.node.shell.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("web-shell")
public interface WebShellService extends RemoteService {

	/**
	 * 
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public String execute( String command ) throws Exception;
}
