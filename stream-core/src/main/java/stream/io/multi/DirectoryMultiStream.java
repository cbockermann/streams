package stream.io.multi;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.AbstractStream;
import stream.io.SourceURL;
import stream.io.Stream;

/**
 * @author Hendrik Blom
 * 
 */
public class DirectoryMultiStream extends AbstractMultiStream {

	static Logger log = LoggerFactory.getLogger(DirectoryMultiStream.class);

	private File dir;
	private static BlockingQueue<String> files;
	private String suffix;
	private static AtomicInteger counter;
	private static AtomicBoolean filesAreRead;
	// TODO order
	private String[] order;
	private AbstractStream stream;
	private boolean noStream = false;

	public DirectoryMultiStream(SourceURL url) {
		super(url);
		if (filesAreRead == null)
			filesAreRead = new AtomicBoolean(false);
		if (files == null)
			files = new LinkedBlockingQueue<String>();
	}

	public DirectoryMultiStream(InputStream in) {
		super(in);
	}

	public DirectoryMultiStream() {
		super();
	}

	public String[] getOrder() {
		return order;
	}

	public void setOrder(String[] order) {
		this.order = order;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public void init() throws Exception {
		// super.init();
		if (!filesAreRead.getAndSet(true)) {
			if (streams.size() != 1) {
				throw new IllegalArgumentException(
						"Only one inner Stream is supported!");
			}

			dir = new File(this.url.getFile());
			if (!dir.isDirectory()
					&& (!(this.url.getProtocol()
							.equals(SourceURL.PROTOCOL_FILE)) || !(this.url
							.getProtocol().equals(SourceURL.PROTOCOL_CLASSPATH))))
				throw new IllegalArgumentException(
						"Given URL is no local directory");
			else {
				String[] f = null;
				if (suffix != null && !suffix.isEmpty()) {
					f = dir.list(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String name) {
							if (name.startsWith(".") || !name.endsWith(suffix))
								return false;
							return true;
						}
					});
					for (String file : f) {
						files.add(file);
					}
				} else {
					f = dir.list();
					List<String> fil = Arrays.asList(f);

					for (String file : order) {
						if (fil.contains(file))
							files.add(file);
					}
				}
			}

		}

	}

	private SourceURL createUrl(String fileName) throws Exception {
		String urlString = url.getProtocol() + ":" + url.getPath() + "/"
				+ fileName;
		return new SourceURL(urlString);
	}

	@Override
	public Data readNext() throws Exception {
		// if (noStream)
		// return null;
		if (stream == null) {
			String f = files.poll(2, TimeUnit.SECONDS);
			if (f == null)
				return null;
			else {
				Stream tmpStream = streams.get(additionOrder.get(0));
				if (!(tmpStream instanceof AbstractStream))
					throw new IllegalArgumentException(
							"Only Subclasses of AbstractStream are supported!");

				stream = (AbstractStream) tmpStream;
				stream.close();
				SourceURL surl = createUrl(f);
				stream.setUrl(surl);
				stream.init();
			}
		}

		Data data = stream.read();
		if (data != null)
			return data;

		String f = files.poll();
		if (f == null)
			return null;
		else {
			stream.close();
			SourceURL surl = null;
			surl = createUrl(f);
			stream.setUrl(surl);
			stream.init();
			data = stream.read();
			return data;
		}
	}
}