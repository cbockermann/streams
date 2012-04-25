Sum
===

This simple processor computes a continuous sum over all numberic
keys of all items processed. The result of this processor is to
replace the values in the processed items by the current some of
each key.

If the `keys` parameter is provided, then only the sum of the specified
keys is computed (for those that are numeric, i.e. of type Integer,
Float, Double or Long).

For example the following setup will first update the sum for `error`
and `memorySize` and will then replace the values for these keys with
the overall sums:

     <Sum keys="error,memorySize" />

After the processor, the item contains as `error` the sum of all
`error` values seen so far (the same holds for `memorySize`).


Anytime Statistics Service
--------------------------

The processor can be registered as service and then provides an
anytime serice of type `StatisticsService`. This service implementation
can then be queried for the sum value of all observed keys/attributes.

For more details, see the (StatisticsService)[StatisticsService.html]
documentation.
