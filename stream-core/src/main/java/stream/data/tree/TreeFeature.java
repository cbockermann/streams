package stream.data.tree;

import java.io.Serializable;


public interface TreeFeature {

	public String createFeatureKey( String inputKey );
	
	public Serializable compute( TreeNode tree );
}
