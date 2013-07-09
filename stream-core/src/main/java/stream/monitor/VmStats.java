/**
 * 
 */
package stream.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractStream;
import stream.util.Time;

/**
 * @author Christian Bockermann
 * 
 */
public class VmStats extends AbstractStream {

	static Logger log = LoggerFactory.getLogger(VmStats.class);
	String[] paths = new String[] { "/usr/bin", "/usr/sbin", "/usr/local/bin",
			"/usr/local/sbin" };

	String[] vmStats = new String[] { "vmstat", "vm_stat" };

	File vmstat = null;
	Process process;
	BufferedReader reader;
	Time interval = new Time(5000L);
	final ArrayList<String> keys = new ArrayList<String>();

	/**
	 * @see stream.io.AbstractStream#init()
	 */
	@Override
	public void init() throws Exception {
		super.init();

		for (String path : paths) {
			for (String probe : vmStats) {
				File f = new File(path + File.separator + probe);
				if (f.canExecute()) {
					vmstat = f;
					break;
				}
			}

			if (vmstat != null)
				break;
		}

		if (vmstat == null) {
			throw new Exception("Failed to locate vm_stat/vmstat utility!");
		}

		String cmd = vmstat.getAbsolutePath();
		Integer time = Math.max(1, interval.asSeconds().intValue());

		process = Runtime.getRuntime().exec(
				new String[] { cmd, time.toString() });
		reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
	}

	/**
	 * @see stream.io.AbstractStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		String line = reader.readLine();
		if (line == null)
			return null;

		if (line.trim().startsWith("Mach") || line.trim().startsWith("proc")) {
			line = reader.readLine(); // the headers
			String[] headers = line.trim().split("\\s+");
			keys.clear();

			for (String header : headers) {
				if (header.trim().isEmpty())
					continue;

				keys.add(header.trim());
			}

			line = reader.readLine();
		}

		String[] vals = line.trim().split("\\s+");

		Data item = DataFactory.create();
		item.put("@timestamp", System.currentTimeMillis());

		for (int i = 0; i < keys.size() && i < vals.length; i++) {
			String key = keys.get(i);
			String val = vals[i];
			Serializable value = val;

			try {
				if (val.toLowerCase().endsWith("k")) {
					value = new Double(val.substring(0, val.length() - 1));
				} else {
					value = new Double(val);
				}
			} catch (Exception e) {
				value = vals[i];
			}
			item.put("vmstats:" + key, value);
		}

		return item;
	}

	/**
	 * @see stream.io.AbstractStream#close()
	 */
	@Override
	public void close() throws Exception {
		super.close();
		process.destroy();
	}

	/**
	 * @return the interval
	 */
	public Time getInterval() {
		return interval;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(Time interval) {
		this.interval = interval;
	}

	public static void main(String[] args) throws Exception {

		VmStats vmstats = new VmStats();
		vmstats.setLimit(100L);

		vmstats.init();

		Data item = vmstats.read();
		while (item != null) {
			log.info("item: {}", item);
			item = vmstats.read();
		}

		vmstats.close();
	}
}
