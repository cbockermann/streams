/**
 * 
 */
package stream.doc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.io.Stream;
import stream.util.URLUtilities;

/**
 * @author chris
 * 
 */
public class DocGenerator {

	public final static Class<?>[] CLASSES = new Class<?>[] { Processor.class,
			Stream.class };

	final static DocConverter converter = new MarkdownToTexConverter();

	static Logger log = LoggerFactory.getLogger(DocGenerator.class);
	File outDir;

	public DocGenerator(File outputDirectory) {
		outDir = outputDirectory;
	}

	public void generateDocs(List<String> packages) throws Exception {
		generateDocs(packages.toArray(new String[packages.size()]));
	}

	public void generateDocs(String[] packages) throws Exception {

		if (!outDir.isDirectory())
			outDir.mkdirs();

		if (!outDir.isDirectory())
			throw new Exception("Failed to create output directory '"
					+ outDir.getAbsolutePath() + "'!");

		log.info("Searching for processors,streams,...");
		DocTree tree = DocTree.findDocs(CLASSES, packages);
		tree.print("  ");
		tree.generateDocs(outDir);

		URL packageUrl = DocGenerator.class.getResource("/streams.pkg");
		if (packageUrl != null) {
			File streamsPkg = new File(outDir.getAbsolutePath()
					+ File.separator + "streams.pkg");
			log.info("Copying {} to {}", packageUrl, streamsPkg);
			URLUtilities.copy(packageUrl, streamsPkg);
		}
	}

	public static void write(URL url, PrintStream out) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				url.openStream()));
		String line = reader.readLine();
		while (line != null) {
			out.println(line);
			line = reader.readLine();
		}
		reader.close();
	}

	public static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[8192];
		int read = in.read(buf);
		while (read > 0) {
			out.write(buf, 0, read);
			read = in.read(buf);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DocTree tree = DocTree.findDocs(CLASSES, args);
			tree.print("  ");
			tree.generateDocs(new File("/tmp"));
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
