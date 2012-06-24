package stream.node.shell.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WebShell implements EntryPoint {

	final WebShellServiceAsync shell = GWT.create(WebShellService.class);

	final VerticalPanel panel = new VerticalPanel();
	final TextArea output = new TextArea();
	final TextBox input = new TextBox();
	String prompt = "";
	final List<String> history = new ArrayList<String>();

	public WebShell() {
		output.addStyleName("shell-output");
		output.setReadOnly(true);
		output.setWidth("99%");
		output.setVisibleLines(30);

		input.addStyleName("shell-input");
		input.setWidth("99%");

		input.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent arg0) {
				if (arg0.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					String cmd = input.getText();
					execute(cmd);
					return;
				}

				if (arg0.getNativeKeyCode() == KeyCodes.KEY_UP) {

				}
			}
		});

		panel.setWidth("90%");
		panel.setHeight("90%");
		panel.addStyleName("shell-panel");

		panel.add(output);
		panel.add(input);
	}

	public void execute(final String command) {

		if (command != null && command.trim().equals("clear")) {
			output.setText("");
			input.setEnabled(true);
			input.setText("");
			return;
		}

		input.setEnabled(false);
		write("Executing: " + command);
		shell.execute(command, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable arg0) {
				error(arg0);
				input.setEnabled(true);
				input.setText(prompt);
			}

			@Override
			public void onSuccess(String arg0) {
				write(arg0);
				input.setEnabled(true);
				input.setText(prompt);
			}
		});
	}

	public void write(String msg) {
		output.setText(output.getText() + "\n" + msg + "\n");
		output.setCursorPos(output.getText().length());
	}

	public void error(Throwable e) {
		StringBuffer s = new StringBuffer();
		s.append("Exception: " + e.getMessage() + "\n");
		for (StackTraceElement te : e.getStackTrace()) {
			s.append(te.getClassName() + ":" + te.getMethodName() + ":"
					+ te.getLineNumber() + "\n");
		}
	}

	@Override
	public void onModuleLoad() {
		if (RootPanel.get("WebShell") != null) {
			RootPanel.get("WebShell").add(panel);
		} else {
			// Window.alert("No div with id 'WebShell' found!");
		}
	}
}