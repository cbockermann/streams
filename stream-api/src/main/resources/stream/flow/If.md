If
==

This processor provides conditioned execution of nested processors.
By specifying a condition, all nested processors are only executed
if that condition is fulfilled.

As an example, the following will only print data items if the
attribute `x` is larger than `3.1415`:

      <If condition="%{data.x} @gt 3.1415">
         <PrintData />
      </If>
