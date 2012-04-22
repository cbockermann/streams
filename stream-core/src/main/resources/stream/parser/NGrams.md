NGrams
======

This parser processor will create n-grams from a specified attribute of the processed
item and will add all the n-grams and their frequency to the item. By default the
processor creates n-grams of length 3.

To not overwrite any existing keys, the n-gram frequencies can be prefixed with a
user-defined string using the `prefix` parameter.

The following example shows an *NGram* processor that will create 5-grams of the string
found in key `text` and add their frequency to the items with a prefix of `5gram`:

      &lt;NGrams n="5" key="text" prefix="5gram" /&gt;
