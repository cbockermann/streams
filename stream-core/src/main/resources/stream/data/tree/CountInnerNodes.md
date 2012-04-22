CountInnerNodes
===============

This processor extracts the number of nodes of trees as new feature for the
input data item. For each processed item, it will either compute the number
of inner nodes of all values implementing the *TreeNode* interface or the
specified attributes if the `keys` parameter has been provided.

For each processed tree, a new attribute `innerNodeCount(...)` will be added
to the item. For example, in the following sample, the attribute `innerNodeCount(@sql:tree)`
will be added:

      &lt;CountInnerNodes keys="@sql:tree" /&gt;
