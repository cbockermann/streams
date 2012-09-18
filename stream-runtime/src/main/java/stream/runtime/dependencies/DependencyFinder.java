/**
 * 
 */
package stream.runtime.dependencies;

/**
 * @author chris
 * 
 */
public interface DependencyFinder {

	public String find(String groupId, String artifactId, String version)
			throws Exception;
}
