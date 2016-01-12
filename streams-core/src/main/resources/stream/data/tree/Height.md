Height
======

This processor computes the height of trees implementing the `TreeNode` interface.
The height is computed for all attributes of type TreeNode or the specified keys,
if the `keys` parameter has been set.

The height of each tree is stored as attribute `height(...)`. For example, in the
following snippet, the Height processor will create a new attribute `height(@sql:tree)`
with the height of the tree stored in `@sql:tree`:

      &lt;Height keys="@sql:tree" /&gt;
