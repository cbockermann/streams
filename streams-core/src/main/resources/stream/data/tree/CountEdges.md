CountEdges
==========

This processor counts edges of a tree element, represented by a class implementing
the *TreeNode* interface. The number of edges for a tree is stored as new attribute
`edgeCount(...)` for each tree found in the item, or the trees specified in the
`keys` parameter of the processor.

For example, the following definition will provide a processor that adds the attribute
`edgeCount(@sql:tree)` to items. This attribute will contain the number of edges of
the tree found in the `@sql:tree` attribute:

      &lt;CountEdges keys="@sql:tree" /&gt;
