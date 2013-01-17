/**
 * 
 */
package stream.doc.helper;

import java.io.PrintStream;

/**
 * @author chris
 *
 */
public interface ParameterTableWriter {

	public abstract void writeParameterTable(Class<?> clazz, PrintStream out);

}