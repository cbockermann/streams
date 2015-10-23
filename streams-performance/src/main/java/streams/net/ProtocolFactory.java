/**
 * 
 */
package streams.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author chris
 *
 */
public class ProtocolFactory extends SSLSocketFactory {

	private final SSLSocketFactory underlyingSSLSocketFactory;
	private final String[] enabledProtocols;
	private final String[] enabledCiphers;

	public ProtocolFactory(final SSLSocketFactory delegate) {
		this(delegate, new String[] { "TLSv1" });
	}

	public ProtocolFactory(final SSLSocketFactory delegate, final String[] enabledProtocols) {
		this.underlyingSSLSocketFactory = delegate;
		this.enabledProtocols = enabledProtocols;

		ArrayList<String> ciphers = new ArrayList<String>();
		for (int i = 0; i < delegate.getSupportedCipherSuites().length; i++) {
			String cipher = delegate.getSupportedCipherSuites()[i];
			if (cipher.toLowerCase().indexOf("krb") < 0) {
				ciphers.add(cipher);
			}
		}

		enabledCiphers = ciphers.toArray(new String[ciphers.size()]);
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return underlyingSSLSocketFactory.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return enabledCiphers;
	}

	@Override
	public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose)
			throws IOException {
		final Socket underlyingSocket = underlyingSSLSocketFactory.createSocket(socket, host, port, autoClose);
		return overrideProtocol(underlyingSocket);
	}

	@Override
	public Socket createSocket(final String host, final int port) throws IOException, UnknownHostException {
		final Socket underlyingSocket = underlyingSSLSocketFactory.createSocket(host, port);
		return overrideProtocol(underlyingSocket);
	}

	@Override
	public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort)
			throws IOException, UnknownHostException {
		final Socket underlyingSocket = underlyingSSLSocketFactory.createSocket(host, port, localAddress, localPort);
		return overrideProtocol(underlyingSocket);
	}

	@Override
	public Socket createSocket(final InetAddress host, final int port) throws IOException {
		final Socket underlyingSocket = underlyingSSLSocketFactory.createSocket(host, port);
		return overrideProtocol(underlyingSocket);
	}

	@Override
	public Socket createSocket(final InetAddress host, final int port, final InetAddress localAddress,
			final int localPort) throws IOException {
		final Socket underlyingSocket = underlyingSSLSocketFactory.createSocket(host, port, localAddress, localPort);
		return overrideProtocol(underlyingSocket);
	}

	/**
	 * Set the {@link javax.net.ssl.SSLSocket#getEnabledProtocols() enabled
	 * protocols} to {@link #enabledProtocols} if the <code>socket</code> is a
	 * {@link SSLSocket}
	 *
	 * @param socket
	 *            The Socket
	 * @return
	 */
	private Socket overrideProtocol(final Socket socket) {
		if (socket instanceof SSLSocket) {
			if (enabledProtocols != null && enabledProtocols.length > 0) {
				((SSLSocket) socket).setEnabledCipherSuites(enabledCiphers);
				((SSLSocket) socket).setEnabledProtocols(enabledProtocols);
			}
		}
		return socket;
	}
}
