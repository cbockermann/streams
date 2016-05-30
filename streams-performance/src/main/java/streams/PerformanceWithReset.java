/**
 *
 */
package streams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import stream.Data;
import stream.ProcessorList;
import streams.logging.Message;
import streams.performance.ProcessorStatistics;

/**
 * This class implements a processor list which aggregates timing information about all its inner
 * processors during execution. The timing is performed based on the wall-clock time (using
 * System.nanoTime()) and is determined with every processed item.
 *
 * @author Christian Bockermann
 */
public class PerformanceWithReset extends Performance {

    static Logger log = LoggerFactory.getLogger(PerformanceWithReset.class);

    boolean processed = false;

    final ProcessorStatistics myStatistics;

    public PerformanceWithReset() {
        myStatistics = new ProcessorStatistics(this);
    }

    /**
     * @see ProcessorList#process(Data)
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
            processed = false;
        } else {
            processed = true;
        }

        return result;
    }

    /**
     * @see ProcessorList#finish()
     */
    @Override
    public void finish() throws Exception {
        log.info("Performance.finish()...");
//        super.finish();

        if (processed) {
            logPerformance();
        }
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

    /**
     * Send performance data to performance receiver. This method is called in the finish method of
     * Performance processor. Furthermore, if 'every' parameter was defined in the XML, then after
     * every X-th item performance is sent to receiver.
     */
    @Override
    public void logPerformance() {
        if (sender != null && items > 0) {
            Message m = rlog.message().add("performance.id", context.path());

            m.add("performance.stats", new ProcessorStatistics(myStatistics));

            // clone statistics
            ProcessorStatistics[] stats = new ProcessorStatistics[statistics.length];
            for (int i = 0; i < statistics.length; i++) {
                stats[i] = new ProcessorStatistics(statistics[i]);
            }

            m.add("processors", stats);
            sender.add(m);

            // reset statistics
            myStatistics.reset();
            for (ProcessorStatistics statistic : statistics) {
                statistic.reset();
            }
        }
    }
}