SelectKeys
==========

This processor can be used to select a subset of keys from the current
data item. The result of this will be a data item that has all keys
removed except for those that have been selected by the `keys` parameter.

The `keys` parameter also allows for specifying keys with wildcards `*`
and `?`. In the following example all attributes that start with `user:`
will be selected, but not `user:gender`:

     <SelectKeys keys="user:*,!user:gender" />
