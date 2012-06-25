package stream;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.DefaultNamingService;
import stream.runtime.rpc.ContainerAnnouncement;
import stream.runtime.rpc.Discovery;
import stream.runtime.rpc.RMIClient;
import stream.service.ServiceInfo;

public class Shell {

	static Logger log = LoggerFactory.getLogger(Shell.class);

	String prompt = "streams> ";
	// RMIClient namingService;
	Discovery discovery = null;
	Map<String, RMIClient> clients = new LinkedHashMap<String, RMIClient>();
	final DefaultNamingService namingService = new DefaultNamingService();

	public Shell() {
		try {
			discovery = new Discovery(9200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String eval(String line) throws Exception {
		log.info("Executing {}", line);

		if (line.equalsIgnoreCase("list")) {
			Map<String, ServiceInfo> list = namingService.list();
			StringBuffer s = new StringBuffer();
			s.append("Registered Services:\n");
			s.append("====================");
			for (String key : list.keySet()) {
				s.append("   " + key + "  ~>  " + list.get(key));
			}
			return s.toString();
		}

		if (line.equals("discover")) {
			StringBuffer s = new StringBuffer(
					"\nContainers:\n-------------\n\n");

			log.info("Running discovery.discover()");
			discovery.discover();

			try {
				for (String key : discovery.getAnnouncements().keySet()) {
					ContainerAnnouncement ref = discovery.getAnnouncements()
							.get(key);

					log.info("Adding RMI connection for container {}", ref);
					if (namingService.getContainer(key) != null) {
						log.info("Container already known: {}",
								namingService.getContainer(key));
					} else {
						this.namingService.addContainer(key,
								new RMIClient(ref.getHost(), ref.getPort()));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Map<String, Long> list = discovery.getContainers();
			for (String key : list.keySet()) {
				s.append("  " + key + "  (" + new Date(list.get(key)) + ")\n");

			}
			s.append("\n");
			return s.toString();
		}

		if (line.startsWith("info")) {

			String[] args = line.split("\\s+");
			if (args.length != 2) {
				return "Error: Missing service name to command 'info'!";
			}

			/*
			 * 
			 * Map<String, String> info = namingService.getServiceInfo(args[1]);
			 * if (info == null) { return "No information for service " +
			 * args[1] + " available!"; } else { StringBuffer s = new
			 * StringBuffer(); for (String key : info.keySet()) { s.append("  "
			 * + key + "  =>  " + info.get(key)); s.append("\n"); } return
			 * s.toString(); }
			 */

		}

		if (line.startsWith("call")) {

			String[] args = line.split("\\s+");
			if (args.length < 3) {
				return "Error: command 'call' requires at least name and method of the call!";
			}

			String name = args[1];
			String method = args[2];
			String[] params = new String[0];
			if (args.length > 3) {
				params = new String[args.length - 3];
				for (int i = 0; i < params.length; i++) {
					params[i] = args[i + 3];
				}
			}
			return call(name, method, params);
		}

		return "";
	}

	public String call(String name, String method, String... args) {

		Serializable[] params = new Serializable[args.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = args[i];
		}

		/*
		 * try { return "" + namingService.call(name, method, "", params); }
		 * catch (Exception e) { return "Error: " + e.getMessage(); }
		 */
		return "";
	}

	public void repl(InputStream in, OutputStream out) throws Exception {

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		PrintWriter writer = new PrintWriter(out);

		writer.print(prompt);
		writer.flush();
		String line = reader.readLine();
		while (line != null) {

			line = line.trim();
			if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
				break;
			}

			if (line.equalsIgnoreCase("list")) {
				Map<String, ServiceInfo> list = namingService.list();
				writer.println("Registered Services:\n");
				writer.println("====================");
				for (String key : list.keySet()) {
					writer.println("   " + key + "  ~>  " + list.get(key));
				}
				writer.println();
				writer.flush();
			}

			String output = eval(line);
			writer.println(output);
			writer.print(prompt);
			writer.flush();
			line = reader.readLine();
		}

		reader.close();
		writer.close();
	}

	public DefaultNamingService getNamingService() {
		return this.namingService;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		int port = 9105;
		String host = "127.0.0.1";

		if (args.length > 1) {
			host = args[0];
			port = new Integer(args[1]);
		}

		System.out.println("connecting to " + host + ":" + port + "...");
		Shell shell = new Shell();
		shell.repl(System.in, System.out);
	}
}
