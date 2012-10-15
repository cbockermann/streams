Package stream.flow
===================

The `stream.flow` package contains processors that allow for data
flow control within a process setup. Processors in this package are
usually processor-lists, i.e. they may provide nested processors
that are executed based on conditions. 

A typical example for control flow is given with the following `If`
processor, which executes the `PrintData` processor only, if the
value of attribute `x1` is larger than 0.5.

       <If condition="%{data.x1} @gt 0.5">
           <PrintData />
       </If>

Other flow control processors provide control of data queues such
as enqueuing events into other processes' queues.
