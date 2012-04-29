CsvWriter
=========

This processor appends all processed data items to a file in CSV format. The
processor either adds all keys of the items or only a set of previous defined
keys.

As first line, the writer emits a header line with a comma separated list of
column names. This line is prepended with a `#` character.

The processor supports the creation of files with varying numbers of keys/attributes.
If an item is processed with a different (larger) number of keys and the set of
keys has not been defined in the `keys` parameter, a new header will be inserted
into the file, signaling the header for the next items to be written.

By default the writer uses `,` as separator, which can be changed by the `separator`
parameter.

The following example shows a *CsvWriter* writing to `/tmp/test.csv` using the `;`
as separator. Only keys `@id` and `name` will be written:

      <CsvWriter url="file:/tmp/test.csv" keys="@id,name"
                 separator=";" />
