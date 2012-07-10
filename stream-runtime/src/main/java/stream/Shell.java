package stream;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.DefaultNamingService;
import stream.runtime.StreamRuntime;
import stream.shell.Call;
import stream.shell.Command;
import stream.shell.Discover;
import stream.shell.Info;
import stream.shell.List;

public class Shell {

	static Logger log = LoggerFactory.getLogger(Shell.class);

	final static String VERSION = "0.01";
	final String prompt = "streams> ";
	final DefaultNamingService namingService = new DefaultNamingService();

	final Map<String, String> env = new LinkedHashMap<String, String>();
	final Map<String, Command> commands = new LinkedHashMap<String, Command>();

	public Shell() throws Exception {
		commands.put("discover", new Discover(this));
		commands.put("shutdown", new stream.shell.Shutdown(this));
		commands.put("info", new Info(this));
		commands.put("call", new Call(this));
		commands.put("list", new List(this));
		commands.put("environment", new Environment(this));
		commands.put("version", new Version(this));
		// commands.get("discover").execute(new String[0]);
	}

	public String eval(String line) throws Exception {
		log.info("Executing {}", line);

		if ("help".equalsIgnoreCase(line.trim())) {
			System.out.println("  Available commands:\n");
			for (String key : commands.keySet()) {
				System.out.println("    " + key);
			}
			return "\n";
		}

		for (String key : env.keySet()) {
			if (key.startsWith("#")) {
				line = line.replace(key, env.get(key));
			}
		}
		log.info("After replacement: {}", line);

		for (String key : commands.keySet()) {
			String[] args = line.split("\\s+", 2);
			if (key.equalsIgnoreCase(args[0]) || key.startsWith(args[0])) {
				log.info("Found command {}", commands.get(key));

				String params[] = new String[0];
				if (args.length > 1) {
					params = args[1].split("\\s+");
				}
				System.out.println();
				return commands.get(key).execute(params);
			}
		}

		log.info("No command found for '{}'", line);
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

	public void set(String key, String value) {
		env.put(key, value);
	}

	public String get(String key) {
		return env.get(key);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		StreamRuntime.setupLogging();

		int port = 9105;
		String host = "192.168.10.2";

		if (args.length > 1) {
			host = args[0];
			port = new Integer(args[1]);
		}
		System.out.println("  java.rmi.server.codebase = "
				+ System.getProperty("java.rmi.server.codebase"));
		System.out.println("connecting to " + host + ":" + port + "...");
		Shell shell = new Shell();
		shell.repl(System.in, System.out);
	}

	public final class Environment extends Command {

		/**
		 * @param shell
		 */
		public Environment(Shell shell) {
			super(shell);
		}

		@Override
		public String execute(String[] args) {
			println("Environment:");
			println("------------");
			for (String key : env.keySet()) {
				println("  " + key + "  =>  " + env.get(key));
			}
			println("");
			return "";
		}
	}

	public final class Version extends Command {

		/**
		 * @param shell
		 */
		public Version(Shell shell) {
			super(shell);
		}

		/**
		 * @see stream.shell.Command#execute(java.lang.String[])
		 */
		@Override
		public String execute(String[] args) {
			println("stream.Shell - Version " + VERSION);
			return "";
		}
	}
}
