/**
 * 
 */
package streams.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * @author chris
 *
 */
public class ProtocolServerFactory extends SSLServerSocketFactory {

	String[] enabledProtocols = new String[] { "TLSv1" };
	SSLServerSocketFactory delegate;
	final String[] enabledCiphers;

	public ProtocolServerFactory(SSLServerSocketFactory delegate) {
		this.delegate = delegate;

		ArrayList<String> ciphers = new ArrayList<String>();
		for (int i = 0; i < delegate.getSupportedCipherSuites().length; i++) {
			String cipher = delegate.getSupportedCipherSuites()[i];
			if (cipher.toLowerCase().indexOf("krb") < 0) {
				ciphers.add(cipher);
			}
		}

		enabledCiphers = ciphers.toArray(new String[ciphers.size()]);

	}

	/**
	 * @return
	 * @throws IOException
	 * @see javax.net.ServerSocketFactory#createServerSocket()
	 */
	public ServerSocket createServerSocket() throws IOException {
		ServerSocket sock = delegate.createServerSocket();
		return overrideProtocol(sock);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return
	 * @throws IOException
	 * @see javax.net.ServerSocketFactory#createServerSocket(int, int,
	 *      java.net.InetAddress)
	 */
	public ServerSocket createServerSocket(int arg0, int arg1, InetAddress arg2) throws IOException {
		ServerSocket sock = delegate.createServerSocket(arg0, arg1, arg2);
		return overrideProtocol(sock);

	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws IOException
	 * @see javax.net.ServerSocketFactory#createServerSocket(int, int)
	 */
	public ServerSocket createServerSocket(int arg0, int arg1) throws IOException {
		ServerSocket sock = delegate.createServerSocket(arg0, arg1);
		return overrideProtocol(sock);
	}

	/**
	 * @param arg0
	 * @return
	 * @throws IOException
	 * @see javax.net.ServerSocketFactory#createServerSocket(int)
	 */
	public ServerSocket createServerSocket(int arg0) throws IOException {
		ServerSocket sock = delegate.createServerSocket(arg0);
		return overrideProtocol(sock);
	}

	/**
	 * @return
	 * @see javax.net.ssl.SSLServerSocketFactory#getDefaultCipherSuites()
	 */
	public String[] getDefaultCipherSuites() {
		return delegate.getDefaultCipherSuites();
	}

	/**
	 * @return
	 * @see javax.net.ssl.SSLServerSocketFactory#getSupportedCipherSuites()
	 */
	public String[] getSupportedCipherSuites() {
		return delegate.getSupportedCipherSuites();
	}

	private ServerSocket overrideProtocol(final ServerSocket socket) {
		if (socket instanceof SSLServerSocket) {
			if (enabledProtocols != null && enabledProtocols.length > 0) {
				((SSLServerSocket) socket).setEnabledCipherSuites(enabledCiphers);
				((SSLServerSocket) socket).setEnabledProtocols(enabledProtocols);
			}
		}
		return socket;
	}
}
