/**
 * 
 */
package streams.compiler.utils;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.runtime.setup.ObjectCreator;
import stream.runtime.setup.ParameterInjection;
import stream.util.MD5;
import stream.util.URLUtilities;
import stream.util.Variables;

/**
 * @author chris
 *
 */
public class InlineObjectCreator implements ObjectCreator {

    static Logger log = LoggerFactory.getLogger(InlineObjectCreator.class);

    Map<String, Class<?>> compiled = new LinkedHashMap<String, Class<?>>();
    MemoryClassLoader classLoader = new MemoryClassLoader(InlineObjectCreator.class.getClassLoader());
    String template;

    public InlineObjectCreator() {
        String defaultTemplate = "/streams/compiler/InlineTemplate.java";

        try {
            URL url = InlineObjectCreator.class.getResource(defaultTemplate);
            template = URLUtilities.readContent(url);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read inline-template from '" + defaultTemplate + "'!");
        }
    }

    /**
     * @see stream.runtime.setup.ObjectCreator#getNamespace()
     */
    @Override
    public String getNamespace() {
        return "inline";
    }

    /**
     * @see stream.runtime.setup.ObjectCreator#create(java.lang.String,
     *      java.util.Map, stream.util.Variables)
     */
    @Override
    public Object create(String clazz, Map<String, String> parameters, Variables local, Element element)
            throws Exception {

        streams.compiler.Compiler c = new streams.compiler.Compiler();

        String src = element.getTextContent();
        log.debug("Need to create Java class from source-snippet:\n{}", src);

        if (src == null) {
            src = "";
        }

        final String id = MD5.md5(src);
        final String className = "streams.compiler.InlineCode" + id;

        Class<?> theClass;

        if (compiled.containsKey(className)) {
            log.debug("Already compiled identical inline-code to class '{}'", className);
            theClass = compiled.get(className);
        } else {

            Variables vars = new Variables();
            vars.put("variable", "data");
            vars.put("inline.id", id);
            vars.put("inline.code", src);

            log.debug("Variables: {}", new HashMap<String, Object>(vars));

            String code = vars.expand(template);
            log.debug("Final code after inline-expansion is:\n{}", code);

            theClass = c.compile(className, code, classLoader);
            compiled.put(className, theClass);
        }

        Object obj = theClass.newInstance();

        ParameterInjection.inject(obj, parameters, local);

        return obj;
    }

    /**
     * @see stream.runtime.setup.ObjectCreator#handles(org.w3c.dom.Element)
     */
    @Override
    public boolean handles(Element element) {
        return "inline".equalsIgnoreCase(element.getNodeName());
    }
}