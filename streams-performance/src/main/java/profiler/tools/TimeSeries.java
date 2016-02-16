/**
 * 
 */
package profiler.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import streams.tikz.Path;
import streams.tikz.Point;

/**
 * @author chris
 *
 */
public class TimeSeries {

    static Logger log = LoggerFactory.getLogger(TimeSeries.class);

    String name;
    public String color = null;
    List<Long> times = new ArrayList<Long>();
    List<Double> values = new ArrayList<Double>();

    long start = -1L;
    long end = -1L;
    double min;
    double max;
    public final Map<String, String> opts = new HashMap<String, String>();

    public TimeSeries(String name) {
        this.name = name;
    }

    public TimeSeries(File file, String x, String y) throws IOException {
        this.name = file.getName();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String line = reader.readLine();
            String[] header = line.split(",");
            int colTime = -1;
            int colValue = -1;

            for (int i = 0; i < header.length; i++) {
                if (header[i].equalsIgnoreCase(x)) {
                    colTime = i;
                    continue;
                }

                if (header[i].equalsIgnoreCase(y)) {
                    colValue = i;
                    continue;
                }
            }

            if (colTime < 0 || colValue < 0) {
                throw new IOException("Failed to identify columns!");
            }

            line = reader.readLine();
            while (line != null) {
                String[] cols = line.split(",");
                add(new Long(cols[colTime]), new Double(cols[colValue]));
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw e;
        } finally {
            reader.close();
        }
    }

    public void add(long t, double val) {
        long ts = map(t);

        if (times.isEmpty()) {
            start = ts;
            min = val;
            max = val;
        }

        end = ts;
        min = Math.min(min, val);
        max = Math.max(max, val);

        times.add(ts);
        values.add(val);
    }

    public Path toPath(double offX, double scaleX, double offY, double scaleY) {

        Path path = new Path();
        if (color == null) {
        } else {
            path.color = color;
        }

        path.opts.putAll(opts);

        Iterator<Point> it = points();
        while (it.hasNext()) {
            Point pt = it.next();
            double x = (pt.x - offX);

            Point cur = new Point(x * scaleX, pt.y * scaleY);
            path.add(cur);
        }
        return path;
    }

    public Iterator<Point> points() {
        Iterator<Point> it = new Iterator<Point>() {
            int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < times.size();
            }

            @Override
            public Point next() {
                Long time = times.get(idx);
                Double value = values.get(idx);
                idx++;
                return new Point(time.doubleValue(), value);
            }

            @Override
            public void remove() {
            }
        };

        return it;
    }

    public double value(long time) {
        long t = map(time);
        int idx = times.indexOf(t);
        if (idx >= 0) {
            return values.get(idx);
        }

        double interpol = 0.0;
        for (int i = 0; i < times.size(); i++) {
            long tt = times.get(i);
            long next = tt;
            if (i + 1 < times.size()) {
                next = times.get(i + 1);
            }

            if (tt < time && time < next) {
                log.info("interpolating data for {}, {}", i, i + 1);
                return (values.get(i) + values.get(i + 1)) * 0.5;
            }
        }

        return interpol;
    }

    protected long map(long ts) {
        return ts - (ts % 1000L);
    }

    public long start() {
        return start;
    }

    public long end() {
        return end;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }
}