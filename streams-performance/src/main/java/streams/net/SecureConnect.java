/**
 * 
 */
package streams.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * This class provides a convenient wrapper to create SSL connections based on
 * the <code>sfb876.jks</code> keystore.
 * 
 * @author Christian Bockermann
 *
 */
public class SecureConnect {

	static Logger log = LoggerFactory.getLogger(SecureConnect.class);

	public final static boolean debug = "true".equals(System.getProperty("streams.net.SecureConnect.debug"));

	public final static String DEFAULT_PROTOCOL = "TLSv1.2";

	private static KeyManager[] km;
	private static TrustManager[] tm;

	private static void init() throws Exception {

		if (km != null && tm != null) {
			// already initialized
			if (debug) {
				log.debug("SecureConnect already initialized. {} key managers " +
						"and {} trust managers available.", km.length, tm.length);
			}
			return;
		}

		URL ksUrl = SecureConnect.class.getResource("/sfb876.jks");

		if (debug) {
			log.debug("Using keystore from {}", ksUrl);
		}

		KeyStore keyStore = KeyStore.getInstance("jks");
		keyStore.load(ksUrl.openStream(), "changeit".toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, "changeit".toCharArray());
		km = kmf.getKeyManagers();

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(keyStore);
		tm = tmf.getTrustManagers();

		//
		// ArrayList<String> ciphers = new ArrayList<String>();
		// for (int i = 0; i < delegate.getSupportedCipherSuites().length; i++)
		// {
		// String cipher = delegate.getSupportedCipherSuites()[i];
		// if (cipher.toLowerCase().indexOf("krb") < 0) {
		// ciphers.add(cipher);
		// }
		// }

	}

	public static void main(String args[]) throws Exception {
		init();

		SSLContext ctx = SSLContext.getInstance(DEFAULT_PROTOCOL);
		ctx.init(km, tm, new SecureRandom());
	}

	public static SSLServerSocket openServer(int port) throws Exception {
		init();

		log.debug("Creating SSL context...");
		SSLContext ctx = SSLContext.getInstance(DEFAULT_PROTOCOL);

		log.debug("Initializing SSL context...");
		ctx.init(km, tm, new SecureRandom());

		log.debug("Creating SSLServerSocket...");

		ProtocolServerFactory ssf = new ProtocolServerFactory(ctx.getServerSocketFactory());
		SSLServerSocket socket = (SSLServerSocket) ssf.createServerSocket(port);
		return socket;
	}

	/**
	 * Try to build first a SSL connection and if it is not possible, use plain TCP connection.
	 *
	 * @param host address for connection
	 * @param port port used for conneciton on host
	 * @return Socket connection
	 */
	public static Socket connect(String host, int port) throws Exception {

		try {
			return connectSSL(host, port);
		} catch (Exception e) {
			log.error("SSL connection couldn't have been established: {}", e.toString());
		}

		log.debug("Establishing plain TCP connection...");
		return new Socket(host, port);
	}

	/**
	 * Try to build SSL connection a given host with specified port.
	 *
	 * @param host address for connection
	 * @param port port used for connection on host
	 * @return SSLSocket
	 */
	public static SSLSocket connectSSL(String host, int port) throws Exception {
		log.debug("Establishing SSL connection...");
		init();

		if (debug) {
			log.debug("Creating SSL context...");
		}
		SSLContext ctx = SSLContext.getInstance(DEFAULT_PROTOCOL);

		if (debug) {
			log.debug("Initializing SSL context...");
		}
		ctx.init(km, tm, new SecureRandom());

		if (debug) {
			log.debug("Connecting socket...");
		}

		SocketFactory sf = new ProtocolFactory(ctx.getSocketFactory());
		SSLSocket socket = (SSLSocket) sf.createSocket(host, port);
		socket.setKeepAlive(true);
		return socket;
	}
}
