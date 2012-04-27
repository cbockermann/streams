LineWriter
==========

This processor simply writes out items to a file in text format. The format
of the file is by default a single line for each item. Any occurrences of
new lines in the values of each item are replace by '\'.

With the `keys` parameter, one can specify the keys (and their order) that
are to be written into each file explicitly.
