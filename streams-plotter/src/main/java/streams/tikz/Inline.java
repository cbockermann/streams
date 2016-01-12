/**
 * 
 */
package streams.tikz;

/**
 * @author chris
 * 
 */
public class Inline {

	final String src;

	public Inline(String tikzCode) {
		src = tikzCode;
	}

	public String toString() {
		return src;
	}
}
