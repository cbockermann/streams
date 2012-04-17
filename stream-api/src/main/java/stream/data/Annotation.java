/**
 * 
 */
package stream.data;

/**
 * <p>
 * This is a list of annotations, each of which describing the role of a key
 * element.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public enum Annotation {

	Prediction("prediction"), Error("error"), Label("label");

	String name;

	private Annotation(String name) {
		this.name = name;
	}

	public String toString() {
		return Data.ANNOTATION_PREFIX + name;
	}
}
