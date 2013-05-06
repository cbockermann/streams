/**
 * 
 */
package stream.urls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.SourceURL;

/**
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class SSLConnection extends TcpConnection {

	static Logger log = LoggerFactory.getLogger(SSLConnection.class);
	KeyStore keyStore;
	KeyStore trustStore;
	private char[] password;
	protected final String host;
	protected final int port;

	protected SSLContext ssl;
	protected SSLSocket socket;

	private InputStream inputStream;
	private OutputStream outputStream;

	public SSLConnection(SourceURL url) throws Exception {
		super(url);
		this.host = url.getHost();
		this.port = url.getPort();

		if (url.getParameters().containsKey("keystorePassword"))
			this.password = url.getParameters().get("keystorePassword")
					.toCharArray();
		else
			this.password = null;

		if (url.getParameters().containsKey("keystoreUrl")) {
			SourceURL ksUrl = new SourceURL(url.getParameters().get(
					"keystoreUrl"));
			loadKeyStore(ksUrl, password);
		} else
			this.keyStore = null;

		if (url.getParameters().containsKey("truststoreUrl")) {
			this.trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			SourceURL tsUrl = new SourceURL(url.getParameters().get(
					"truststoreUrl"));

			String trustpw = url.getParameters().get("truststorePassword");
			if (trustpw == null) {
				trustpw = new String(password);
			}
			loadTrustStore(tsUrl, trustpw.toCharArray());
		} else {
			this.trustStore = this.keyStore;
		}
	}

	public void setKeyStore(KeyStore ks, char[] password) {
		this.keyStore = ks;
		this.password = password;
	}

	public void loadKeyStore(URL url, char[] password) throws Exception {
		keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(url.openStream(), password);
		this.password = password;
	}

	public void loadKeyStore(SourceURL source, char[] pass) throws Exception {
		keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(source.openStream(), pass);
		this.password = pass;
	}

	public void setTrustStore(KeyStore ks, char[] password) {
		this.trustStore = ks;
		this.password = password;
	}

	public void loadTrustStore(URL url, char[] password) throws Exception {
		trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(url.openStream(), password);
	}

	public void loadTrustStore(SourceURL url, char[] password) throws Exception {
		trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(url.openStream(), password);
	}

	public void open() throws Exception {

		SSLSocketFactory factory;

		if (keyStore == null && password == null) {

			log.debug("No KeyStore/password specified, using default keys/trust-store.");
			factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

		} else {

			log.debug("Specific keystore specified, creating custom SSL context...");
			KeyManagerFactory kmf = getKeyManagerFactory();
			log.debug(
					"Initializing key manager factory with keyStore {} and password {}",
					keyStore, password);
			kmf.init(keyStore, password);

			TrustManagerFactory tmf = getTrustManagerFactory();
			log.debug("Initializing trust manager factory with trustStore {}",
					trustStore);
			tmf.init(trustStore);

			ssl = SSLContext.getInstance("TLS");
			log.debug("Created SSL context {}", ssl);
			ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			factory = ssl.getSocketFactory();
		}

		socket = (SSLSocket) factory.createSocket(host, port);

		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
	}

	/**
	 * This method closes the socket of this connection (if the socket is
	 * connected).
	 * 
	 * @throws Exception
	 */
	public void close() throws Exception {

		if (socket != null)
			socket.close();
	}

	/**
	 * Returns the InputStream of the socket (if connected). If the socket is
	 * not connected, this method returns <code>null</code>.
	 * 
	 * @return
	 * @throws Exception
	 */
	public InputStream getInputStream() throws IOException {
		return inputStream;
	}

	/**
	 * Returns the OutputStream of the socket (if connected). If the socket is
	 * not connected, this method returns <code>null</code>.
	 * 
	 * @return
	 * @throws Exception
	 */
	public OutputStream getOutputStream() throws IOException {
		return outputStream;
	}

	/**
	 * This method creates an uninitialized KeyManagerFactory.
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	protected KeyManagerFactory getKeyManagerFactory()
			throws NoSuchAlgorithmException {
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
				.getDefaultAlgorithm());
		return kmf;
	}

	/**
	 * This method creates an uninitialized TrustManagerFactory.
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	protected TrustManagerFactory getTrustManagerFactory()
			throws NoSuchAlgorithmException {
		TrustManagerFactory tmf = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		return tmf;
	}

	/**
	 * @see stream.urls.Connection#connect()
	 */
	@Override
	public InputStream connect() throws IOException {

		if (socket != null && socket.isConnected() && inputStream != null) {
			log.debug("Connection already established...");
			return this.inputStream;
		}

		try {
			this.open();

			return socket.getInputStream();

		} catch (Exception e) {
			log.error("Failed to connect: {}", e.getMessage());
			e.printStackTrace();
			throw new IOException("Failed to connect: " + e.getMessage());
		}
	}

	/**
	 * @see stream.urls.Connection#disconnect()
	 */
	@Override
	public void disconnect() throws IOException {
		try {
			this.close();
		} catch (Exception e) {
			log.error("Failed to disconnect: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
			throw new IOException("Failed to disconnect: " + e.getMessage());
		} finally {
			inputStream = null;
			outputStream = null;
		}
	}

	/**
	 * @see stream.urls.Connection#getSupportedProtocols()
	 */
	@Override
	public String[] getSupportedProtocols() {
		return new String[] { "ssl" };
	}

	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}
}