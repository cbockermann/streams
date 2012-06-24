/**
 * 
 */
package stream.node.shell.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author chris
 * 
 */
public class XMLEditor extends VerticalPanel {

	final ContainerEditorServiceAsync editorService = GWT
			.create(ContainerEditorService.class);

	final FlowPanel buttons = new FlowPanel();
	final TextArea area = new TextArea();
	final ListBox select = new ListBox();

	public XMLEditor() {
		addStyleName("xml-editor");

		buttons.setStylePrimaryName("xml-editor-buttons");
		add(buttons);

		editorService.listFiles(new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<String> result) {
				select.clear();
				select.addItem("");
				for (String item : result)
					select.addItem(item);
			}
		});

		select.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int idx = select.getSelectedIndex();
				if (idx >= 1) {
					String fileName = select.getValue(idx);
					load(fileName);
				}
			}
		});
		buttons.add(select);

		Button upload = new Button("Load");
		upload.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

			}
		});
		buttons.add(upload);

		Button save = new Button("Save");
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				save();
			}
		});
		buttons.add(save);

		Button start = new Button("Start");
		start.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int idx = select.getSelectedIndex();
				if (idx >= 0) {

					final String fileName = select.getValue(idx);

					editorService.writeFile(fileName, area.getText(),
							new AsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Failed while saving XML definition: "
											+ caught.getMessage());
								}

								@Override
								public void onSuccess(Boolean result) {

									editorService.start(fileName,
											new AsyncCallback<String>() {

												@Override
												public void onFailure(
														Throwable caught) {
													Window.alert(caught
															.getMessage());
												}

												@Override
												public void onSuccess(
														String result) {
													Window.alert("Container start: "
															+ result);
												}
											});

								}

							});
				}
			}
		});
		buttons.add(start);

		area.setStylePrimaryName("xml-editor-area");
		area.addStyleName("xml-area");
		area.setCharacterWidth(120);
		area.setVisibleLines(30);
		add(area);
	}

	public void load(String str) {
		this.editorService.readFile(str, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				area.setText(result);
			}
		});
	}

	public void save() {
		int idx = select.getSelectedIndex();
		if (idx > 0) {
			final String name = select.getValue(idx);
			editorService.writeFile(name, area.getText(),
					new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
						}

						@Override
						public void onSuccess(Boolean result) {
							Window.alert("XML stored in file " + name);
						}
					});
		}
	}
}