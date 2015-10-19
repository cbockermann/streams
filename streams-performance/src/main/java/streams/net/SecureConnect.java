/**
 * 
 */
package streams.net;

import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author chris
 *
 */
public class SecureConnect {

	// static Logger log = LoggerFactory.getLogger(SecureConnect.class);

	public final static String DEFAULT_PROTOCOL = "TLSv1.2";

	private static KeyManager[] km;
	private static TrustManager[] tm;

	private static void init() throws Exception {

		if (km != null && tm != null) {
			// already initialized
			// log.debug("SecureConnect already initialized. {} key managers and
			// {} trust managers available.", km.length,
			// tm.length);
			return;
		}

		URL ksUrl = SecureConnect.class.getResource("/sfb876.jks");
		// log.debug("Using keystore from {}", ksUrl);

		KeyStore keyStore = KeyStore.getInstance("jks");
		keyStore.load(ksUrl.openStream(), "changeit".toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, "changeit".toCharArray());
		km = kmf.getKeyManagers();

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(keyStore);
		tm = tmf.getTrustManagers();
	}

	public static void main(String args[]) throws Exception {
		init();

		SSLContext ctx = SSLContext.getInstance(DEFAULT_PROTOCOL);
		ctx.init(km, tm, new SecureRandom());

		// String[] enabled =
		// ctx.getServerSocketFactory().getDefaultCipherSuites();
		// String[] supported =
		// ctx.getServerSocketFactory().getSupportedCipherSuites();
		//
		// log.info("{} ciphers enabled, {} ciphers supported.", enabled.length,
		// supported.length);
		//
		// for (int i = 0; i < enabled.length; i++) {
		// log.info("enabled[{}] = {}", i, enabled[i]);
		// }
		//
		// for (int i = 0; i < supported.length; i++) {
		// log.info("supported[{}] = {}", i, supported[i]);
		// }

	}

	public static SSLSocket connect() throws Exception {
		return connect("performance.sfb876.de", 6001);
	}

	public static SSLSocket connect(String host, int port) throws Exception {

		init();

		// log.debug("Creating SSL context...");
		SSLContext ctx = SSLContext.getInstance(DEFAULT_PROTOCOL);

		// log.debug("Initializing SSL context...");
		ctx.init(km, tm, new SecureRandom());

		// log.debug("Connecting socket...");
		SocketFactory sf = new ProtocolFactory(ctx.getSocketFactory());

		return (SSLSocket) sf.createSocket(host, port);
	}
}
