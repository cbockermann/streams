/**
 * 
 */
package stream.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.expressions.MacroExpander;
import stream.statistics.History;
import stream.util.Time;

/**
 * @author chris
 * 
 */
public class BinaryWriter extends AbstractWriter {

	static Logger log = LoggerFactory.getLogger(BinaryWriter.class);
	final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	final SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
	final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
	File file;
	File current;
	OutputStream os;
	long bytesWritten = 0L;
	Time rotate = new Time(1 * History.MINUTE);

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		current = file;
		if (!file.getParentFile().isDirectory()) {
			file.getParentFile().mkdirs();
		}
		os = new FileOutputStream(file);
	}

	/**
	 * @see stream.io.AbstractWriter#write(stream.Data)
	 */
	@Override
	public void write(Data input) throws Exception {
		Set<String> ks = selectedKeys(input);

		if (!ks.isEmpty()) {

			for (String key : ks) {
				Serializable value = input.get(key);
				if (value != null && value.getClass().isArray()
						&& value.getClass().getComponentType() == byte.class) {
					try {
						byte[] data = (byte[]) value;
						long ts = System.currentTimeMillis();
						Date now = new Date(ts);
						input.put("timestamp", ts);
						input.put("datetime", fmt.format(now));
						input.put("time", timeFormat.format(now));
						input.put("date", dateFormat.format(now));

						File df = new File(MacroExpander.expand(
								file.getAbsolutePath(), input));
						if (!df.getCanonicalPath().endsWith(
								current.getCanonicalPath())) {
							log.info("Closing file {}",
									current.getAbsoluteFile());
							os.flush();
							os.close();

							if (!df.getParentFile().isDirectory()) {
								df.getParentFile().mkdirs();
							}

							os = new FileOutputStream(df);
							current = df;
						}

						os.write(data);
						os.flush();
						bytesWritten += data.length;
						input.put("file", "file:" + df.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
}
