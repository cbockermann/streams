/**
 * 
 */
package example;

import stream.Data;
import stream.Processor;

/**
 * @author Christian Bockermann
 *
 */
public class Normalization implements Processor {

    final String dataKey = "data:raw";

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {

        double[] data = (double[]) input.get(dataKey);
        double max = data[0];
        double min = data[0];

        for (int i = 0; i < data.length; i++) {
            max = Math.max(max, data[i]);
            min = Math.min(min, data[i]);
        }

        input.put("data:min", min);
        input.put("data:max", max);

        double offset = min;
        double scale = Math.abs(min - max);

        double[] norm = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            norm[i] = (data[i] - offset) / scale;
        }

        input.put("data:normalized", norm);
        return input;
    }
}