Package stream.parser
=====================

When processing streams of data each single data item may contain additional
information that needs to be extracted into more detailed attributes or into
other value types.

The `stream.parser` package provides a set of parsing processors, that usually
act upon on or more keys and extract information from the attributes denoted
by those keys.

For example the `ParseDouble` processor will parse double values from all strings
that are denoted in its `keys` parameter. Other parsers in this package are for
example the `ParseJSON`, `Timestamp` or the `NGrams` processor.
