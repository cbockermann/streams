/**
 * 
 */
package stream.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.MenuBar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * @author chris
 * 
 */
public class Editor extends JFrame {

	/** The unique class ID */
	private static final long serialVersionUID = 6902268959933789352L;
	JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));

	JLabel pos = new JLabel("Line: , Column:");

	JPanel content = new JPanel();

	JTextArea textArea = new JTextArea();
	final MenuBar menu = new MenuBar();

	public Editor() {

		status.add(pos);

		JButton run = new JButton("Run");
		buttons.add(run);

		this.setMenuBar(menu);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(buttons, BorderLayout.NORTH);
		this.getContentPane().add(content, BorderLayout.CENTER);
		this.getContentPane().add(status, BorderLayout.SOUTH);

		content.setLayout(new BorderLayout());
		content.setBorder(null);

		textArea.setBorder(new EmptyBorder(5, 5, 5, 5));
		content.add(textArea, BorderLayout.CENTER);

		this.setSize(1024, 768);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Editor editor = new Editor();
		editor.setVisible(true);
	}
}
