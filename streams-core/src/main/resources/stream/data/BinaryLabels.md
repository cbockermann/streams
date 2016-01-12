BinaryLabels
============

This processor transforms items into binary labeled items with labels
with values *-1.0* or *+1.0*. The processor provides several parameters
that can be used for adjusting the strategy on how to map labels.

By default the label attribute is expected to be in key `@label`. This
can be changed to the correct key name using the `label` parameter.

### Mapping String labels

In the case of labels of string type, the processor needs to be provided
with the string value of the positive class using the `positive` parameter.
Items with the label key set to the specified value for `positive` will
be flagged with a label *+1.0*, all other will be marked as *-1.0*.

The following example sets up a *BinaryLabels* processor mapping all items
with a label `@label` and value `SPAM` to *+1.0*. Items with any other
label value are marked as *-1.0*:

      &lt;BinaryLabels label="@label" positive="SPAM" /&gt;

If no value for `positive` is specified, the first value for that label
key is regarded as the positive class value.


### Mapping Numberic Labels

If the labels are of numberic type, the mapping can be done with a simple
threshold comparison. For this, the processor provides a `threshold` parameter.

In the following example, items with a `@label` value higher than *0.5* are
mapped to class *+1.0*, all others are mapped to class *-1.0*:

      &lt;BinaryLabels label="@label" threshold="0.5" /&gt;

If no threshold is specified, the default threshold of *0.0* is used.
