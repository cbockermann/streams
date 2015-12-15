/**
 * 
 */
package example;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;

/**
 * @author chris
 *
 */
public class CreateImage implements Processor {

    static Logger log = LoggerFactory.getLogger(CreateImage.class);

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {

        Integer samples = (int) input.get("data:samples");
        double sqrt = Math.sqrt(samples.doubleValue());

        if ((sqrt - Math.round(sqrt)) > 0) {
            log.error("Non-squared sample size!");
            return input;
        } else {
            double[] data = (double[]) input.get("data:normalized");
            Long sq = Math.round(sqrt);

            BufferedImage bi = new BufferedImage(sq.intValue(), sq.intValue(), BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < data.length; i++) {

                int y = i / sq.intValue();
                int x = i % sq.intValue();

                Double val = data[i] * 255.0;
                int rgb = (val.intValue() & 0xff);
                // rgb = (rgb << 16) | (rgb << 8) | rgb;

                bi.setRGB(x, y, rgb);
            }

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bi, "PNG", baos);

                baos.flush();
                baos.close();

                input.put("image:png", baos.toByteArray());

            } catch (Exception e) {
                log.error("Failed to write PNG: {}", e.getMessage());
                e.printStackTrace();
            }
        }

        return input;
    }
}
