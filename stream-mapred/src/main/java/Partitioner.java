import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.CommandLineArgs;

public class Partitioner {

	static Logger log = LoggerFactory.getLogger(Partitioner.class);

	/**
	 * @param args
	 */
	public static void main(String[] params) {
		String[] args = params;
		URL url;

		if (args.length < 1) {
			System.out.println("Usage:");
			System.out
					.println("    java ... stream.hadoop.Partitioner [-b LINES] [-l LIMIT] URL");
			System.out.println();
			return;
		}

		CommandLineArgs cla = new CommandLineArgs(params);
		cla.dumpArgs();
		cla.setSystemProperties("partitioner");

		if (cla.getArguments().size() > 0)
			System.setProperty("partitioner.input.url",
					cla.getArguments().get(0));

		if (cla.getArguments().size() > 1)
			System.setProperty("partitioner.output", cla.getArguments().get(1));

		try {

			int limit = Integer.parseInt(cla.getOption("limit", ""
					+ Integer.MAX_VALUE));
			int lines = Integer.parseInt(cla.getOption("block.size", "1000"));
			int parts = Integer.parseInt(cla.getOption("max.parts", "10"));

			System.out.println("Using block-size of " + lines + " lines");
			System.out.println("Creating blocks from a maximum of " + limit
					+ " examples");

			log.info("Start[Partitioner]");

			url = new URL(cla.getArguments().get(0));
			File outputDirectory = new File(".");

			if (cla.getArguments().size() > 1)
				outputDirectory = new File(cla.getArguments().get(1));

			log.info("   Starting Partition for data {} to {}", url,
					outputDirectory);
			log.info("   Creating {} partitions of size {}", parts, lines);
			long start = System.currentTimeMillis();
			stream.Partitioner p = new stream.Partitioner();
			if (cla.getOption("shuffle") != null)
				p.shuffledPartitions(lines, parts, limit, url, outputDirectory);
			else
				p.createPartitions(lines, parts, limit, url, outputDirectory);

			log.info("   Partitioning completed.");
			log.info("   Partitioner required {} ms",
					System.currentTimeMillis() - start);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			log.info("Partitioning failed: {}", e.getMessage());
		}

		log.info("End[Partitioner]");
	}
}