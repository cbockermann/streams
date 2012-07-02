/**
 * 
 */
package stream.node;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * @author chris
 * 
 */
public class RunJava {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println("JAVA_HOME = " + System.getenv("JAVA_HOME"));
		for (String key : System.getenv().keySet()) {
			System.out.println(key + " = " + System.getenv(key));
		}

		System.out
				.println("------------------------------------------------------");
		for (Object key : System.getProperties().keySet()) {
			System.out.println(key + " = " + System.getProperty(key + ""));

		}

		String exec = System.getProperty("java.home") + File.separator + "bin"
				+ File.separator + "java";
		File java = new File(exec);
		System.out.println("file " + java + " exists? " + java.exists());

		ProcessBuilder pb = new ProcessBuilder(java.getAbsolutePath(),
				"-version");
		Process jvm = pb.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				jvm.getErrorStream()));
		String line = reader.readLine();
		while (line != null) {
			System.out.println(line);
			line = reader.readLine();
		}

		int code = jvm.waitFor();
		reader.close();
		System.out.println("exit code: " + code);
	}
}
