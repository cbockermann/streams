/**
 * 
 */
package stream.doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stream.annotations.Parameter;
import stream.runtime.setup.ParameterDiscovery;

/**
 * @author chris
 * 
 */
public class MarkdownToTexConverter implements DocConverter {

	int level = 0;

	/**
	 * @see stream.doc.DocConverter#convert(java.io.InputStream,
	 *      java.io.OutputStream)
	 */
	@Override
	public void convert(InputStream in, OutputStream out) {

		try {
			File tmp = File.createTempFile("markdown_input", ".md");

			FileOutputStream fos = new FileOutputStream(tmp);
			DocGenerator.copy(in, fos);
			fos.close();

			File tmp2 = File.createTempFile("pandoc_output", ".tex");
			String exec = "/usr/local/bin/pandoc -f markdown -t latex --base-header-level="
					+ level
					+ " -o "
					+ tmp2.getAbsolutePath()
					+ " "
					+ tmp.getAbsolutePath();
			// System.out.println("Executing: " + exec);
			Process pandoc = Runtime.getRuntime().exec(exec);

			pandoc.waitFor();
			// System.out.println("External pandoc command returned: " + ret);

			DocGenerator.copy(new FileInputStream(tmp2), out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see stream.doc.DocConverter#createTableOfContents(java.util.Collection,
	 *      java.io.OutputStream)
	 */
	@Override
	public void createTableOfContents(Collection<DocTree> elements,
			OutputStream out) {

		PrintStream p = new PrintStream(out);
		for (DocTree elem : elements) {

			if (!elem.isLeaf())
				continue;

			String path = elem.getPath();
			if (!path.isEmpty()) {
				path = path.substring(1);
			}
			p.println("\\input{" + path.replace('/', '_') + "_"
					+ elem.name.replace(".md", "") + "}");
		}
		p.flush();
	}

	/**
	 * @see stream.doc.helper.ParameterTableWriter#writeParameterTable(java.lang
	 *      .Class, java.io.PrintStream)
	 */
	@Override
	public void writeParameterTable(Class<?> clazz, PrintStream out) {
		out.println();
		Map<String, Class<?>> tmp = ParameterDiscovery
				.discoverParameters(clazz);

		if (tmp.isEmpty())
			return;

		out.println("\\begin{table}[h]");
		out.println("\\begin{center}{\\footnotesize");
		out.println("{\\renewcommand{\\arraystretch}{1.4}");
		out.println("\\textsf{");
		out.println("\\begin{tabular}{|c|c|p{9cm}|c|} \\hline");
		out.println("\\textbf{Parameter} & \\textbf{Type} & \\textbf{Description} & \\textbf{Required} \\\\ \\hline  ");

		for (String key : tmp.keySet()) {
			Parameter p = ParameterDiscovery.getParameterAnnotation(clazz, key);
			Class<?> type = tmp.get(key);
			String typeName = type.getSimpleName();
			if (type.isArray()) {
				typeName = type.getComponentType().getSimpleName() + "[]";
			}

			if (p != null) {
				String name = key;
				if (p.name() != null && !p.name().trim().isEmpty())
					name = p.name();
				out.print("{\\ttfamily " + name + " }");
				out.print(" & " + typeName);
				out.print(" & " + toTex(p.description().replaceAll("%", "\\%")));
				out.print(" & " + p.required());
			} else {
				out.print("{\\ttfamily " + key + " }");
				out.print(" & " + typeName);
				out.print(" & ");
				out.print(" & ? ");
			}
			out.println("\\\\ \\hline");
		}
		out.println("\\end{tabular}");
		out.println(" } ");
		out.println(" } ");
		out.println(" } ");
		out.println("\\caption{Parameters of class {\\ttfamily "
				+ clazz.getCanonicalName() + "}.}");
		out.println("\\end{center}");
		out.println("\\end{table}");
	}

	public static String toTex(String str) {
		String s = str;

		Pattern p = Pattern.compile("`(.*)`");
		Matcher m = p.matcher(s);
		if (m.find()) {

			String val = m.group();
			val = val.substring(1, val.length() - 1);

			s = s.substring(0, m.start()) + "{\\ttfamily " + val + "}"
					+ s.substring(m.end());
		}

		return s;
	}

	/**
	 * @see stream.doc.DocConverter#sectionDown()
	 */
	@Override
	public void sectionDown() {
		this.level++;
	}

	/**
	 * @see stream.doc.DocConverter#sectionUp()
	 */
	@Override
	public void sectionUp() {
		this.level--;
	}

	public static void main(String[] args) {
		String in = "This is `@timestamp` a test";
		System.out.println("Original: " + in);
		String tex = toTex(in);
		System.out.println("Converted: " + tex);
	}
}
