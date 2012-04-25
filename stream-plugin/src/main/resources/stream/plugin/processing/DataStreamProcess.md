DataStreamProcess
=================

The *DataStreamProcess* operator is a simple operator which will
iteratively read data items from a given data stream and apply
all its inner operators to each of the data items.


Conditioned Processing
----------------------

An optional condition can be specified, which is a boolean expression
following the syntax of the [stream-api filter language](http://sfb876.cs.tu-dortmund.de/streams/stream-api/filter-language.html).

A simple example is given below, which will result in processing only
the events, whose attribute `x1` is larger than 5:

       %{data.x1} @gt 5

The same filter can also be specified at `%{data.x1} > 5`.


Result Buffering
----------------

If the inner operators produce resulting data items, these can be
buffered as output and will be provided as a new stream to other
*DataStreamProcessor*s connected to the output port.

As buffering is currently performed in main memory, it is recommended
to provide a buffer size to not exceed any memory limits.

