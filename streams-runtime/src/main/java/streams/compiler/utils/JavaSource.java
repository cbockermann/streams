/**
 * 
 */
package streams.compiler.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class JavaSource extends SimpleJavaFileObject {

	static Logger log = LoggerFactory.getLogger(JavaSource.class);
	private final CharSequence code;
	private final ByteArrayOutputStream byteCode = new ByteArrayOutputStream(
			16 * 1024);

	private final String name;

	public JavaSource(String name, CharSequence code) {
		super(URI.create("string:///" + name.replace('.', '/')
				+ Kind.SOURCE.extension), Kind.SOURCE);
		this.code = code;
		this.name = name;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}

	/**
	 * @see javax.tools.SimpleJavaFileObject#openInputStream()
	 */
	@Override
	public InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream(byteCode.toByteArray());
	}

	/**
	 * @see javax.tools.SimpleJavaFileObject#openOutputStream()
	 */
	@Override
	public OutputStream openOutputStream() throws IOException {
		log.info(
				"Returning byte-array-output stream to receive byte code for class '{}'",
				name);
		return byteCode;
	}

	public byte[] getByteCode() {
		return byteCode.toByteArray();
	}
}
