package stream.io.active;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.Processor;
import stream.io.DataStream;

/**
 * *
 * <p>
 * A simple active stream implementation.
 * </p>
 * 
 * @author Hendrik Blom
 * 
 */
public class ActiveDataStreamImpl implements ActiveDataStream {

	protected final LinkedBlockingQueue<Data> queue;

	protected DataStream stream;
	protected StreamActivator activator;

	public ActiveDataStreamImpl(DataStream stream) {
		this.stream = stream;
		this.queue = new LinkedBlockingQueue<Data>(100);
	}

	@Override
	public Map<String, Class<?>> getAttributes() {
		return stream.getAttributes();
	}

	@Override
	public Data readNext() throws Exception {
		return readNext(new DataImpl());
	}

	@Override
	public Data readNext(Data datum) throws Exception {
		Data d = queue.poll();
		if (d != null)
			datum.putAll(d);
		return datum;
	}

	@Override
	public void close() {
		stream.close();
		this.activator.setRun(false);
	}

	@Override
	public void addPreprocessor(Processor proc) {
		stream.addPreprocessor(proc);
	}

	@Override
	public void addPreprocessor(int idx, Processor proc) {
		stream.addPreprocessor(idx, proc);
	}

	@Override
	public List<Processor> getPreprocessors() {
		return stream.getPreprocessors();
	}

	@Override
	public void activate() throws Exception {
		this.activator = new StreamActivator();
		new Thread(activator).start();
	}

	private class StreamActivator implements Runnable {
		private boolean run = true;

		public StreamActivator() {
			run = true;
		}

		public void run() {
			try {
				while (run)
					queue.put(stream.readNext());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void setRun(boolean run) {
			this.run = run;
		}

	}
}
