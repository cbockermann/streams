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
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Parameter;
import stream.io.Sink;
import stream.runtime.setup.ParameterDiscovery;

/**
 * @author chris
 * 
 */
public class MarkdownToTexConverter extends AbstractDocConverter {

	static Logger log = LoggerFactory.getLogger(MarkdownToTexConverter.class);
	final File pandoc = new File("/usr/local/bin/pandoc");
	int level = 0;

	/**
	 * @see stream.util.doc.DocConverter#convert(java.io.InputStream,
	 *      java.io.OutputStream)
	 */
	@Override
	public void convert(InputStream in, OutputStream out) {

		if (!pandoc.canExecute()) {
			log.debug("Cannot find pandoc command!");
		}

		try {
			File tmp = File.createTempFile("markdown_input", ".md");

			FileOutputStream fos = new FileOutputStream(tmp);
			DocGenerator.copy(in, fos);
			fos.close();

			File tmp2 = File.createTempFile("pandoc_output", ".tex");
			String exec = pandoc.getAbsolutePath()
					+ " -f markdown -t latex --base-header-level=" + level
					+ " -o " + tmp2.getAbsolutePath() + " "
					+ tmp.getAbsolutePath();
			log.debug("Executing: {}", exec);
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
	 * @see stream.util.doc.DocConverter#createTableOfContents(java.util.Collection,
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
			p.println("\\input{" + elem.prefix + path.replace('/', '_') + "_"
					+ elem.name.replace(".md", "") + "}");
		}
		p.flush();
	}

	/**
	 * @see stream.util.doc.helper.ParameterTableWriter#writeParameterTable(java.lang
	 *      .Class, java.io.PrintStream)
	 */
	@Override
	public void writeParameterTable(Class<?> clazz, PrintStream out) {
		out.println();
		Map<String, Class<?>> tmp = ParameterDiscovery
				.discoverParameters(clazz);

		for (Method m : clazz.getMethods()) {
			if (m.getName().startsWith("set")) {

				@SuppressWarnings("rawtypes")
				Class[] pt = m.getParameterTypes();
				if (pt.length == 1) {
					Class<?> ct = pt[0];
					if (pt[0].isArray()) {
						ct = pt[0].getComponentType();
					}

					if (Sink.class.isAssignableFrom(ct)) {
						String name = m.getName().substring(3);
						name = Character.toLowerCase(name.charAt(0))
								+ name.substring(1);

						log.info("Found additional Sink setter '{}' => {}",
								m.getName(), name);
						tmp.put(name, m.getParameterTypes()[0]);
					}
				}

			}
		}

		if (tmp.isEmpty())
			return;

		out.println("\\begin{table}[h]");
		out.println("\\begin{center}{\\footnotesize");
		out.println("{\\renewcommand{\\arraystretch}{1.4}");
		out.println("\\textsf{");
		out.println("\\begin{tabular}{|c|c|p{\\parameterDescriptionWidth}|c|} \\hline");
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
		out.println("\\caption{\\label{api:" + clazz.getCanonicalName()
				+ "} Parameters of class {\\ttfamily "
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

		int i = s.indexOf("_");
		while (i >= 0) {
			s = s.replace("_", "\\_");
			i = s.indexOf("_", i + 2);
		}

		return s;
	}

	/**
	 * @see stream.util.doc.DocConverter#sectionDown()
	 */
	@Override
	public void sectionDown() {
		this.level++;
	}

	/**
	 * @see stream.util.doc.DocConverter#sectionUp()
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
