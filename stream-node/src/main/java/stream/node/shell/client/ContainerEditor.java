package stream.node.shell.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ContainerEditor implements EntryPoint {

	int last = 0;
	
	final VerticalPanel content = new VerticalPanel();
	final FlowPanel buttons = new FlowPanel();
	final TabPanel tabs = new TabPanel();
	final ContainerEditPanel editor = new ContainerEditPanel();
	final ContainerViewPanel view = new ContainerViewPanel();
	
	public ContainerEditor(){
		content.addStyleName( "container-editor" );
		
		content.setPixelSize( 1024, 800 );
		
		content.add( buttons );
		Button add = new Button( "Add" );
		add.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent arg0) {
				view.add( new Element( "Element-" + (last++) ) );
				editor.setText( editor.getText() + "\nAdded new Element" );
			}
		});
		buttons.add( add );
		
		editor.addStyleName( "container-editor-textarea" );
		tabs.add( editor, "Editor" );
		tabs.add( view, "View" );
		
		//tabs.setWidth( "100%" );
		//tabs.setHeight( "100%" );
		tabs.setPixelSize( 1016, 780);
		content.add( tabs );
	}
	
	@Override
	public void onModuleLoad() {
		if( RootPanel.get( "ContainerEditor" )  != null ){
			RootPanel.get( "ContainerEditor" ).add( content );
		}
	}
}
