package stream.node.shell.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WebShellServiceAsync {

	void execute(String command, AsyncCallback<String> callback);

}
