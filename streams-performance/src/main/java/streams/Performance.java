/**
 *
 */
package streams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import stream.Data;
import stream.ProcessContext;
import stream.Processor;
import stream.ProcessorList;
import stream.annotations.Parameter;
import streams.logging.Message;
import streams.logging.Rlog;
import streams.net.MessageQueue.Sender;
import streams.performance.ProcessorStatistics;

/**
 * This class implements a processor list which aggregates timing information
 * about all its inner processors during execution. The timing is performed
 * based on the wall-clock time (using System.nanoTime()) and is determined with
 * every processed item.
 *
 * @author Christian Bockermann
 */
public class Performance extends ProcessorList {

    static Logger log = LoggerFactory.getLogger(Performance.class);
    Rlog rlog = new Rlog();
    String id = null;
    int every = 10000;

    long initStart = 0L;
    long initEnd = 0L;

    long items = 0L;
    long firstItem = 0L;
    long lastItem = 0L;

    long finishStart = 0L;
    long finishEnd = 0L;

    final ProcessorStatistics myStatistics;
    ProcessorStatistics[] statistics = new ProcessorStatistics[0];

    long ignoreFirst = 0;

    final static AtomicInteger global = new AtomicInteger(0);

    File output;
    String hostname;
    String path;

    /**
     * Host where performance receiver can be found
     */
    String host;

    /**
     * Port for performance receiver. Default value: 6001.
     */
    int port = 6001;
    Sender sender;

    public Performance() {
        myStatistics = new ProcessorStatistics(this);
    }

    /**
     * @see stream.ProcessorList#init(stream.ProcessContext)
     */
    @Override
    public void init(ProcessContext context) throws Exception {

        String appId = context.resolve("application.id") + "";
        // log.info("Application ID is: '{}'", appId);
        rlog.define("trace", appId);
        String pid = context.getId();
        // log.info("Process ID is: '{}'", pid);
        rlog.define("process.id", pid);

        path = appId + "/" + pid;

        initStart = System.currentTimeMillis();
        super.init(context);
        initEnd = System.currentTimeMillis();

        statistics = new ProcessorStatistics[this.processors.size()];
        for (int i = 0; i < statistics.length; i++) {
            Processor p = processors.get(i);
            statistics[i] = new ProcessorStatistics(p.getClass().getName(), p);
        }
        hostname = InetAddress.getLocalHost().getHostName();
        global.incrementAndGet();

        if (host != null) {
            log.info("Starting my own messenger...");
            sender = new Sender(host, port);
            sender.start();
        }

        if (output != null) {
            log.info("Writing final performance results to {}", output);
        }
    }

    /**
     * @see stream.ProcessorList#process(stream.Data)
     */
    @Override
    public Data process(Data data) {

        if (firstItem == 0L) {
            firstItem = System.currentTimeMillis();
        }

        items++;

        long t0 = System.nanoTime();
        Data result = data;
        if (statistics.length > 0) {
            result = this.executeInnerProcessors(data);
        }
        long t1 = System.nanoTime();

        if (items >= ignoreFirst) {
            myStatistics.addNanos(t1 - t0);
        }
        lastItem = System.currentTimeMillis();

        if (every > 0 && items % every == 0) {
            logPerformance();
        }

        return result;
    }

    public Data executeInnerProcessors(Data data) {

        if (data != null) {

            int i = 0;
            for (Processor p : processors) {
                long t0 = System.nanoTime();
                data = p.process(data);
                long t1 = System.nanoTime();

                if (items >= ignoreFirst) {
                    statistics[i].addNanos((t1 - t0));
                }

                // If any nested processor returns null we stop further processing.
                if (data == null) {
                    return null;
                }
                i++;
            }

        }
        return data;
    }

    /**
     * @see stream.ProcessorList#finish()
     */
    @Override
    public void finish() throws Exception {
        log.info("Performance.finish()...");
        super.finish();

        logPerformance();
        while (sender != null && sender.messagesPending() > 0) {
            log.debug("Waiting for sender to finish... {} messages pending", sender.messagesPending());
            try {
                sender.join(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (output != null) {
            log.info("Writing performance measurements to {}", output);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.newDocument();
            Element perf = doc.createElement("performances");
            doc.appendChild(perf);

            for (ProcessorStatistics ps : this.statistics) {
                Element proc = doc.createElement("processor");
                proc.setAttribute("class", ps.className);

                Element stats = doc.createElement("performance");
                stats.setAttribute("items", ps.itemsProcessed() + "");
                stats.setAttribute("time", ps.processingTime() + "");
                stats.setAttribute("start", ps.start() + "");
                stats.setAttribute("end", ps.end() + "");

                proc.appendChild(stats);
                perf.appendChild(proc);
            }

            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            tf.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(output)));
        } else {
            log.info("Attribute 'file' not specified. Not writing performance output...");
        }
    }

    public ProcessorStatistics[] getProcessorStatistics() {
        ProcessorStatistics[] ps = new ProcessorStatistics[this.statistics.length];
        for (int i = 0; i < ps.length; i++) {
            ps[i] = new ProcessorStatistics(statistics[i]);
        }
        return ps;
    }

    /**
     * Send performance data to performance receiver. This method is called in
     * the finish method of Performance processor. Furthermore, if 'every'
     * parameter was defined in the XML, then after every X-th item performance
     * is sent to receiver.
     */
    public void logPerformance() {
        if (sender != null && items > 1) {
            // log.info("current performance: {} items/sec",
            // (count.doubleValue() / seconds));
            Message m = rlog.message().add("performance.id", context.path());
            m.add("performance.stats", new ProcessorStatistics(this.myStatistics));
            m.add("processors", this.getProcessorStatistics());
            sender.add(m);
        }
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    @Parameter(description = "A custom identifier to associate with all the timing data produced by this processor list.")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the output
     */
    public File getFile() {
        return output;
    }

    /**
     * @param output
     *            the output to set
     */
    @Parameter(description = "An optional output file, to which performance stats will be written in XML format.", required = false)
    public void setFile(File output) {
        log.info("Setting output = {}", output);
        this.output = output;
    }

    /**
     * @return the every
     */
    public int getEvery() {
        return every;
    }

    /**
     * @param every
     *            the every to set
     */
    @Parameter(description = "Determines the interval after which performance stats are "
            + "emitted/written out, e.g. every 10 items.", required = false)
    public void setEvery(int every) {
        this.every = every;
    }

    /**
     * @return the ignoreFirst
     */
    public long getIgnoreFirst() {
        return ignoreFirst;
    }

    /**
     * @param ignoreFirst
     *            the ignoreFirst to set
     */
    @Parameter(description = "The number of items to be ignored in the beginning - to provide a "
            + "gap for just-in-time compilation to kick in.", required = false)
    public void setIgnoreFirst(long ignoreFirst) {
        this.ignoreFirst = ignoreFirst;
    }

    public String getPath() {
        return path;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     *            the host to set
     */
    @Parameter(description = "The host where to send the statistics to. If not set, the default "
            + "setting from rlog.host will be used.", required = false)
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    @Parameter(description = "The host where to send the statistics to. If not set, the default "
            + "setting from rlog.host will be used.", required = false)
    public void setPort(int port) {
        this.port = port;
    }
}