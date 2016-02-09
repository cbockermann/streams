/**
 * 
 */
package stream.monitor;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class DataRateGroup {

    static Logger log = LoggerFactory.getLogger(DataRateGroup.class);

    final static Map<String, DataRateGroup> groups = new LinkedHashMap<String, DataRateGroup>();

    public static synchronized DataRateGroup get(String groupId) {
        DataRateGroup group = groups.get(groupId);
        if (group == null) {
            group = new DataRateGroup(groupId);
            groups.put(groupId, group);
        }
        return group;
    }

    final Object lock = new Object();

    final Set<String> members = new LinkedHashSet<String>();
    final Map<String, Result> results = new LinkedHashMap<String, Result>();

    final String groupId;

    public DataRateGroup(String id) {
        this.groupId = id;
    }

    public void register(String id) {
        synchronized (lock) {
            members.add(id);
        }
    }

    public void add(String id, Result result) {
        synchronized (lock) {
            results.put(id, result);

            if (results.size() == members.size()) {
                System.out.println("DataGroup results complete!");
                dumpResults();
            }
        }
    }

    public void dumpResults() {
        Long min = null;
        Long max = null;
        Double sum = 0.0;

        for (Result result : results.values()) {
            if (min == null) {
                min = result.start;
            }

            if (max == null) {
                max = result.end;
            }

            min = Math.min(min, result.start);
            max = Math.max(max, result.end);

            sum += result.items;
        }

        Double time = (max - min) / 1000.0d;
        DecimalFormat fmt = new DecimalFormat("0.000");
        log.info("Averaged total data rate for group '{}' is: {} items/sec", groupId, fmt.format(sum / time));
    }

    public static class Result {
        public final String id;
        public final long start;
        public final long end;
        public final long items;

        public Result(String id, long start, long end, long items) {
            this.id = id;
            this.start = start;
            this.end = end;
            this.items = items;
        }
    }
}