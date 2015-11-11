/**
 * 
 */
package streams.tikz;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class TexCompile {

	static Logger log = LoggerFactory.getLogger(TexCompile.class);

	public static File locatePdfLatex() {

		String[] paths = "/usr/local/bin:/usr/bin".split(":");
		for (String path : paths) {

			File pdflatex = new File(path + File.separator + "pdflatex");
			if (pdflatex.canExecute()) {
				return pdflatex;
			}
		}
		return null;
	}

	public static File pdfCompile(File tex) throws Exception {

		File pdfLatex = locatePdfLatex();
		if (pdfLatex == null) {
			throw new Exception("'pdflatex' binary not found!");
		}

		File directory = tex.getParentFile();
		if (!directory.isDirectory()) {
			throw new Exception("Output directory '" + directory + "' does not exist!");
		}

		String cmd = pdfLatex.getAbsolutePath() + " -output-directory " + directory.getAbsolutePath() // +
																										// "
																										// -interaction=batchmode
																										// "
				+ tex.getAbsolutePath();
		log.info("Running command:\n\t'{}'", cmd);
		Process p = Runtime.getRuntime().exec(cmd);

		Thread stdout = createDumper("output", p.getInputStream());
		stdout.start();
		Thread stderr = createDumper("error", p.getErrorStream());
		stderr.start();
		p.waitFor();

		return new File(tex.getAbsolutePath().replaceAll("\\.tex$", ".pdf"));
	}

	protected static Thread createDumper(final String prefix, final InputStream in) throws Exception {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		Thread t = new Thread() {
			public void run() {
				try {
					String line = reader.readLine();
					while (line != null) {
						log.info("TeX " + prefix + ": {}", line);
						line = reader.readLine();
					}
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		return t;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
