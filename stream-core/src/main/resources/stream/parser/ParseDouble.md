ParseDouble
===========

This simple processor parses all specified keys into double values. If a key
cannot be parsed to a double it will be replaced by *Double.NaN*.

The processor will be applied for all keys of an item unless the `keys` parameter
is used to specify the keys/attributes that should be transformed into double
values.

The following example shows a *ParseDouble* processor that converts the attributes
`x1` and `x2` into double values:

      &lt;ParseDouble keys="x1,x2" /&gt;

### Different Default Value

The `default` parameter allows for specifying a different value than the default
*Double.NaN* value. The following example converts all values to their double
representation and defaults to 0.0 if parsing as double fails:

      &lt;ParseDouble default="0.0" /&gt;
