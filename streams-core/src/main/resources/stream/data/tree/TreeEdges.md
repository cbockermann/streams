TreeEdges
=========

This processor acts upon all trees found in the item or the specified tree
attributes if the parameter `keys` has been specified.

The processor will add a new attribute `key:edge[A->B]` for each edge found
in the tree. This attribute will contain the number of times this edge is
found in the tree.

For example, the following definition

       &lt;TreeEdges keys="@sql:tree" /&gt;

will add attributes in the following scheme:

       sql:tree[ROOT->Select] = 1.0
       sql:tree[Select->ResultList] = 1.0
       sql:tree[Select->FromList] = 1.0
       sql:tree[Select->WhereClause] = 1.0

Please note, that a possible leading `@` character is removed from the key
before creating the new attribute keys. Usually the trees are regarded as
special attributes whereas the tree edge attributes are plain features in
the machine learning sense and not special anymore.
