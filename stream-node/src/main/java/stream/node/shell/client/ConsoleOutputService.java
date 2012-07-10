/**
 * 
 */
package stream.node.shell.client;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author chris
 * 
 */
public interface ConsoleOutputService extends RemoteService {

	/**
	 * Returns the lines of console output starting with the lines produced
	 * since <code>since</code>.
	 * 
	 * @param since
	 * @return
	 * @throws Exception
	 */
	public String[] getLines(String processId, Long since) throws Exception;
}
