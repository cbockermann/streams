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

import stream.runtime.rpc.ContainerAnnouncement;
import stream.runtime.rpc.Discovery;
import stream.runtime.rpc.RMIClient;

public class Shell {

	static String prompt = "streams> ";
	static RMIClient namingService;
	static Discovery discovery = null;
	static Map<String, RMIClient> clients = new LinkedHashMap<String, RMIClient>();

	public static String eval(String line) throws Exception {

		if (line.equals("discover")) {
			StringBuffer s = new StringBuffer(
					"\nContainers:\n-------------\n\n");

			discovery.discover();

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

			Map<String, String> info = namingService.getServiceInfo(args[1]);
			if (info == null) {
				return "No information for service " + args[1] + " available!";
			} else {
				StringBuffer s = new StringBuffer();
				for (String key : info.keySet()) {
					s.append("  " + key + "  =>  " + info.get(key));
					s.append("\n");
				}
				return s.toString();
			}
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

	public static String call(String name, String method, String... args) {

		Serializable[] params = new Serializable[args.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = args[i];
		}

		try {
			return "" + namingService.call(name, method, "", params);
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	public static void repl(InputStream in, OutputStream out) throws Exception {

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
				Map<String, String> list = namingService.list();
				writer.println("Registered Services:");
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

		discovery = new Discovery(9200);
		ContainerAnnouncement container = discovery.discover();
		System.out.println("Found container: " + container);

		// System.setSecurityManager( new RMISecurityManager() );
		System.out.println("Connecting to RMI naming service '"
				+ container.getName() + "' at port " + container.getPort()
				+ "...");
		namingService = new RMIClient(container.getHost(), container.getPort());
		System.out.println("Naming service is: " + namingService);

		repl(System.in, System.out);

	}
}
