/**
 * 
 */
package streams.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.util.ByteSize;
import streams.codec.Codec;
import streams.codec.DefaultCodec;

/**
 * @author chris
 *
 */
public class BobWriter extends AbstractProcessor {

    static Logger log = LoggerFactory.getLogger(BobWriter.class);

    File file;
    ByteSize blockSize = null;

    Codec<Data> serializer;

    FileOutputStream fos;
    DataOutputStream dos;

    long objects = 0L;
    long bytes = 0l;

    /**
     * @see stream.AbstractProcessor#init(stream.ProcessContext)
     */
    @Override
    public void init(ProcessContext ctx) throws Exception {
        super.init(ctx);
        log.info("Initializing BobWriter...");
        this.fos = new FileOutputStream(file);
        this.dos = new DataOutputStream(fos);

        if (serializer == null) {
            log.info("Using default JavaSerializer...");
            serializer = new DefaultCodec<Data>();
        }
    }

    /**
     * @see stream.Processor#process(stream.Data)
     */
    public Data process(Data input) {
        try {
            byte[] bytes = serializer.encode(input);

            int bytesWritten = BobCodec.writeBlock(bytes, dos);
            log.debug("Wrote {} bytes for item", bytesWritten);

            this.objects++;
            this.bytes += bytesWritten;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return input;
    }

    /**
     * @see stream.AbstractProcessor#finish()
     */
    @Override
    public void finish() throws Exception {
        log.info("Closing BobWriter ({} objects written to {} bytes)...", objects, bytes);
        super.finish();
        dos.close();
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file
     *            the file to set
     */
    public void setFile(File file) {
        this.file = file;
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}