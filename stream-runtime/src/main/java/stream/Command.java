/**
 * 
 */
package stream;

import java.util.List;

/**
 * @author chris
 * 
 */
public interface Command {

	public void execute(List<String> args) throws Exception;
}
