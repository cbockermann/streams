/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.annotations.EmbeddedContent;
import stream.annotations.Parameter;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public abstract class Script extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(Script.class);

	final static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

	protected EmbeddedContent embedded = null;

	protected ScriptEngine scriptEngine;

	protected File file;
	transient protected String theScript = null;

	public Script(ScriptEngine engine) {
		if (engine == null)
			throw new RuntimeException("No ScriptEngine found!");
		this.scriptEngine = engine;
	}

	protected Script() {
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
	@Parameter(required = false)
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the embedded
	 */
	public EmbeddedContent getScript() {
		return embedded;
	}

	/**
	 * @param embedded
	 *            the embedded to set
	 */
	@Parameter(required = false)
	public void setScript(EmbeddedContent embedded) {
		this.embedded = embedded;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		try {
			String script = loadScript();
			log.info("Script loaded is:\n{}", script);

			ScriptContext ctx = scriptEngine.getContext();

			// log.info( "Binding data-item to 'data'" );
			ctx.setAttribute("data", data, ScriptContext.ENGINE_SCOPE);

			scriptEngine.put("data", data);

			log.info("Evaluating script...");
			scriptEngine.eval(script, ctx);

		} catch (Exception e) {
			log.error("Failed to execute script: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();

			throw new RuntimeException("Script execution error: "
					+ e.getMessage());
		}

		log.info("Returning data: {}", data);
		return data;
	}

	protected String loadScript() throws Exception {

		if (theScript == null) {

			if (embedded != null) {
				log.info("Using embedded content...");
				theScript = embedded.getContent();
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
