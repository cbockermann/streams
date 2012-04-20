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
package fact.plugin.operators;

import java.io.File;
import java.net.URL;
import java.util.List;

import stream.plugin.DataStreamPlugin;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeFile;

import fact.data.DrsCalibration;
import fact.io.FactDataStream;
import fact.plugin.FactEventStream;

/**
 * @author chris
 * 
 */
public class FactEventReader extends Operator {

	public final static String FITS_DATA_FILE = "Fits file";
	public final static String FITS_DRS_FILE = "Drs file";
	public final static String KEEP_UNCALIBRATED = "Keep uncalibrated data";

	final OutputPort output = getOutputPorts().createPort(
			DataStreamPlugin.DATA_STREAM_PORT_NAME);

	/**
	 * @param description
	 */
	public FactEventReader(OperatorDescription description) {
		super(description);
		producesOutput(FactEventStream.class);
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		try {
			File fitsFile = getParameterAsFile(FITS_DATA_FILE);
			URL fitsData = fitsFile.toURI().toURL();
			FactDataStream stream = new FactDataStream(fitsData);

			File drsFile = getParameterAsFile(FITS_DRS_FILE);
			if (drsFile != null) {
				DrsCalibration drs = new DrsCalibration();
				drs.setDrsFile(drsFile.getAbsolutePath());
				// stream.setDrsFile( drsFile.getAbsolutePath() );

				Boolean b = getParameterAsBoolean(KEEP_UNCALIBRATED);
				drs.setKeepData(b);
				stream.addPreprocessor(drs);
			}

			FactEventStream feStream = new FactEventStream(stream);
			output.deliver(feStream);

		} catch (Exception e) {
			throw new UserError(this, e, -1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add(new ParameterTypeFile(FITS_DATA_FILE,
				"The fits file to read from", null, false));
		types.add(new ParameterTypeFile(FITS_DRS_FILE,
				"The DRS file for calibration", null, true));
		types.add(new ParameterTypeBoolean(KEEP_UNCALIBRATED,
				"Keep uncalibrated data", false));
		return types;
	}
}