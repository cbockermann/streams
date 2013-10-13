package stream.runtime;

import java.net.URL;
import java.util.Map;

/**
 * @author hendrik
 * 
 */
public class Container implements Runnable {

	private URL url;
	private Map<String, String> args;

	public Container(URL url) {
		this.url = url;
	}

	public Container(URL url, Map<String, String> args) {
		this(url);
		this.args = args;
	}

	public void run() {
		try {
			if (this.args == null)
				stream.run.main(this.url);
			else
				stream.run.mainWithMap(this.url, this.args);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
