/**
 * 
 */
package streams.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.io.SourceURL;
import stream.runtime.dependencies.DependencyResolver;
import stream.runtime.setup.factory.ObjectFactory;
import stream.runtime.setup.handler.LibrariesElementHandler;
import stream.util.URLUtilities;
import stream.util.Variables;
import stream.util.XMLUtils;
import streams.compiler.utils.JavaSource;
import streams.compiler.utils.MemJavaFileManager;
import streams.compiler.utils.MemoryClassLoader;

/**
 * @author chris
 * 
 */
public class Compiler {

    static Logger log = LoggerFactory.getLogger(Compiler.class);

    final ObjectFactory objectFactory = ObjectFactory.newInstance();
    final DependencyResolver resolver = new DependencyResolver();

    final LibrariesElementHandler libs = new LibrariesElementHandler(objectFactory);

    File output;
    final List<CompilationError> errors = new ArrayList<CompilationError>();

    public Compiler() {
    }

    public void compile(SourceURL xml) throws Exception {

        Document doc = XMLUtils.parseDocument(xml.openStream());
        log.debug("Loading all referenced libraries...");
        libs.handle(null, doc, new Variables(), null);

        errors.clear();

        NodeList processes = doc.getElementsByTagName("process");
        for (int i = 0; i < processes.getLength(); i++) {

            Element process = (Element) processes.item(i);

            NodeList ps = process.getChildNodes();

            for (int j = 0; j < ps.getLength(); j++) {

                if (ps.item(j).getNodeType() == Node.ELEMENT_NODE) {

                    Element processor = (Element) ps.item(j);
                    Map<String, String> params = XMLUtils.getAttributes(processor);

                    String className = processor.getTagName();
                    if (params.containsKey("class") && !params.get("class").isEmpty()) {
                        className = params.get("class");
                    }

                    log.debug("Class name is '{}'", className);

                    if (processor.getAttribute("src") != null) {
                        log.debug("Found in-line code for {}", className);
                        log.debug("Creating processor with inline-enabled object-factory...");

                        SourceURL source = new SourceURL(processor.getAttribute("src"));

                        retrieveAndCompile(className, source, new MemoryClassLoader(objectFactory.getClassLoader()));
                    }
                }
            }
        }
    }

    public Class<?> retrieveAndCompile(String className, SourceURL src, MemoryClassLoader classLoader)
            throws Exception {
        String java = URLUtilities.readResponse(src.openStream());
        return compile(className, java, classLoader);
    }

    public Class<?> compile(String className, String java, MemoryClassLoader classLoader) throws Exception {

        String theClass = className;

        String pkg = findPackage(java);
        pkg = pkg.replace(".", "/");
        log.debug("Package of class is: '{}'", pkg);

        String simpleClassName = className;
        if (className.lastIndexOf(".") > 0) {
            simpleClassName = className.substring(className.lastIndexOf(".") + 1);
        }

        String classLocation = pkg.replace(".", File.separator) + File.separator + simpleClassName + ".java";

        java = "// " + "  @" + System.currentTimeMillis() + "\n" + java;
        final String javaSource = java;
        log.debug("Found source code ref '{}' for {}", "", theClass);
        log.debug("Inline code is:\n{}", javaSource);

        final File srcFile;
        if (output != null) {
            srcFile = new File(output.getCanonicalPath() + File.separator + classLocation);
            if (!srcFile.getParentFile().isDirectory()) {
                srcFile.getParentFile().mkdirs();
            }
            FileWriter w = new FileWriter(srcFile);
            w.write(java);
            w.close();
        } else {
            srcFile = new File("." + classLocation);
        }
        log.debug("Storing source in {}", srcFile.getCanonicalPath());

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        MemJavaFileManager fileManager = new MemJavaFileManager(compiler, classLoader);

        JavaSource javaFile = new JavaSource(theClass, javaSource);
        fileManager.addInlineSource(className, javaFile);

        Collection<JavaSource> units = Collections.singleton(javaFile);

        final String[] lines = javaSource.split("\n");
        final Diagnosis diag = new Diagnosis(srcFile, javaSource, lines);

        log.debug("Starting compilation...");
        CompilationTask compilation = compiler.getTask(null, fileManager, diag, null, null, units);
        compilation.call();
        log.debug("Compiler finished.");

        if (!diag.errors().isEmpty()) {
            log.error("Failed to compile inline code!");
            log.error("Errors:");
            for (CompilationError error : diag.errors) {
                log.error("  Line {}: '{}'   -> {}", error.line, error.snippet, error.message);
                log.error("     line is:\t{}", error.lines[error.line]);
            }

            throw new RuntimeException("Compliation errors in inline-code!");
        }

        if (output != null) {
            File code = new File(srcFile.getCanonicalPath().replace(".java", ".class"));
            log.debug("Storing byte-code in {}", code.getCanonicalPath());
            FileOutputStream fos = new FileOutputStream(code);
            fos.write(javaFile.getByteCode());
            fos.close();
        }

        try {
            Class<?> clazz = Class.forName(theClass, true, classLoader);
            log.debug("Returning freshly compiled class {}", clazz.getName());
            return clazz;
        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
            return null;
        }
    }

