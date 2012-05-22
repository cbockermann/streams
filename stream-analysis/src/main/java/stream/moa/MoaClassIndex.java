/**
 * 
 */
package stream.moa;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.ClassFinder;

/**
 * @author chris
 * 
 */
public class MoaClassIndex {

	static Logger log = LoggerFactory.getLogger(MoaClassIndex.class);
	final Set<Class<?>> classes = new LinkedHashSet<Class<?>>();

	public MoaClassIndex() {
		try {
			Class<?>[] cls = ClassFinder.getClasses("moa");
			log.debug("Indexing {} classes.", cls.length);
			for (Class<?> cl : cls) {
				classes.add(cl);
			}
		} catch (Exception e) {
			log.error("Failed to build class-list: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}

	public Set<Class<?>> getAllClasses() {
		return Collections.unmodifiableSet(classes);
	}

	public Set<Class<?>> getClassesOfType(Class<?> required) {
		Set<Class<?>> found = new LinkedHashSet<Class<?>>();

		log.debug("Looking for class that implements {}", required);

		for (Class<?> cl : classes) {

			if (required.isInterface() && isImplementation(cl, required)) {
				found.add(cl);
				continue;
			}

			if (required.isAssignableFrom(cl)) {
				found.add(cl);
			}
		}

		log.debug("Checked {} classes, found {} matches.", classes.size(),
				found.size());
		return found;
	}

	public boolean isImplementation(Class<?> impl, Class<?> interf) {

		for (Class<?> intf : impl.getInterfaces()) {
			if (intf.equals(interf))
				return true;
		}

		return false;
	}
}