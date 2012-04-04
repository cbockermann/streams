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