    public String findPackage(String src) {
        int idx = src.indexOf("package");
        if (idx >= 0) {
            int end = src.indexOf(";", idx);
            String pkg = src.substring(idx + "package ".length(), end);
            log.debug("Found package string: '{}'", pkg);
            return pkg;
        }
        return "";
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        SourceURL src = new SourceURL("http://localhost:8080/application/test/config.xml");
        if (args.length > 0) {
            src = new SourceURL(args[0]);
        }

        Compiler c = new Compiler();
        c.output = new File(".").getCanonicalFile();
        c.compile(src);

        log.debug("Compilation had {} errors.", c.errors.size());
        for (CompilationError err : c.errors) {
            log.debug("{}", err);
        }
    }

    public static class CompilationError extends LinkedHashMap<String, String> {
        /** The unique class ID */
        private static final long serialVersionUID = 6216565205279026711L;

        final String[] lines;
        final int line;
        final String message;
        final String snippet;

        public CompilationError(String message, int line, String[] lines, String snippet) {
            this.message = message;
            this.lines = lines;
            this.line = line;
            this.snippet = snippet;
        }
    }

    public static class Diagnosis implements DiagnosticListener<Object> {

        File srcFile;
        String javaSource;
        String[] lines;
        List<CompilationError> errors = new ArrayList<CompilationError>();

        public Diagnosis(File src, String javaSource, String[] lines) {
            this.srcFile = src;
            this.javaSource = javaSource;
            this.lines = lines;
        }

        @Override
        public void report(Diagnostic<?> diagnostic) {
            log.error("Found compiler error: " + diagnostic.getMessage(Locale.getDefault()) + " at: "
                    + diagnostic.getLineNumber());

            Long line = diagnostic.getLineNumber() - 12;
            Long start = diagnostic.getStartPosition();
            // Long end = diagnostic.getEndPosition();
            String[] lines = this.lines;
            String snippet = ""
                    + javaSource.substring((int) diagnostic.getStartPosition(), (int) diagnostic.getEndPosition());

            String msg = diagnostic.getMessage(Locale.getDefault());
            CompilationError error = new CompilationError(msg, Math.max(0, (int) diagnostic.getLineNumber() - 1), lines,
                    snippet);
            error.put("type", diagnostic.getKind().name());
            error.put("message", diagnostic.getMessage(Locale.getDefault()));
            error.put("file", srcFile.getPath());
            error.put("line", "" + diagnostic.getLineNumber());
            error.put("source", "" + diagnostic.getSource());
            error.put("snippet", snippet);
            error.put("code", diagnostic.getCode() + "");
            error.put("start", start.intValue() + "");
            error.put("end", start.intValue() + "");

            log.debug("Java Source is: {}", lines[line.intValue() - 1]);

            errors.add(error);
        }

        public List<CompilationError> errors() {
            return errors;
        }
    }
}
