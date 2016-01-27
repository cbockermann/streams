/**
 * 
 */
package streams.tikz;

/**
 * @author chris
 *
 */
public interface Component {

    public String toTikzString();

    public Rectangle bounds();
}
