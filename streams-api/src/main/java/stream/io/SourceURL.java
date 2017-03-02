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
package stream.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.urls.Connection;
import stream.urls.FilesConnection;
import stream.urls.SSLConnection;
import stream.urls.TcpConnection;
import stream.urls.TcpListener;
import stream.util.parser.Parser;
import stream.util.parser.ParserGenerator;

/**
 * <p>
 * This URL encapsulates the definition of URLs for resources. It introduces a
 * thin layer of abstraction for providing support for more than the existing
 * protocol types in Java. The reason for introducing this SourceURL class is
 * that we do not want to register a custom protocol handler, which might
 * destroy some existing applications that also require a custom protocol
 * handler. Java only allows to register a single custom protocol handler.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;, Hendrik Blom
 * 
 */
public class SourceURL implements Serializable {

    public final static String PROTOCOL_FILE = "file";
    public final static String PROTOCOL_TCP = "tcp";
    public final static String PROTOCOL_SSL = "ssl";
    public final static String PROTOCOL_FIFO = "fifo";
    public final static String PROTOCOL_CLASSPATH = "classpath";
    public final static String PROTOCOL_JDBC = "jdbc";
    public final static String PROTOCOL_HTTP = "http";
    public final static String PROTOCOL_HTTPS = "https";
    public final static String PROTOCOL_STDIN = "stdin";

    protected final static String FILE_GRAMMAR = "%(protocol):%(path)";
    protected final static String JDBC_GRAMMAR = "jdbc:%(driver):%(target)";
    protected final static String GRAMMAR = "%(protocol)://%(address)/%(path)";

    /** The unique class ID */
    private static final long serialVersionUID = -7992522266824113404L;

    static Logger log = LoggerFactory.getLogger(SourceURL.class);

    final static Map<String, Class<? extends Connection>> urlProvider = new LinkedHashMap<String, Class<? extends Connection>>();

