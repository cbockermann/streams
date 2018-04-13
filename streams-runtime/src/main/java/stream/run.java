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
package stream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.StreamRuntime;
import stream.util.Variables;

/**
 * @author chris
 * 
 */
public class run {

    static {
        StreamRuntime.setupLogging();
    }
    public final static Logger log = LoggerFactory.getLogger(stream.run.class);
    private static String version;

    public static void setupOutput() throws Exception {
        if (System.getProperty("container.stdout") != null) {
            System.setOut(new PrintStream(new FileOutputStream(System.getProperty("container.stdout"))));
        }

        if (System.getProperty("container.stderr") != null) {
            System.setOut(new PrintStream(new FileOutputStream(System.getProperty("container.stdout"))));
        }
    }

    public static List<String> handleArguments(String[] args) {

        if (args.length == 0) {
            System.out.println("streams, Version " + getVersion());
            System.out.println();
            System.out.println("No container file specified.");
            System.out.println();
            System.out.println("Usage: ");
            System.out.println("\tstream.run /path/container-file.xml");
            System.out.println();
            return null;
        }

        for (String arg : args) {
            if (arg.equals("-v") || "--version".equals(args)) {
                System.out.println("streams, Version " + getVersion());
                return null;
            }
        }

        List<String> list = new ArrayList<String>();
        for (String arg : args) {
            if (arg.startsWith("-D") || arg.startsWith("--")) {
                int idx = arg.indexOf("=");
                String key = null;
                String value = "";
                if (idx > 2) {
                    key = arg.substring(2, idx);
                    value = arg.substring(idx + 1);
                } else {
                    key = arg.substring(2);
                }

                log.debug("Setting property '{}' = '{}'", key, value);
                System.setProperty(key, value);
            } else {
                log.debug("Adding argument '{}'", arg);
                list.add(arg);
            }
        }

        return list;
    }

    public static void mainWithMap(URL url, Map<String, String> args) throws Exception {
        StreamRuntime.setupLogging();

        Variables props = StreamRuntime.loadUserProperties();
        props.addVariables(args);

        log.debug("Creating process-container from configuration at {}", url);
        ProcessContainer container = new ProcessContainer(url, null, props);

        log.info("Starting process-container...");
        container.run();
        log.info("Container finished.");

    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        try {
            List<String> params = handleArguments(args);
            if (params == null || params.isEmpty()) {
                System.err.println( "Missing arguments! Expecting at least an XML file!");
                System.exit(1);
                return;
            }

            setupOutput();

            URL url;
            try {
                url = new URL(params.get(0));
            } catch (Exception e) {
                File f = new File(params.get(0));
                url = f.toURI().toURL();
            }
            main(url);
        } catch (Exception e) {
            System.err.println( "streams exited on error: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(URL url) throws Exception {

        setupOutput();

        StreamRuntime.setupLogging();

        final Variables vars = StreamRuntime.loadUserProperties();

        log.debug("Creating process-container from configuration at {}", url);
        ProcessContainer container = new ProcessContainer(url, null, vars);

        log.info("Starting process-container...");

        container.run();
        log.info("Container finished.");
    }

    public static void main(URL url, Map<String, ElementHandler> elementHandler) throws Exception {
        StreamRuntime.setupLogging();

        Variables vars = StreamRuntime.loadUserProperties();

        log.debug("Creating process-container from configuration at {}", url);
        ProcessContainer container = new ProcessContainer(url, elementHandler, vars);

        log.info("Starting process-container...");
        container.run();
        log.info("Container finished.");
    }

    public static void main(String resource) throws Exception {
        log.info("Looking for configuration at resource {} in classpath", resource);
        main(run.class.getResource(resource));
    }

    public static void main(String resource, Map<String, ElementHandler> elementHandler) throws Exception {
        log.info("Looking for configuration at resource {} in classpath", resource);
        main(run.class.getResource(resource), elementHandler);
    }

    public synchronized static String getVersion() {
        if (version != null) {
            return version;
        }

        // try to load from maven properties first
        try {
            Properties p = new Properties();
            InputStream is = run.class.getResourceAsStream("/META-INF/maven/de.sfb876/streams-runtime/pom.properties");
            if (is != null) {
                p.load(is);
                version = p.getProperty("version");
            }
        } catch (Exception e) {
            // ignore
        }

        // fallback to using Java API
        if (version == null) {

            log.warn("Could not obtain streams version from Maven properties");

            Package aPackage = run.class.getPackage();
            if (aPackage != null) {
                version = aPackage.getImplementationVersion();
                if (version == null) {
                    version = aPackage.getSpecificationVersion();
                }
            }

        }

        if (version == null) {

            // we could not compute the version so use a blank
            version = "";
            log.warn("Could neither obtain streams version from Java API");

        }

        return version;
    }
}