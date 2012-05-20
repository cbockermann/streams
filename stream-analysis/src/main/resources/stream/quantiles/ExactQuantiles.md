ExactQuantiles
==============

This processor maintains exact quantile statistics for a specific
attribute. This is done by remembering all observed values and sorting
these to determine the exact element for a quantile.

The processor implements the [QuantilesService](QuantilesService.html)
service interface.

**Important:** This is *not* an online algorithm as it will keep a copy
of each double value in memory and thus quickly hit memory limits on
large streams.


### Example

An example of computing the exact quantile for attribute `x` is given
by the following XML snippet:

        <stream.quantiles.ExactQuantiles key="x" />
