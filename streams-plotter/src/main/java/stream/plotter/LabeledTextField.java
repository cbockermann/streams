/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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
package stream.plotter;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Hendrik Blom
 * 
 */
public class LabeledTextField extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel label;
	private JTextField textField;
	private String lastValue;

	public LabeledTextField(String label, String defaultValue, boolean editable) {
		this.setLayout(new FlowLayout());
		this.label = new JLabel(label);
		this.textField = new JTextField(defaultValue);
		this.add(this.label);
		this.add(this.textField);
		this.lastValue = defaultValue;

	}

	public JLabel getLabel() {
		return label;
	}

	public void setLabel(JLabel label) {
		this.label = label;
	}

	public JTextField getTextField() {
		return textField;
	}

	public void setTextField(JTextField textField) {
		this.textField = textField;
	}

	public LabeledTextField(String label) {
		this(label, "", true);
	}

	public LabeledTextField(String label, String defaultValue) {
		this(label, defaultValue, true);
	}

	public String getLabelText() {
		return label.getText();
	}

	public void setText(String text) {
		textField.setText(text);
	}

	public void updateText(String text) {
		if (lastValue != text)
			setText(text);
	}
}
