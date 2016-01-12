SelectKeys
==========

This processor is a process-list. It creates a copy of the current data item
with all attributes matching the list of specified keys. Then all nested
processors are applied to that copy and the copy is merged back into the
original data item.

If any of the nested data items returns *null*, this processor will also
return *null*.

The `keys` parameter of this processor allows for specifying a comma separated
list of keys and key-patterns using simple wildcards `\*` and `?`.


As an example, the following definition prints data items containing only the
attribute `x1`, `x2` and any attributes that start with `user:`:

      &lt;SelectKeys keys="x1,x2,user:*"&gt; 
          &lt;PrintData /&gt;
      &lt;/SelectKeys&gt;
