/**
 * 
 */
package stream.shell;

import stream.Shell;

/**
 * @author chris
 * 
 */
public class ShellTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Shell shell = new Shell();
		shell.repl(System.in, System.out);
	}
}
