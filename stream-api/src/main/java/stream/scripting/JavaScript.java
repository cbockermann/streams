/**
 * 
 */
package stream.scripting;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.runtime.Context;
import stream.runtime.annotations.Description;
import stream.util.URLUtilities;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Processing.Script")
public class JavaScript extends Script {
	static Logger log = LoggerFactory.getLogger(JavaScript.class);

	final static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	final static String preamble = URLUtilities
			.readContentOrEmpty(JavaScript.class
					.getResource("/stream/data/JavaScript.preamble"));

	transient String theScript = null;
	String script = null;

	/**
	 * @param engine
	 */
	public JavaScript() {
		super(scriptEngineManager.getEngineByName("JavaScript"));
	}

	/**
	 * @see stream.AbstractDataProcessor#init(stream.runtime.Context)
	 */
	@Override
	public void init(Context ctx) throws Exception {
		super.init(ctx);
		this.script = loadScript();
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		try {
			if (script == null) {
				log.debug("No script loaded, skipping script execution...");
				return data;
			}

			log.debug("Script loaded is:\n{}", script);

			ScriptContext ctx = scriptEngine.getContext();
			scriptEngine.put("data", data);

			log.debug("Evaluating script...");
			scriptEngine.eval(script, ctx);

		} catch (Exception e) {
			log.error("Failed to execute script: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();

			throw new RuntimeException("Script execution error: "
					+ e.getMessage());
		}

		log.debug("Returning data: {}", data);
		return data;
	}

	protected String loadScript() throws Exception {

		if (theScript == null) {

			if (embedded != null) {
				log.info("Using embedded content...");
				theScript = preamble + "\n" + embedded.getContent();
				return theScript;
			}

			if (file != null) {
				log.debug("Reading script from file {}", file);
				theScript = loadScript(new FileInputStream(file));
				return theScript;
			}
		}

		return theScript;
	}

	protected String loadScript(InputStream in) throws Exception {
		log.debug("Loading script from input-stream {}", in);
		StringBuffer s = new StringBuffer();
		s.append(preamble);
		s.append("\n");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		while (line != null) {
			s.append(line + "\n");
			log.debug("Appending line: {}", line);
			line = reader.readLine();
		}
		reader.close();
		return s.toString();
	}
}