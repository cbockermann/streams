/**
 * 
 */
package example;

import java.io.FileOutputStream;

import stream.Data;
import stream.Processor;

/**
 * @author chris
 *
 */
public class WriteImage implements Processor {

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {

        byte[] pngData = (byte[]) input.get("image:png");
        if (pngData != null) {

            try {
                FileOutputStream fos = new FileOutputStream("/tmp/test.png");
                fos.write(pngData);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return input;
    }
}
