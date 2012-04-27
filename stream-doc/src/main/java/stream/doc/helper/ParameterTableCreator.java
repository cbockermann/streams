/**
 * 
 */
package stream.doc.helper;

import java.io.PrintStream;

/**
 * @author chris
 *
 */
public interface ParameterTableCreator {

	public abstract void writeParameterTable(Class<?> clazz, PrintStream out);

}