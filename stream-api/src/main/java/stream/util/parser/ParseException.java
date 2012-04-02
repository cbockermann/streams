/**
 * 
 */
package stream.util.parser;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class ParseException extends Exception {

	/** The unique class ID */
	private static final long serialVersionUID = 4718808761507297658L;

	public ParseException() {
		super();
	}

	public ParseException(String msg) {
		super(msg);
	}
}
