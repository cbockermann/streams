/**
 * 
 */
package example;

import java.util.Random;

import stream.Data;
import stream.ProcessorList;
import stream.data.DataFactory;
import stream.io.AbstractStream;
import stream.runtime.ProcessContextImpl;

/**
 * @author chris
 *
 */
public class DataStream extends AbstractStream {

    Random rnd = new Random(2015L);
    String[] types = "A,B,C,D,E,F,G,H,I,J,K,L,M,N".split(",");

    int samples = 64 * 1024;

    /**
     * @see stream.io.AbstractStream#readNext()
     */
    @Override
    public Data readNext() throws Exception {
        Data item = DataFactory.create();

        double[] data = new double[samples];
        for (int i = 0; i < samples; i++) {
            double x = i;
            data[i] = Math.cos(5 * x) * 2 * rnd.nextDouble() - 1.0;
        }

        item.put("@timestamp", System.currentTimeMillis());
        item.put("data:samples", samples);
        item.put("data:raw", data);

        int idx = rnd.nextInt(types.length);
        item.put("type", types[idx]);

        return item;
    }

    /**
     * @return the samples
     */
    public int getSamples() {
        return samples;
    }

    /**
     * @param samples
     *            the samples to set
     */
    public void setSamples(int samples) {
        this.samples = samples;
    }

    public static void main(String[] args) throws Exception {
        DataStream input = new DataStream();
        input.setLimit(100L);
        input.setSamples(16 * 16 * 16 * 16);
        input.init();

        ProcessorList ps = new ProcessorList();
        ps.add(new Normalization());
        ps.add(new Sum());
        ps.add(new CreateImage());
        ps.add(new WriteImage());

        ps.init(new ProcessContextImpl());

        Data item = input.read();
        while (item != null) {
            item = ps.process(item);
            System.out.println(item);
            item = input.read();
        }

        input.close();
    }
}