package stream.node.shell.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Element implements IsSerializable {

	String name;
	
	public Element(){
		this.name = "";
	}
	
	public Element( String str ){
		this.name = str;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
