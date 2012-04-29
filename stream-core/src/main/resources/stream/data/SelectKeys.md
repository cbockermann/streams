SelectKeys
==========

This processor works as an opposite processor to *RemoveKeys*: it allows for
specifying the keys that are to be selected from a processed item. All other
keys will be removed from the item.

As an example, the following definition creates a processor that removes all
attributes but `x1` and `x2`:

      &lt;SelectKeys keys="x1,x2" /&gt;
