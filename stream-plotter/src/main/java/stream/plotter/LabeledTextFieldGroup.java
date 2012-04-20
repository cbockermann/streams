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
package stream.plotter;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import stream.data.Data;
import stream.plotter.utils.Utils;

public class LabeledTextFieldGroup extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HashMap<String, LabeledTextField> components;
	private HashMap<String, LabeledTextFieldGroup> subgroups;
	private JLabel label;

	public LabeledTextFieldGroup() {
		super();
		this.setLayout(new FlowLayout());
		components = new HashMap<String, LabeledTextField>();
		subgroups = new HashMap<String, LabeledTextFieldGroup>();
	}

	public LabeledTextFieldGroup(String title) {
		this();
		this.label = new JLabel(title);
		this.add(label);
	}

	public Component add(String key, LabeledTextField comp) {
		Component result = super.add(comp);
		components.put(key, comp);
		return result;
	}

	public Component add(LabeledTextField comp) {
		return this.add(comp.getLabelText(), comp);
	}

	public Component add(String key, LabeledTextFieldGroup comp) {
		Component result = super.add(comp);
		subgroups.put(key, comp);
		return result;
	}

	public Component add(LabeledTextFieldGroup comp) {
		return this.add(comp.getLabelText(), comp);
	}

	public String getLabelText() {
		return (this.label == null ? "" : getLabelText());
	}

	public void update(Data data) {
		for (LabeledTextFieldGroup subgroup : subgroups.values()) {
			subgroup.update(data);
		}
		for (String key : components.keySet()) {
			String value = String.valueOf(data.get(key));
			if (!value.isEmpty())
				components.get(key).updateText(value);
		}
	}

	public String[] keys() {

		return components.keySet().toArray(
				new String[components.keySet().size()]);
	}

	public String[] allKeys() {
		String[][] keys = new String[subgroups.values().size()][];

		List<LabeledTextFieldGroup> keyList = new LinkedList<LabeledTextFieldGroup>(
				subgroups.values());

		for (int i = 0; i < subgroups.values().size(); i++) {
			keys[i] = keyList.get(i).keys();
		}

		return Utils.concatAll(keys(), keys);
	}
}