    static {
        urlProvider.put(PROTOCOL_SSL, stream.urls.SSLConnection.class);
        urlProvider.put(PROTOCOL_TCP, stream.urls.TcpConnection.class);
        urlProvider.put(PROTOCOL_FIFO, stream.urls.FIFOConnection.class);
        registerUrlHandler("files", FilesConnection.class);
        registerUrlHandler("tcpd", TcpListener.class);

        try {
            @SuppressWarnings("unchecked")
            Class<? extends Connection> c = (Class<? extends Connection>) Class.forName("stream.urls.HdfsConnection");
            if (c != null) {
                registerUrlHandler("hdfs", c);
            }
        } catch (Exception e) {
            // log.error("Failed to register handler for protocol '{}'",
            // "hdfs");
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    public final static void registerUrlHandler(String protocol, Class<? extends Connection> clazz) {
        if (urlProvider.containsKey(protocol)) {
            log.warn("Overriding URL handler {} for protocol '{}'", urlProvider.get(protocol), protocol);
        }
        urlProvider.put(protocol, clazz);
    }

    final URL url;
    final String urlString;

    final String protocol;
    final String host;
    final int port;
    final String path;
    final String username;
    final String password;
    final String queryString;

    final Map<String, String> parameters = new LinkedHashMap<String, String>();

    protected SourceURL() {
        this.url = null;
        this.urlString = "";

        protocol = "unknown";
        host = "";
        port = 0;
        path = "";
        queryString = "";
        username = null;
        password = null;
    }

    public SourceURL(URL url) {
        this.url = url;
        this.urlString = url.toString();

        protocol = url.getProtocol();
        host = url.getHost();
        port = url.getPort();
        path = url.getPath();
        queryString = url.getQuery();
        username = null;
        password = null;
    }

    public SourceURL(String urlString) throws Exception {
        this.url = null;
        this.urlString = urlString;

        if (!hasProtocol(urlString))
            throw new MalformedURLException("Missing Protocol in: " + urlString);

        if (urlString.toLowerCase().startsWith(PROTOCOL_FILE) || urlString.toLowerCase().startsWith(PROTOCOL_CLASSPATH)
                || urlString.toLowerCase().startsWith(PROTOCOL_FIFO)) {
            ParserGenerator gen = new ParserGenerator(FILE_GRAMMAR);
            Parser<Map<String, String>> parser = gen.newParser();
            Map<String, String> vals = parser.parse(urlString);
            protocol = vals.get("protocol");
            host = null;
            username = null;
            password = null;
            port = -1;
            String p = vals.get("path");
            if (p.indexOf("?") > 0) {
                path = p.substring(0, p.indexOf("?"));
                queryString = p.substring(p.indexOf("?") + 1);
            } else {
                path = p;
                queryString = "";
            }
        } else {

            //
            // special handling of JDBC URLs needed as they *may* have
            // a '://' for the host specification or not.
            //
            String grammar = GRAMMAR;
            if (urlString.toLowerCase().startsWith(PROTOCOL_JDBC)) {
                grammar = JDBC_GRAMMAR;
            }
            ParserGenerator gen = new ParserGenerator(grammar);
            Parser<Map<String, String>> parser = gen.newParser();
            Map<String, String> vals = parser.parse(urlString);
            if (grammar == JDBC_GRAMMAR) {
                vals.put("protocol", "jdbc");

                // target may be very specific according to the specific
                // JDBC driver used
                //
                String target = vals.get("target");

                if (target.startsWith("//")) {
                    // assume a host:port
                    ParserGenerator pg = new ParserGenerator(GRAMMAR);
                    Parser<Map<String, String>> dbparser = pg.newParser();
                    Map<String, String> values = dbparser.parse("jdbc:" + target);
                    log.debug("sub-parsing jdbc-target returned: {}", values);
                    vals.putAll(values);
                }

                log.debug("'target' of JDBC-URL is: {}", vals.get("target"));
            }
            protocol = vals.get("protocol");

            for (String proto : urlProvider.keySet()) {
                if (protocol.equalsIgnoreCase(proto)) {
                    log.debug("Protocol {} is handled by {}", proto, urlProvider.get(protocol));
                }
            }

            String hostname = vals.get("address");
            if (hostname != null) {
                int at = hostname.indexOf("@");
                if (at >= 0) {
                    String auth = hostname.substring(0, at);
                    String[] tok = auth.split(":", 2);
                    if (tok.length > 1) {
                        username = tok[0];
                        password = tok[1];
                    } else {
                        username = auth;
                        password = "";
                    }

                    hostname = hostname.substring(at + 1);
                } else {
                    username = null;
                    password = null;
                }

                int idx = hostname.indexOf(":");
                int port = 80;
                if (idx > 0) {
                    host = hostname.substring(0, idx);
                    port = Integer.parseInt(hostname.substring(idx + 1));
                } else {
                    host = hostname;
                    if (PROTOCOL_HTTP.equalsIgnoreCase(protocol)) {
                        port = 80;
                    }

                    if (PROTOCOL_HTTPS.equalsIgnoreCase(protocol)) {
                        port = 443;
                    }
                }
                this.port = port;
            } else {
                // username = null;
                // password = null;
                this.username = null;
                this.password = null;
                this.port = -1;
                this.host = "";
            }
            String p = vals.get("path");
            if (p == null)
                p = vals.get("target");

            if (p.indexOf("?") > 0) {
                path = p.substring(0, p.indexOf("?"));
                queryString = p.substring(p.indexOf("?") + 1);
            } else {
                path = p;
                queryString = "";
            }
        }

        if (queryString != null && !queryString.isEmpty()) {
            log.debug("Query string for URL is: {}", queryString);
            for (String pv : queryString.split("&")) {
                if (pv.indexOf("=") > 0) {
                    String[] kv = pv.split("=", 2);
                    parameters.put(kv[0], kv[1]);
                } else {
                    parameters.put(pv, "1");
                }
            }

            log.debug("Parameters are: {}", parameters);
        }
    }

    private boolean hasProtocol(String urlString) {
        String[] p = urlString.split(":");
        if (p.length < 2 || (p.length > 1 && (p[0] == null || p[0].length() == 0)))
            return false;
        return true;
    }

    public boolean isGzip() {
        if (urlString != null && urlString.toLowerCase().endsWith(".gz"))
            return true;

        if (url != null && url.toString().toLowerCase().endsWith(".gz"))
            return true;

        return false;
    }

    public InputStream openStream() throws IOException {

        InputStream inputStream = createStream();

        if (isGzip()) {
            log.debug("Wrapping stream {} in GZIPInputStream for URL {}", inputStream, this);
            return new GZIPInputStream(inputStream, 1048576);
        }

        return inputStream;
    }

    private InputStream createStream() throws IOException {

        log.debug("Opening URL {}", this.urlString);
        if (this.url != null) {
            return url.openStream();
        }

        for (String proto : urlProvider.keySet()) {
            if (proto.equalsIgnoreCase(protocol)) {
                Class<? extends Connection> clazz = urlProvider.get(proto);
                log.debug("Found url-provider '{}' for URL {}", clazz, this);
                try {
                    Constructor<? extends Connection> constructor = clazz.getConstructor(SourceURL.class);

                    log.debug("Using constructor {} to create new instance of provider {}", constructor, clazz);
                    Connection con = constructor.newInstance(this);
                    return con.connect();
                } catch (NoSuchMethodException nsm) {
                    nsm.printStackTrace();
                    log.error("Failed to create instance of class {} for URL {}", clazz, this);
                    throw new IOException(nsm.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException(e.getMessage());
                }
            }
        }

        if (PROTOCOL_STDIN.equalsIgnoreCase(protocol)) {
            return System.in;
        }

        if (PROTOCOL_CLASSPATH.equalsIgnoreCase(protocol)) {
            log.debug("Returning InputStream for classpath resource '{}'", getPath());
            return SourceURL.class.getResourceAsStream(getPath());
        }

        if (PROTOCOL_TCP.equalsIgnoreCase(protocol)) {
            TcpConnection con = new TcpConnection(this);
            return con.getInputStream();
        }

        if (PROTOCOL_SSL.equalsIgnoreCase(protocol)) {
            try {
                SSLConnection ssl = new SSLConnection(this);
                ssl.open();
                return ssl.getInputStream();
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
        }

        String theUrl = this.urlString;
        try {

            if (theUrl.startsWith(PROTOCOL_FILE + ":")) {
                File f = new File(theUrl.substring(PROTOCOL_FILE.length() + 1));
                if (!f.canRead()) {
                    log.error("Cannot open file '{}' for reading!", f);
                    throw new FileNotFoundException("Cannot open file '" + f.getAbsolutePath() + "' for reading!");
                }
            }

            if (theUrl.startsWith(PROTOCOL_FIFO)) {

                log.debug("Handling FIFO URL pattern...");
                theUrl = theUrl.replace("fifo:", "file:");
                File file = new File(theUrl.replace("file:", ""));
                if (!file.exists()) {
                    log.debug("Creating new fifo file '{}' with mkfifo", file);
                    Process p = Runtime.getRuntime().exec("mkfifo " + file.getAbsolutePath());
                    log.debug("Waiting for mkfifo to return...");
                    int ret = p.waitFor();
                    log.debug("mkfifo finished: {}", ret);
                } else {
                    log.debug("Using existing fifo-file '{}'", file);
                }

                if (!file.exists()) {
                    throw new IOException("Failed to create/acquire FIFO file '" + file.getAbsolutePath() + "'!");
                }

                log.debug("Returning FileInputStream for FIFO {}", file);
                FileInputStream fis = new FileInputStream(file);
                return fis;
            }

            log.debug("The URL string is: '{}'", theUrl);
            URL url = new URL(theUrl);
            if (url.getUserInfo() != null) {
                final URLConnection conn = url.openConnection();
                String basicAuth = "Basic "
                        + new String(javax.xml.bind.DatatypeConverter.printBase64Binary(url.getUserInfo().getBytes()));
                conn.setRequestProperty("Authorization", basicAuth);
                return conn.getInputStream();
            }
            return url.openStream();
        } catch (FileNotFoundException fnf) {
            log.error("Did not find referenced file: {}", fnf.getMessage());
            throw fnf;
        } catch (IOException ioe) {
            log.error("Failed to open URL '{}' with default URL.openStream(): {}", theUrl, ioe.getMessage());
            throw ioe;
        } catch (Exception e) {
            log.error("Failed to open '{}' with default Java URL mechanism: {}", theUrl, e.getMessage());
            e.printStackTrace();
            throw new IOException("No handler found for protocol '" + protocol + "': " + e.getMessage());
        }

    }

    public String getFile() {
        return path;
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return urlString;
    }
}