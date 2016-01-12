/**
 * 
 */
package streams.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 *
 */
public class GSON implements Codec<Data> {

    final Gson gson;

    public GSON() {
        gson = new GsonBuilder().create();
    }

    /**
     * @see streams.codec.Codec#decode(byte[])
     */
    @Override
    public Data decode(byte[] rawBytes) throws Exception {
        Reader reader = new InputStreamReader(new ByteArrayInputStream(rawBytes));
        Map<?, ?> map = gson.fromJson(reader, Map.class);
        Data item = DataFactory.create();
        for (Object key : map.keySet()) {
            item.put(key.toString(), (Serializable) map.get(key));
        }
        reader.close();
        return item;
    }

    /**
     * @see streams.codec.Codec#encode(java.lang.Object)
     */
    @Override
    public byte[] encode(Data object) throws Exception {
        String json = gson.toJson(object);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        out.print(json);
        out.flush();
        out.close();
        return baos.toByteArray();
    }
}
