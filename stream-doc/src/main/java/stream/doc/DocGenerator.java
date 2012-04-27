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

import stream.Processor;
import stream.io.DataStream;

/**
 * @author chris
 * 
 */
public class DocGenerator {

	final static Class<?>[] CLASSES = new Class<?>[] { Processor.class,
			DataStream.class };

	final static DocConverter converter = new MarkdownToTexConverter();

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
			DocTree tree = DocTree.findDocs(CLASSES);
			tree.print("  ");
			tree.generateDocs(new File("/tmp"));
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
