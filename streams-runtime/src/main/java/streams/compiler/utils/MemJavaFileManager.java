/**
 * 
 */
package streams.compiler.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chris
 *
 */
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    final static Logger log = LoggerFactory.getLogger(MemJavaFileManager.class);

    private final MemoryClassLoader classLoader;
    Map<String, JavaSource> sources = new HashMap<String, JavaSource>();

    public MemJavaFileManager(JavaCompiler compiler, MemoryClassLoader classLoader) {
        super(compiler.getStandardFileManager(null, null, null));
        this.classLoader = classLoader;
    }

    public void addInlineSource(String className, JavaSource source) {
        log.info("Adding line-source '{}' => {}", className, source);
        sources.put(className, source);
        classLoader.registerInlineCode(className, source);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) {

        log.info("getJavaFileForOutput ( location={}, className={}, fileObject={} )", location, className, sibling);
        JavaSource source = sources.get(className);
        if (source == null) {
            return source;
        } else {
            log.info("Found in-memory JavaSource for '{}'", className);
        }

        return source;
    }
}