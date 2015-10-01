/**
 * 
 */
package streams.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import stream.Data;
import stream.data.DataFactory;
import stream.io.Codec;

/**
 * 
 * @author Christian Bockermann
 *
 */
public class KryoCodec implements Codec<Data> {

	final static Map<String, Serializable> template = new LinkedHashMap<String, Serializable>();
	final Kryo kryo;

	public KryoCodec() {
		kryo = new Kryo();
	}

	public Kryo serializer() {
		return kryo;
	}

	/**
	 * @see stream.io.Codec#decode(byte[])
	 */
	@Override
	public Data decode(byte[] rawBytes) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(rawBytes);

		@SuppressWarnings("unchecked")
		Data item = DataFactory.create(kryo.readObject(new Input(bais), template.getClass()));
		bais.close();
		return item;
	}

	/**
	 * @see stream.io.Codec#encode(java.lang.Object)
	 */
	@Override
	public byte[] encode(Data object) throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Output out = new Output(baos);
		kryo.writeObject(out, object);
		out.close();

		return baos.toByteArray();
	}
}