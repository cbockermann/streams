/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
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

import stream.ProcessContext;
import stream.annotations.Description;
import stream.data.Data;
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
	 * @see stream.AbstractProcessor#init(stream.Context)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
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