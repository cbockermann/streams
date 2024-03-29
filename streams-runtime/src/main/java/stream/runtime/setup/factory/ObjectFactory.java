/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 *
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can
 *  redistribute it and/or modify it under the terms of the
 *  GNU Affero General Public License as published by the Free Software
 *  Foundation, either version 3 of the License, or (at your option) any
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.runtime.setup.factory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import stream.Configurable;
import stream.annotations.BodyContent;
import stream.runtime.RuntimeClassLoader;
import stream.runtime.setup.ObjectCreator;
import stream.runtime.setup.ParameterInjection;
import stream.runtime.setup.UserSettings;
import stream.util.Variables;
import stream.util.XMLUtils;
import stream.utils.FileUtils;

/**
 * This class implements a generic object factory that is able to instantiate
 * objects from XML elements.
 *
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class ObjectFactory extends Variables {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Extract the config {@link org.w3c.dom.Element} as a new
     * {@link org.w3c.dom.Document}.
     *
     * @param node
     * @return
     * @throws ParserConfigurationException
     */
    public static final Element createConfigDocument(Element node) throws ParserConfigurationException {
        Element clone = (Element) node.cloneNode(true);
        return clone;
    }

    static Logger log = LoggerFactory.getLogger(ObjectFactory.class);
    static Map<String, Integer> globalObjectNumbers = new HashMap<String, Integer>();
    final static Map<String, String> classNames = new HashMap<String, String>();

    final static String[] DEFAULT_PACKAGES = new String[] { "", "stream.data.", "stream.data.mapper.",
            "stream.data.tree.", "stream.filter.", "stream.data.filter.", "stream.data.stats.", "stream.data.vector.",
            "stream.data.test.", "stream.logic", "stream.flow", "stream.monitor", "stream.statistics", "stream.script",
            "streams.performance" };

    // TODO: Extend this with a custom class loader that will search other
    // places like ${user.home}/lib or ${user.home}/.streams/lib/ or any
    // other search path list found in the system environment/system settings
    final RuntimeClassLoader classLoader = new RuntimeClassLoader(new URL[0], ObjectFactory.class.getClassLoader());

    final Set<URL> urls = new LinkedHashSet<URL>();
    final List<String> searchPath = new ArrayList<String>();
    final static List<ObjectCreator> objectCreators = new ArrayList<ObjectCreator>();

    protected ObjectFactory() {
        super(new HashMap<String, String>());

        for (String pkg : DEFAULT_PACKAGES) {
            addPackage(pkg);
        }

        UserSettings settings = new UserSettings();
        List<File> files = new ArrayList<File>();

        for (URL url : settings.getLibrarySearchPath()) {
            if (url.getProtocol().toLowerCase().startsWith("file")) {
                files.addAll(FileUtils.findAllFiles(new File(url.getFile())));
            }
        }

        URL[] url = new URL[files.size()];
        log.debug("Extra class paths:");
        int i = 0;
        for (File f : files) {
            if (!f.getName().endsWith(".jar") || f.getName().indexOf("stream-runtime") >= 0)
                continue;
            try {
                url[i] = f.toURI().toURL();
                urls.add(f.toURI().toURL());
                log.debug("   {}", f);
                classLoader.addExtraURLs(f.toURI().toURL());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        log.debug("URLs: {}", (Object[]) classLoader.getURLs());
    }

    public void addClassPathUrls(Collection<URL> newUrls) {
        log.debug("Adding urls {}", newUrls);
        urls.addAll(newUrls);
        log.debug("URLs now are: {}", this.urls);

        Iterator<URL> it = urls.iterator();
        while (it.hasNext()) {
            URL url = it.next();
            if (url.toString().indexOf("stream-runtime") >= 0) {
                log.debug("Removing referenced URL {}", url);
                it.remove();
            } else {
                classLoader.addExtraURLs(url);
            }
        }
        log.debug("URLClassLoader.getURLs(): {}", (Object[]) classLoader.getURLs());
    }

    public void addPackage(String pkg) {
        String name = pkg;
        if (!name.endsWith("."))
            name = name + ".";

        if (!searchPath.contains(name))
            searchPath.add(0, name);
        else
            log.warn("Package {} already in search-path!", pkg);
    }

    public List<String> getSearchPaths() {
        return searchPath;
    }

    public static void register(String name, Class<?> clazz) {
        classNames.put(name.toLowerCase(), clazz.getName());
    }

    public static void registerObjectCreator(ObjectCreator creator) {
        objectCreators.add(creator);
    }

    public static ObjectFactory newInstance() {
        return new ObjectFactory();
    }

    public static synchronized String getNextIdentifier(String obj) {
        Integer cur = globalObjectNumbers.get(obj);
        if (cur == null)
            cur = new Integer(0);

        globalObjectNumbers.put(obj, cur + 1);
        return obj + cur;
    }

    public Object create(Element node) throws Exception {
        Map<String, String> params = getAttributes(node);
        return create(node, params, new Variables(this.variables));
    }

    /**
     * @param node
     * @param variables
     * @return
     * @throws Exception
     */
    public Object create(Element node, Map<String, String> params, Variables local) throws Exception {

        //
        // Map<String, String> params = getAttributes(node);
        log.debug("Creating object '{}' with attributes: {}", node.getNodeName(), params);

        // String name = node.getNodeName();
        // for (ObjectCreator creator : objectCreators) {
        // if (name.startsWith(creator.getNamespace())) {
        // return creator.create(name, params, local);
        // }
        // }

        Object obj = create(this.findClassForElement(node), params, createConfigDocument(node), local);
        return obj;
    }

    public Object create(String className, Map<String, String> parameter, org.w3c.dom.Element config) throws Exception {
        return create(className, parameter, config, new Variables());
    }

    public Object create(String className, Map<String, String> parameter, org.w3c.dom.Element config, Variables local)
            throws Exception {

        log.debug("Creating object for class '{}'", className);
        log.debug("parameters: {}", parameter);
        log.debug("Parameters for new class: {}", parameter);
        log.debug("local variables: {}", local);

        Map<String, String> p = new HashMap<String, String>();
        for (String key : parameter.keySet()) {
            //
            // TODO: move the macro-expansion into the ParameterInjection!
            //
            if (parameter.get(key).indexOf("%{container") >= 0) {
                String orig = parameter.get(key);
                String expanded = local.expand(orig);
                p.put(key, expanded);
                log.debug("Expanded {} to {}", orig, expanded);
            } else {
                String orig = parameter.get(key);
                // TODO Exception werfen
                String expanded = local.expand(orig);
                log.debug("Expanded {} to {}", orig, expanded);
                p.put(key, expanded);
            }
        }

        Object object = null;

        // check if a custom creator is registered for that package
        //
        for (ObjectCreator creator : objectCreators) {
            if (className.startsWith(creator.getNamespace())) {
                log.debug("Found object-creator {} for class {}", creator, className);

                object = creator.create(className, p, local);
                return object;
            }
        }

        // create an instance of this class using the "default way"
        //
        log.debug("Looking for class '{}'", className);
        Class<?> clazz = Class.forName(className, false, classLoader);

        Deprecated depr = clazz.getAnnotation(Deprecated.class);
        if (depr != null) {
            log.warn("Using deprecated class '{}' -> {}", className, depr);
        }
        object = clazz.newInstance();

        // Inject the parameters into the object...
        //
        log.debug("Injecting parameters: {}", p);
        ParameterInjection.inject(object, p, this);

        // If the instance implements Configurable, then we provide
        // it with a deep-cloned copy of the DOM element that was
        // used to create the instance
        //
        if (object instanceof Configurable) {
            log.debug("Applying configuration: {}", config);

            Element el = (Element) config.cloneNode(true);

            Map<String, String> attr = XMLUtils.getAttributes(el);
            attr = local.expandAll(attr);

            for (String a : attr.keySet()) {
                el.setAttribute(a, attr.get(a));
            }

            ((Configurable) object).configure(el);
        }

        return object;
    }

    /**
     * @param node
     * @return
     */
    public Map<String, String> getAttributes(Node node) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        NamedNodeMap att = node.getAttributes();
        for (int i = 0; i < att.getLength(); i++) {
            Node attr = att.item(i);
            String value = attr.getNodeValue();
            map.put(attr.getNodeName(), value);
        }

        //
        // Special case for handling the XML content as __EMBEDDED_CONTENT__
        // parameter (e.g. for the ScriptDataProcessor)
        //
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            String text = element.getTextContent();
            if (text != null && !"".equals(text.trim())) {
                map.put(BodyContent.KEY, expand(text));
            }
        }

        return map;
    }

    public String findClassForElement(Element node) throws Exception {
        if (node.getAttribute("class") != null && !"".equals(node.getAttribute("class")))
            return node.getAttribute("class");

        try {
            classLoader.loadClass(node.getNodeName());
            log.debug("Found direct class-match: {}", node.getNodeName());

            URL doc = findDocumentation(node.getNodeName());
            if (doc == null)
                log.debug("No documentation provided for class '{}'!", node.getNodeName());

            return node.getNodeName();
        } catch (Exception e) {
        }

        for (String prefix : searchPath) {

            String cn = prefix + node.getNodeName();
            try {
                while (cn.startsWith("."))
                    cn = cn.substring(1);

                log.debug("Checking for class {}", cn);
                Class<?> clazz = classLoader.loadClass(cn);
                log.debug("Auto-detected class {} for node {}", clazz, node.getNodeName());

                URL doc = findDocumentation(clazz.getName());
                if (doc == null)
                    log.debug("No documentation provided for class '{}'!", clazz.getName());

                return clazz.getName();
            } catch (Exception e) {
                log.debug("No class '{}' found", cn);
            }
        }

        throw new Exception("Failed to determine class for node '" + node.getNodeName() + "'!");
    }

    private URL findDocumentation(String className) {
        String docResource = "/" + className.replace('.', '/') + ".md";
        log.trace("Doc resource for '{}' is '{}'", className, docResource);
        URL url = classLoader.getResource(docResource);
        return url;
    }
}