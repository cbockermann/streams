/**
 * 
 */
package streams.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;
import streams.codec.Codec;
import streams.codec.DefaultCodec;

/**
 * This processor extracts a serialized data item from a raw-byte attribute of
 * the processed item. The attributes of the resulting item are merged into the
 * processed item.
 * 
 * This allows for raw, low-level byte events to be passed around and distribute
 * the deserialization to separate threads.
 * 
 * @author Christian Bockermann
 *
 */
public class ParseBob implements Processor {

    static Logger log = LoggerFactory.getLogger(ParseBob.class);

    boolean gzip = false;
    String key = "data";
    Codec<Data> serializer = new DefaultCodec<Data>();

    /**
     * @see stream.Processor#process(stream.Data)
     */
    public Data process(Data input) {

        byte[] bytes = (byte[]) input.get(key);
        if (bytes != null) {
            try {
                if (gzip) {
                    bytes = gunzip(bytes);
                }

                Data item = serializer.decode(bytes);
                input.putAll(item);
            } catch (Exception e) {
                log.error("Failed to de-serialize item: {}", e.getMessage());
                if (log.isDebugEnabled()) {
                    e.printStackTrace();
                }
            }
        }

        return input;
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
     * @return the serializer
     */
    public String getSerializer() {
        return serializer.getClass().getName();
    }

    /**
     * @param serializer
     *            the serializer to set
     */
    @SuppressWarnings("unchecked")
    public void setSerializer(String serializer) {
        try {
            Class<?> clazz = Class.forName(serializer);
            this.serializer = (Codec<Data>) clazz.newInstance();
            log.info("Using serializer: {}", serializer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the gzip
     */
    public boolean isGzip() {
        return gzip;
    }

    /**
     * @param gzip
     *            the gzip to set
     */
    public void setGzip(boolean gzip) {
        this.gzip = gzip;
    }

    public static byte[] gunzip(byte[] buf) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(buf));
        byte[] tmp = new byte[4 * 1024];
        int read = in.read(tmp);
        while (read > 0) {
            baos.write(tmp, 0, read);
            read = in.read(tmp);
        }
        in.close();
        baos.close();
        return baos.toByteArray();
    }
}