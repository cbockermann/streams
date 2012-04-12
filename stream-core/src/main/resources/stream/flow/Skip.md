Skip
====

This processor will simply skip all events matching a
given condition. If no condition is specified, the processor
will skip all events.

The condition must be a bool expression created from numerical
operators like `@eq`, `@gt`, `@ge`, `@lt` or `@le`. In addition
to those numerical tests the `@rx` operator followed by a regular
expression can be used.

The general syntax is

       variable  operator  argument

For example, the following expression will check the value of
attribute `x1` against the 0.5 threshold:

       x1 @gt 0.5

 
