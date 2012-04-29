SvmLightStream
==============

This stream implementation provides a data stream for the SVMlight format.
The SVMlight format is a simple `key:value` format for compact storage of
high dimensional sparse labeled data.

It is a line oriented format. The keys are usually indexes, but this stream
implementation also supports string keys.

The following snippet shows some line of an SVMlight formatted file:

    -1.0 4:3.3 10:0.342 44:9.834 # some comment

In the example line above, the first column `-1.0` is the label attribute,
followed by `key:value` pairs. The `#` character starts a comment that
can be provided to each line.
