/**
 * 
 */
package streams.codec;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 *
 */
public class JSON implements Codec<Data> {

    /**
     * @see streams.codec.Codec#decode(byte[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public Data decode(byte[] rawBytes) throws Exception {

        final JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        Map<String, Object> map = (Map<String, Object>) parser.parse(rawBytes, Map.class);

        Data item = DataFactory.create();
        for (String key : map.keySet()) {
            Object val = map.get(key);
            if (val instanceof Serializable) {
                item.put(key, (Serializable) val);
            }
        }

        return item;
    }

    /**
     * @see streams.codec.Codec#encode(java.lang.Object)
     */
    @Override
    public byte[] encode(Data object) throws Exception {
        String json = JSONObject.toJSONString(object);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream p = new PrintStream(baos);
        p.print(json);
        p.close();
        return baos.toByteArray();
    }
}