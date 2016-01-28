/**
 * 
 */
package streams.compiler.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Christian Bockermann
 * 
 */
public class MemoryClassLoader extends ClassLoader {

    final static Logger log = LoggerFactory.getLogger(MemoryClassLoader.class);

    final Map<String, JavaSource> byteCode = new HashMap<String, JavaSource>();
    final ClassLoader runtime;

    public MemoryClassLoader(ClassLoader parent) {
        super(parent);
        runtime = parent;
    }

    public void registerInlineCode(String className, JavaSource sourceObject) {
        if (this.byteCode.containsKey(className)) {
            log.warn("Overriding existing byte-code for class '{}'!", className);
        }

        log.debug("Registering inline-code provider for class '{}'", className);
        byteCode.put(className, sourceObject);
    }

    /**
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        if (byteCode.containsKey(name)) {
            log.debug("Found custom in-memory byte-code for class '{}'", name);
            JavaSource code = byteCode.get(name);
            byte[] bytes = code.getByteCode();
            if (bytes.length == 0) {
                throw new ClassNotFoundException(
                        "Class '" + name + "' was registered for inline-compilation, but has not been compiled, yet!");
            }

            return defineClass(name, bytes, 0, bytes.length);
        }

        return super.findClass(name);
    }
}
