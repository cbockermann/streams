/**
 * 
 */
package stream.doc;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import stream.doc.helper.ParameterTableCreator;

/**
 * @author chris
 * 
 */
public interface DocConverter extends ParameterTableCreator {

	public void sectionDown();

	public void sectionUp();

	public void convert(InputStream in, OutputStream out);

	public void createTableOfContents(Collection<DocTree> elements,
			OutputStream out);
}
