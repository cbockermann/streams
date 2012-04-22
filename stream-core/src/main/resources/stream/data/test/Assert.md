Assert
======

This element checks each processed element against a specific condition. If
the condition does not hold for that element, an exception is thrown.

The *Assert* processor is intended to ensure that pre-defined properties hold
for each event that is further processed by the experiment.

The following example fires an exception if an element does exceed the limit
of *1.0* in its `x1` key:

      &lt;Assert condition="%{data.x1} @le 1.0" /&gt;
