CountLeaves
===========

Similar to the (CountNodes)[CountNodes.html] processor, this processor computes
the number of leaves of all trees found in the data item. For each tree, it
creates a new attribute with key `leafCount(..)` that contains the number of
leaves of the tree.

In the following example, the processor will create the attribute `leafCount(@sql:tree)`
for the tree stored in the `@sql:tree` attribute:

      &lt;CountLeaves keys="@sql:tree" /&gt;
