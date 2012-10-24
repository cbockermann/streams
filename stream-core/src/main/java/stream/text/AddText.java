/**
 * 
 */
package stream.text;

import java.io.File;
import java.net.URL;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.util.URLUtilities;

/**
 * @author chris
 * 
 */
public class AddText extends AbstractProcessor {

	String file;

	String text;

	String key;

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);

		URL url = null;

		try {
			url = new URL(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (url == null) {
				File f = new File(file);
				url = f.toURI().toURL();
			}

			text = URLUtilities.readContent(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (text != null && key != null) {
			input.put(key, text);
		}

		return input;
	}
}