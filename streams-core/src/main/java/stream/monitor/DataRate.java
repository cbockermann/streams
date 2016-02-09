/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.monitor;

import java.text.DecimalFormat;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.annotations.Parameter;
import stream.data.Statistics;
import stream.monitor.DataRateGroup.Result;
import stream.statistics.StatisticsService;

public class DataRate extends AbstractProcessor implements StatisticsService {

    final DecimalFormat fmt = new DecimalFormat("0.000");
    static Logger log = LoggerFactory.getLogger(DataRate.class);
    String clock = null;
    Long count = 0L;
    Long start = null;

    Long windowCount = 0L;
    Long last = 0L;
    Double elapsed = 0.0d;
    Double rate = new Double(0.0);

    Integer every = null;
    String key = "dataRate";
    String id;

    @Parameter
    String group;

    final String internalId = UUID.randomUUID().toString();
    DataRateGroup dataRateGroup;

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
    public void setId(String id) {
        this.id = id;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @see stream.AbstractProcessor#init(stream.ProcessContext)
     */
    @Override
    public void init(ProcessContext ctx) throws Exception {
        super.init(ctx);
        // start = System.currentTimeMillis();

        if (group != null) {
            log.info("Registering to data-rate-group '{}'", group);
            dataRateGroup = DataRateGroup.get(group);
            dataRateGroup.register(internalId);
        }
    }

    @Override
    public Data process(Data input) {

        long now = System.currentTimeMillis();
        if (start == null)
            start = now;

        count++;
        if (every != null && count % every.intValue() == 0) {
            printDataRate(now);

            if (key != null) {
                Double sec = (now - start) / 1000.0;
                Double rate = count.doubleValue() / sec.doubleValue();
                input.put(key, rate);
                input.put(key + ":time", now);
                input.put(key + ":items", count.doubleValue());
            }
        }

        Long t = now - start;
        if (t > 0 && count % 10 == 0) {
            synchronized (rate) {
                rate = this.count.doubleValue() / (t.doubleValue() / 1000.0d);
            }
        }

        last = now;
        return input;
    }

    public void printDataRate() {
        printDataRate(System.currentTimeMillis());
    }

    protected void printDataRate(Long now) {
        Long sec = (now - start) / 1000;
        if (sec > 0)
            log.info("Data rate '" + getId() + "': {} items processed, data-rate is: {}/second", count,
                    fmt.format(count.doubleValue() / sec.doubleValue()));
    }

    /**
     * @see stream.AbstractProcessor#finish()
     */
    @Override
    public void finish() throws Exception {
        super.finish();

        if (start != null) {
            Long now = last; // System.currentTimeMillis();

            Long sec = (now - start);
            log.info("DataRate processor '{}' has been running for {} ms, {} items.", id, sec, count.intValue());
            Double s = sec.doubleValue() / 1000.0d;
            if (s > 0)
                log.info("Overall average data-rate for processor '{}' is: {}/second", id,
                        fmt.format(count.doubleValue() / s));

            if (dataRateGroup != null) {
                dataRateGroup.add(internalId, new Result(internalId, start, last, count));
            }
        } else {
            log.info("Start time not available.");
        }
    }

    @Override
    public void reset() throws Exception {
        count = 0L;
        windowCount = 1L;
        last = 0L;
        start = null;
    }

    @Override
    public Statistics getStatistics() {
        Statistics st = new Statistics();
        synchronized (rate) {
            st.put("dataRate", new Double(rate.doubleValue()));
        }
        return st;
    }

    /**
     * @return the every
     */
    public Integer getEvery() {
        return every;
    }

    /**
     * @param every
     *            the every to set
     */
    public void setEvery(Integer every) {
        this.every = every;
    }

}