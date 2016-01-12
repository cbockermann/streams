Count
=====

This processor is a simple aggregation of counts. It provides a
`groupBy` parameter, that specifies a dynamic expression being
resolved for each data item. The value to which this expression
is resolved, specifies the element that should be counted.

This allows for a very generic counting. As an example, the
following snippet will create a count processor counting the
combinations of `REMOTE_ADDRESS` and `REMOTE_USER` values
by a 1-minute aggregation:

       <Count groupBy="%{data.REMOTE_ADDRESS},%{data.REMOTE_USER}"
              window="1 minute" />

At the beginning of each new time interval as specified by the
`window` parameter, the current aggregation is added to the
data item and a new aggregation is started.

The aggregated counts added to the data item upon window completion
are prefixed by `count:`. This can be changed using the `prefix`
parameter of the processor.
