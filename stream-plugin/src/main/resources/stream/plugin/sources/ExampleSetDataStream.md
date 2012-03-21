ExampleSet DataStream
=====================

This operator simply creates a *data stream* handle from an existing
example set. The example set will most likely remain in main memory
and the resulting *data stream* handle will provide a sequential access
to each of the examples.

The operator is intended to work as adapter for existing datasets to be
processed stream-wise.
