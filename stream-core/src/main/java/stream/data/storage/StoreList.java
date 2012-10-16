package stream.data.storage;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Hendrik Blom
 * 
 * @param <T>
 */
public abstract class StoreList<T extends Serializable> extends
		Store<ArrayList<T>> {

	public StoreList() {
		super();
	}
}