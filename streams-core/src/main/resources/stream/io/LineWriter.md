LineWriter
==========

This processor simply writes out items to a file in text format. The format
of the file is by default a single line for each item. Any occurrences of
new lines in the values of each item are escaped by backslash escaping.

The following example will create a single line for each item starting with
some constant string, followed by the value of the items `@id` attribute,
a constant string ` -> ` and the items `name` attribute:

      <LineWriter format="UserId: %{data.@id} -> %{data.name}"
                  file="/tmp/example.out.txt"/>
