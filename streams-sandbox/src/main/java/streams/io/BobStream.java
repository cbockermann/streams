/**
 * 
 */
package streams.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractStream;
import stream.io.SourceURL;
import streams.codec.Codec;
import streams.codec.DefaultCodec;

/**
 * @author chris
 *
 */
public class BobStream extends AbstractStream {

    static Logger log = LoggerFactory.getLogger(BobStream.class);

    DataInputStream in;
    long count = 0L;
    final Object lock = new Object();

    long bytesRead = 0L;
    long firstItem = 0L;
    long lastItem = 0L;

    boolean parse = false;
    boolean gunzip = false;
    Codec<Data> codec = new DefaultCodec<Data>();

    public BobStream(SourceURL url) {
        super(url);
    }

    /**
     * @see stream.io.AbstractStream#init()
     */
    @Override
    public void init() throws Exception {
        super.init();

        this.in = new DataInputStream(getInputStream());
    }

    /**
     * @see stream.io.AbstractStream#readNext()
     */
    @Override
    public Data readNext() throws Exception {

        try {
            synchronized (lock) {
                byte[] block = BobCodec.readBlock(in);
                if (block == null) {
                    log.debug("Failed to read more blocks from file! End-Of-File?");
                    return null;
                }

                if (count == 0) {
                    firstItem = System.currentTimeMillis();
                }

                bytesRead += BobCodec.MAGIC_CODE.length;
                bytesRead += 4;
                bytesRead += block.length;

                lastItem = System.currentTimeMillis();

                if (block.length == 0) {
                    Double seconds = (lastItem - firstItem) / 1000.0;
                    Double gbit = bytesRead * 8 / 1000.0 / 1000.0 / 1000.0;
                    DecimalFormat fmt = new DecimalFormat("0.00");
                    log.debug("{} blocks read, {} blocks/sec => " + fmt.format(gbit / seconds) + " GBit/s", count,
                            fmt.format(count / seconds));
                    return null;
                }

                if (gunzip) {
                    block = gunzip(block);
                }

                Data item = null;
                if (parse) {
                    item = codec.decode(block);
                } else {
                    item = DataFactory.create();
                    item.put("data", block);
                }
                count++;
                return item;
            }
        } catch (EOFException eof) {
            log.debug("End of file reached.");
            return null;
        } catch (Exception e) {
            log.error("Failed to read event #{}:  {}", count, e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @return the parse
     */
    public boolean isParse() {
        return parse;
    }

    /**
     * @param parse
     *            the parse to set
     */
    public void setParse(boolean parse) {
        this.parse = parse;
    }

    /**
     * @return the codec
     */
    public Codec<Data> getCodec() {
        return codec;
    }

    /**
     * @param codec
     *            the codec to set
     */
    public void setCodec(String codec) {
        try {
            @SuppressWarnings("unchecked")
            Class<Codec<Data>> clazz = (Class<Codec<Data>>) Class.forName(codec);
            this.codec = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @return the gunzip
     */
    public boolean isGunzip() {
        return gunzip;
    }

    /**
     * @param gunzip
     *            the gunzip to set
     */
    public void setGunzip(boolean gunzip) {
        this.gunzip = gunzip;
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

    public static void main(String[] args) throws Exception {

        SourceURL source = new SourceURL(args[0]);
        BobStream stream = new BobStream(source);
        stream.init();

        long start = System.currentTimeMillis();
        long bytesRead = 0L;
        Data item = stream.read();
        while (item != null) {
            byte[] block = (byte[]) item.get("data");
            bytesRead += (12 + block.length);
            item = stream.read();
        }
        long end = System.currentTimeMillis();
        Double secs = (end - start * 1.0d) / 1000.0d;
        Double dataRate = (bytesRead / 1024 / 1024) / secs;

        stream.close();
        DecimalFormat fmt = new DecimalFormat("0.0");
        log.info("Read {} bytes, ({} MB/sec)", bytesRead, fmt.format(dataRate));
    }
}