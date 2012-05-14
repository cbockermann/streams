/**
 * 
 */
package stream.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.rapidminer.tools.plugin.Plugin;

/**
 * @author chris
 * 
 */
public class FakePlugin extends Plugin {

	final static URL url = FakePlugin.class.getResource("/FakePlugin.jar");
	final static File fakeFile = new File(url.getFile());

	String name;
	String version;
	String extensionId;
	String prefix;

	public static Plugin createPlugin(String name, String version,
			String extensionId) {
		try {
			return new FakePlugin(name, version, extensionId);
		} catch (Exception e) {
			return null;
		}
	}

	private FakePlugin() throws IOException {
		this("FakePlugin", "1.0", "rmx_fake");
	}

	/**
	 * @param file
	 * @throws IOException
	 */
	private FakePlugin(String name, String version, String extensionId)
			throws IOException {
		super(fakeFile);
		this.name = name;
		this.version = version;
		this.extensionId = extensionId;
	}

	/**
	 * @see com.rapidminer.tools.plugin.Plugin#getName()
	 */
	@Override
	public String getName() {
		if (name != null)
			return name;
		return super.getName();
	}

	/**
	 * @see com.rapidminer.tools.plugin.Plugin#getVersion()
	 */
	@Override
	public String getVersion() {
		if (version != null)
			return version;
		return super.getVersion();
	}

	/**
	 * @see com.rapidminer.tools.plugin.Plugin#getPrefix()
	 */
	@Override
	public String getPrefix() {
		if (prefix != null)
			return prefix;
		return super.getPrefix();
	}

	/**
	 * @see com.rapidminer.tools.plugin.Plugin#getExtensionId()
	 */
	@Override
	public String getExtensionId() {
		if (this.extensionId != null)
			return extensionId;
		return super.getExtensionId();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		try {

			URL url = FakePlugin.class.getResource("/FakePlugin.jar");
			System.out.println("URL is: " + url);
			File file = new File(url.getFile());
			System.out.println("File is: " + file);
			FakePlugin p = new FakePlugin("FakePlugin", "1.0", "rmx_fake");
			System.out.println("plugin is " + p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}