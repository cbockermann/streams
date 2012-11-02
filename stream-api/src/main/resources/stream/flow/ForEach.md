ForEach
=======

This class implements a processor list. It can be used if the current
data item provides an attribute that holds a collection (list, set, array)
of data items, which need to be processed.

The `ForEach` class extracts the nested collection of data items and
applies each of the inner processors to each data item found in the
collection. The `key` parameter needs to be specified to define the
attribute which holds the collection of items.

If no key is specified or the data item itself does not provide a
collection of items in this key, then this processor will simply return 
the current data item.


