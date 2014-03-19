/**
 * 
 */
package stream.doc;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author chris
 * 
 */
public abstract class AbstractDocConverter implements DocConverter {

	int level = 0;
	final Map<String, String> properties = new LinkedHashMap<String, String>();

	public AbstractDocConverter() {
	}

	/**
	 * @see stream.util.doc.helper.ParameterTableWriter#writeParameterTable(java.lang.
	 *      Class, java.io.PrintStream)
	 */
	@Override
	public void writeParameterTable(Class<?> clazz, PrintStream out) {
		// TODO Auto-generated method stub

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
}