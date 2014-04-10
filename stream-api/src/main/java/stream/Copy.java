package stream;

/**
 * @author Hendrik Blom
 *
 */
public class Copy {
	private String id;
	private String[] subids;

	public Copy(){
		id="";
		subids = new String[0];
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getSubids() {
		return subids;
	}

	public void setSubids(String[] subids) {
		this.subids = subids;
	}

	@Override
	public String toString() {
		return "Copy [id=" + id + "]";
	}

}