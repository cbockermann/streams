AddTimestamp
============

This processor simply adds the current time as a UNIX timestamp to the
current data item. The default attribute/key to add is `@timestamp`.

The value is the number of milliseconds since the epoch date, usually
1.1.1970. Using the `key` parameter, the name of the attribute to add
can be changed:

        &lt;stream.data.AddTimestamp key="@current-time" />
