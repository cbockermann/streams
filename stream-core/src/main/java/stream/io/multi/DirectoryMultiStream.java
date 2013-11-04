package stream.io.multi;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.AbstractStream;
import stream.io.SourceURL;
import stream.io.Stream;

public class DirectoryMultiStream extends AbstractMultiStream {

	static Logger log = LoggerFactory.getLogger(DirectoryMultiStream.class);

	private File dir;
	private String[] files;
	private int counter;
	// TODO order
	private String order;
	private AbstractStream stream;

	public DirectoryMultiStream(SourceURL url) {
		super(url);
	}

	public DirectoryMultiStream(InputStream in) {
		super(in);
	}

	public DirectoryMultiStream() {
		super();
	}
	
	
	@Override
	public void init() throws Exception {
//		super.init();
		counter = 0;
		if (streams.size() != 1) {
			throw new IllegalArgumentException(
					"Only one inner Stream is supported!");
		}
		
		dir = new File(this.url.getFile());
		if (!dir.isDirectory()
				&& (!(this.url.getProtocol().equals(SourceURL.PROTOCOL_FILE)) || !(this.url
						.getProtocol().equals(SourceURL.PROTOCOL_CLASSPATH))))
			throw new IllegalArgumentException(
					"Given URL is no local directory");
		else
			// TODO File order
			this.files = dir.list(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					if(name.startsWith("."))
						return false;
					return true;
				}
			});

		Stream tmpStream = streams.get(additionOrder.get(0));
		if (!(tmpStream instanceof AbstractStream))
			throw new IllegalArgumentException(
					"Only Subclasses of AbstractStream are supported!");
		

		stream = (AbstractStream) tmpStream;
		stream.close();

		if(files.length>0){
			SourceURL surl=createUrl(files[counter]);
			log.info(surl.toString());
			stream.setUrl(surl);		}
		else
			throw new IllegalArgumentException("Directory is empty");
		stream.init();

	}

	private SourceURL createUrl(String fileName) throws Exception {
		String urlString = url.getProtocol() + ":" + url.getPath() + "/"
				+ fileName;
		return new SourceURL(urlString);
	}

	@Override
	public Data readNext() throws Exception {

		Data data = stream.read();
		if (data != null)
			return data;

		counter++;
		if (counter < files.length) {
			stream.close();
			SourceURL surl=createUrl(files[counter]);
			log.info(surl.toString());
			stream.setUrl(surl);
			
			stream.init();
			data = stream.read();
			return data;
		}
		return null;
	}
}