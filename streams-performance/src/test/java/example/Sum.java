/**
 * 
 */
package example;

import stream.Data;
import stream.Processor;

/**
 * @author chris
 *
 */
public class Sum implements Processor {

    String key = "data:normalized";

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {

        double[] data = (double[]) input.get(key);
        double sum = 0.0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        input.put(key + ":sum", sum);
        return input;
    }
}
