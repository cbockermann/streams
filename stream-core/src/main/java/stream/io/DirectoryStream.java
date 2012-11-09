/**
 * 
 */
package stream.io;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.annotations.Description;

/**
 * This stream reads the filenames from the given directory. The provided
 * dataItem includes the full qualified filename (@url), the directory
 * (@directory) and the filename(@filename).
 * 
 * @author Hendrik Blom
 * 
 */
@Description(group = "Data Stream.Sources")
public class DirectoryStream extends AbstractDataStream {

	Logger log = LoggerFactory.getLogger(DirectoryStream.class);

	private File dir;
	private String[] files;
	private String dirPath;
	private int counter;

	public DirectoryStream(SourceURL url) throws Exception {
		super(url);
		this.initReader();

	}

	@Override
	protected void initReader() throws Exception {
		dir = new File(new URI(url.toString()));
		if (!dir.isDirectory())
			throw new IllegalArgumentException("Directory not found");
		dirPath = dir.getAbsolutePath();
		files = dir.list();
		counter = 0;
	}

	@Override
	public void readHeader() throws Exception {
	}

	@Override
	public Data readItem(Data data) throws Exception {
		if (counter < files.length) {
			data.put(
					"@url",
					new URL("file:" + dirPath
							+ System.getProperty("file.separator")
							+ files[counter]));
			data.put("@directory", dirPath);
			data.put("@filename", files[counter]);
			counter++;
			return data;
		}
		return null;
	}

	@Override
	public void close() {
	}
}