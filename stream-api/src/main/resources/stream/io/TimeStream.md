TimeStream
==========

This is a very simple stream that emits a single data item
upon every read. The data item contains a single attribute
`@timestamp` that contains the current timestamp (time in
milliseconds).

The name of the attribute can be changed with the `key` parameter,
e.g. to obtain the timestamp in attribute `@clock`:

      <Stream class="stream.io.TimeStream" key="@clock" />
