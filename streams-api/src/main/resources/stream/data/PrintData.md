PrintData
=========

This processor will simply print out the processed elements to the
standard output. As the processor is a conditioned processor, it can
also be used to print only specific items, matching a given condition.

The following example shows a processor that will only print items
with no key `x1`:

      <PrintData condition="%{data.x1} == null" />
