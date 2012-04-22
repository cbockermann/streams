CountNodes
==========

This processor iterates over all elements of the data items which implement
the `TreeNode` interface, i.e. which represent trees. It will create a new
attribute for each tree that contains the number of nodes of this tree.

The following example shows the *CountNodes* processor:

      &lt;CountNodes keys="@sql:tree" /&gt;

In this example, the *CountNodes* processor will create a new attribute with
the key `nodeCount(@sql:tree)` that will contain the number of nodes in the
tree stored in attribute `@sql:tree`.
