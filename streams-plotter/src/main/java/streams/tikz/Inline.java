/**
 * 
 */
package streams.tikz;

/**
 * @author chris
 * 
 */
public class Inline implements Element {

    final String src;

    public Inline(String tikzCode) {
        src = tikzCode;
    }

    public String toString() {
        return src;
    }
}
