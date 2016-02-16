/**
 * 
 */
package profiler.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import streams.tikz.Path;
import streams.tikz.Point;

/**
 * @author chris
 *
 */
public class DataRatePlot {

    static Logger log = LoggerFactory.getLogger(DataRatePlot.class);

    List<File> files = new ArrayList<File>();
    List<TimeSeries> data = new ArrayList<TimeSeries>();

    public DataRatePlot(File... files) throws IOException {
        for (File file : files) {
            this.files.add(file);
            data.add(new TimeSeries(file, "rate:time", "rate"));
        }
    }

    public static List<String> lines(File file) throws IOException {
        return Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
    }

    public String toString() {

        StringWriter w = new StringWriter();
        PrintWriter p = new PrintWriter(w);

        String[] colors = "hellblau,hellblauRand,lila,lilaRand".split(",");

        double max = data.get(0).max();
        long t0 = data.get(0).start();
        long tmax = data.get(0).end();
        int maxSteps = data.get(0).values.size();
        for (int i = 1; i < data.size(); i++) {
            max = Math.max(max, data.get(i).max());
            maxSteps = Math.max(maxSteps, data.get(i).values.size());
            t0 = Math.min(t0, data.get(i).start());
            tmax = Math.max(tmax, data.get(i).end());

        }
        maxSteps = 13;
        log.info("t0 = {}, t_max = {}", t0, tmax);
        double span = (tmax * 1.0) - (t0 * 1.0);
        log.info(" tmax - t0 = {}", tmax - t0);
        log.info(" span = {}", span);
        log.info("maxSteps = {}", maxSteps);

        double x = 0.0;
        double xstep = 1.0;

        double scale = 8.0 / max;
        double scaleX = 12 / span;

        double xOff = t0;

        log.info("scale-x = {}", scaleX);

        StringBuffer s = new StringBuffer();

        for (int idx = 0; idx < data.size(); idx++) {
            TimeSeries vals = data.get(idx);

            Path path = vals.toPath(xOff, scaleX, 0.0, scale);
            //
            // x = 0.0;
            //
            // // Point last = null;
            // Path path = new Path();
            // if (vals.color == null) {
            // path.color = colors[idx % colors.length];
            // } else {
            // path.color = vals.color;
            // }
            //
            // path.opts.putAll(vals.opts);
            //
            // Iterator<Point> it = vals.points();
            // while (it.hasNext()) {
            // Point pt = it.next();
            // x = (pt.x - t0);
            //
            // Point cur = new Point(x * scaleX, pt.y * scale);
            // path.add(cur);
            // }
            // s.append("%path:\n");
            // s.append(path.toString());
            // p.println("%path:");
            // p.println(path.toString());

            s.append(path.toString() + "\n");
        }

        x = 0.0;
        for (double i = 0; i < maxSteps; i++) {
            x = 1.0 * i;
            // if (i % 10 == 0) {
            // p.println("\\draw[black, thin] " + new Point(x, 0) + " -- " + new
            // Point(x, -0.25) + ";");
            // }

            if (i % 1 == 0) {
                p.println("\\draw[black!30, thin, dashed, opacity=0.75] " + new Point(x, 9) + " -- " + new Point(x, 0)
                        + ";");
            }

            x += xstep;
        }

        double dy = 0;

        for (int i = 1; dy * scale <= 9.0; i++) {

            double y = dy * scale;

            p.println("\\draw[black!40,thin,opacity=0.75] " + new Point(0, y) + " -- " + new Point(maxSteps * 1.0, y)
                    + ";");

            p.println("\\node[anchor=east,scale=0.65] at " + new Point(-0.25, y) + " { \\color{black!60}\\textsf{" + dy
                    + "} };");
            dy += 10000;
        }

        p.println(s.toString());

        p.println("\\draw[black!80,thin,->] " + new Point(0, 0) + " -- " + new Point(maxSteps * 1.0, 0) + ";");
        p.println("\\draw[black!80,thin] " + new Point(0, 0) + " -- " + new Point(0, 9) + ";");

        p.close();
        return w.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] params) throws Exception {
        // /Users/chris/logs/write-rate.csv,
        String[] args = "/Users/chris/logs/write-rate.csv,/Users/chris/logs/rate-0.csv,/Users/chris/logs/rate-1.csv,/Users/chris/logs/rate-2.csv,/Users/chris/logs/rate-3.csv"
                .split(",");

        File[] in = new File[args.length];
        for (int i = 0; i < in.length; i++) {
            in[i] = new File(args[i]);
        }

        DataRatePlot plot = new DataRatePlot(in);

        TimeSeries sum = new TimeSeries("sum");

        TreeSet<Long> t = new TreeSet<Long>();

        for (TimeSeries ts : plot.data) {
            if (ts.name.indexOf("write") >= 0) {
                ts.opts.put("thick", "");
                ts.opts.put("draw", "orangeRand");
                ts.color = "orangeRand";
                continue;
            }

            t.addAll(ts.times);
        }

        for (Long time : t) {
            double sumVal = 0.0;
            for (TimeSeries ts : plot.data) {
                if (ts.name.indexOf("write") >= 0) {
                    continue;
                }
                double val = ts.value(time);
                sumVal += val;
            }
            sum.add(time, sumVal);
        }

        sum.color = "hellblau";
        sum.add(t.last(), 0.0);
        sum.add(t.first(), 0.0);

        sum.opts.put("fill", sum.color);
        sum.opts.put("thin", "");
        sum.opts.put("opacity", "0.5");
        plot.data.add(0, sum);

        log.info("sum has {} time points", t.size());

        FileWriter out = new FileWriter(new File("/Users/chris/logs/plot-data.tex"));
        out.write(plot.toString());
        out.close();
        // System.out.println(plot.toString());
    }
}
