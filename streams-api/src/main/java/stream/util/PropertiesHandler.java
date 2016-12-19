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
package stream.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.io.SourceURL;

public class PropertiesHandler {

    private Logger log = LoggerFactory.getLogger(PropertiesHandler.class);

    /**
     * This method adds properties defined in the format as provided by maven's
     * <code>&lt;properties&gt;...&lt;/properties&gt;</code> element.
     * 
     * @param pop
     *            The XML element that is to be checked for properties
     * @param variables
     *            The variables that have been gathered so far
     * @param systemProperties
     *            Variables provided through system properties
     */
    public void handlePropertiesElement(Element prop, Variables variables, Variables systemProperties) {

        log.debug("Handling properties element '{}'", prop.getNodeName());
        String suffix = "";
        if (prop.hasAttribute("suffix")) {
            suffix = prop.getAttribute("suffix");
            log.debug("  adding suffix {} to properties.", suffix);
        }

        NodeList children = prop.getChildNodes();
        if (children.getLength() > 0) {
            // TextNodes
            for (int k = 0; k < children.getLength(); k++) {

                Node ch = children.item(k);
                if (ch.getNodeType() == Node.ELEMENT_NODE) {

                    String key = ch.getNodeName();
                    String value = ch.getTextContent();
                    log.debug("found node '{}' => '{}'", key, value);

                    variables.set(key + suffix, value);
                }
            }

        }
        // Properties from URL
        else if (prop.hasAttribute("url")) {
            String purl = prop.getAttribute("url");
            log.info("Reading properties from URL {}", purl);
            try {
                // ORDER IMPORTANT
                Variables props = new Variables(variables);
                props.addVariables(systemProperties);
                purl = props.expand(purl);
                log.debug("Properties URL is: {}", purl);

                SourceURL propUrl = new SourceURL(purl);

                Map<String, String> p = this.readProperties(propUrl.openStream());
                // Properties p = new Properties();
                // p.load(propUrl.openStream());
                for (String k : p.keySet()) {
                    String key = k.toString().trim();
                    String value = p.get(key);
                    if (value == null) {
                        value = "";
                    }
                    if (value != null) {
                        value = value.trim();
                    }

                    log.info("Reading key '{}' = '{}'", key, value);
                    try {
                        value = variables.expand(value, false);
                    } catch (Exception e) {
                        log.error("Failed to expand variable '{}' with value '{}'", key, value);
                        throw e;
                    }
                    log.debug("Adding property '{}' = '{}'", key, value);
                    variables.set(key + suffix, value);
                }

            } catch (Exception e) {
                log.error("Failed to read properties from url {}: {}", purl, e.getMessage());
                e.printStackTrace();
            }
            // Properties from URL
        } else if (prop.hasAttribute("file")) {
            File file = new File(prop.getAttribute("file"));
            try {
                Properties p = new Properties();
                p.load(new FileInputStream(file));
                for (Object k : p.keySet()) {
                    variables.set(k.toString() + suffix, p.getProperty(k.toString()));
                }
            } catch (Exception e) {
                log.error("Failed to read properties from file {}: {}", file, e.getMessage());
            }
        }

    }

    public void handlePropertyElement(Element prop, Variables variables, Variables systemVariables) {
        if (prop.getNodeName().equalsIgnoreCase("property")) {
            log.debug("Found property element '{}'", prop.getNodeName());
            String key = prop.getAttribute("name");
            String value = prop.getAttribute("value");
            log.debug("   '{}' => '{}'", key, value);

            if (key != null && !"".equals(key.trim()) && value != null && !"".equals(value.trim())) {

                log.debug("handling element {} with key = '{}', value = '{}'", prop.getNodeName(), key, value);

                // ORDER IMPORTANT
                Variables props = new Variables(variables);
                props.addVariables(systemVariables);
                String k = key.trim();
                String v = value.trim();
                // ORDER IMPORTANT
                // // All found variables ()
                v = props.expand(v);
                log.debug("Setting property '{}' = '{}'", k, v);
                variables.set(k, v);
            }
        }
    }

    /**
     * This method adds all the system properties to the container properties,
     * possibly overwriting pre-defined properties.
     * 
     * @param variables
     *            The collection of variables to add the system properties to.
     */
    public void addSystemProperties(Variables variables) {
        for (Object key : System.getProperties().keySet()) {
            String k = key.toString();
            String v = System.getProperty(k);
            log.debug("Adding system property '{}' => '{}'", k, v);
            variables.set(key.toString(), System.getProperty(key.toString()));
        }
    }

    public Map<String, String> readProperties(InputStream is) throws IOException {
        Map<String, String> props = new LinkedHashMap<String, String>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();
        while (line != null) {

            if (!line.trim().isEmpty() && !line.startsWith("#")) {

                int idx = line.indexOf("=");
                if (idx > 0 && idx + 1 < line.length()) {

                    log.debug(" Index of '=' is {}", idx);
                    log.debug("key ~> '{}'", line.substring(0, idx));
                    log.debug("value ~> '{}'", line.substring(idx + 1));

                    String key = line.substring(0, idx);
                    String value = line.substring(idx + 1);
                    props.put(key.trim(), value.trim());
                }
            }

            line = reader.readLine();
        }

        reader.close();

        return props;
    }
}