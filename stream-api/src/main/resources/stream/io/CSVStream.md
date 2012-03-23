CSVStream
=========

This data stream source reads simple comma separated values
from a file/url. Each line is split using a separator (regular
expression).

Lines starting with a hash character (`#`) are regarded to be
headers which define the names of the columns.

The default split expression is `(;|,)`, but this can changed
to whatever is required using the `separator` parameter.
