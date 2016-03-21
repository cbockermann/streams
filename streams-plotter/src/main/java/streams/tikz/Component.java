/**
 * 
 */
package streams.tikz;

/**
 * @author chris
 *
 */
public interface Component extends Element {

    public String toTikzString();

    public Rectangle bounds();
}
