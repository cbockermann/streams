package streams.net;

/**
 * MergedPerformanceReceiver collects performance statistics data using {@link
 * streams.PerformanceWithReset} processor list that merges incoming data as it is reset every time
 * it is reported to this receiver.
 */
public class MergedPerformanceReceiver extends PerformanceReceiver {
    /**
     * Create performance receiver on a given port using SSL server connection.
     *
     * @param port number for service's port
     */
    public MergedPerformanceReceiver(int port) throws Exception {
        super(port);
    }

    @Override
    protected void initUpdater() {
        log.info("Initialize Merger as updater.");
        Merger merger = new Merger(updates, performanceTrees);
        merger.setDaemon(true);
        merger.start();
    }

    /**
     * Start merged performance receiver on a server.
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                log.error("You can only define port number as a parameter. Using default: 6001."
                        + args[0]);
            }
        }
        Runtime.getRuntime().addShutdownHook(new Dump());

        MergedPerformanceReceiver receiver = new MergedPerformanceReceiver(port);
        log.info("Starting merged-performance-receiver on port {}", receiver.server.getLocalPort());
        receiver.run();
    }
}
