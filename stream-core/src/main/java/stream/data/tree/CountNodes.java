package stream.data.tree;

import java.io.Serializable;


public class CountNodes 
	extends AbstractTreeFeature 
{
    
    public CountNodes(){
        this.id = "nodeCount";
    }

	@Override
	public Serializable compute(TreeNode tree) {
		return getNumberOfNodes( tree );
	}
	
	
	public Integer getNumberOfNodes( TreeNode tree ){
		if( tree.isLeaf() )
			return 1;
		
		Integer sum = 1;
		for( TreeNode ch : tree.children() )
			sum += getNumberOfNodes( ch );
				
		return sum;
	}
}