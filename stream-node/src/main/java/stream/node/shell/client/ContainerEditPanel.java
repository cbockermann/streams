package stream.node.shell.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ContainerEditPanel extends VerticalPanel {

	final FlowPanel buttons = new FlowPanel();
	final TextArea area = new TextArea();
	
	public ContainerEditPanel(){
		add( buttons );
		buttons.addStyleName( "container-editor-buttons" );
		add( area );
		area.addStyleName( "container-editor-textarea" );
		
		setWidth( "100%" );
		setHeight( "100%" );
	}
	
	public String getText(){
		return area.getText();
	}
	
	public void setText( String str ){
		area.setText( str );
	}
}