/**
 * 
 */
package stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author chris
 * 
 */
public class RunJava {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		URLClassLoader ucl = (URLClassLoader) Start.class.getClassLoader();
		StringBuffer classPath = new StringBuffer();

		URL[] urls = ucl.getURLs();
		for (int i = 0; i < urls.length; i++) {
			System.out.println("   " + urls[i]);

			if (urls[i].toURI().getScheme().equalsIgnoreCase("file")) {
				classPath.append(urls[i].toURI().getPath());
				if (i + 1 < urls.length)
					classPath.append(File.pathSeparatorChar);
			}
		}

		System.out.println("Classpath: " + classPath.toString());

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

		ProcessBuilder pb = new ProcessBuilder(java.getAbsolutePath(), " -cp "
				+ classPath, "-version");
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
