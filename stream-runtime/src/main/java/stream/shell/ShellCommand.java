/**
 * 
 */
package stream.shell;

import java.util.List;

/**
 * @author chris
 */
public interface ShellCommand {

	public void execute(List<String> args) throws Exception;
}
