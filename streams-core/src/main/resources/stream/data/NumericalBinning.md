NumericalBinning
================

This processor transforms a numeric attribute into a discrete nominal attribute
by creating bins and mapping all values onto a bin. This can be regarded as a
simple interval-based discretization.

The *NumericalBinning* processor requires a `minimum`, `maximum` and `bins`
parameter to be set. It will then compute equi-distant interval from these values
and will replace each value by a string denoting the interval/bin it was mapped to.

By default, the *NumericalBinning* will discretize all numeric attributes, i.e.
all keys that refer to an Integer, Long, Float or Double value. If the `keys`
parameter is provided, only the attributes listed in `keys` are discretized.

The following example shows the numerical binning for attribute `x1`:

        &lt;NumericalBinning keys="x1" minimum="0.0" maximum="10.0" bins="10" /&gt;
