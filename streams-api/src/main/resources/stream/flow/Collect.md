Collect
=======

This processor requires a `count` parameter and a `key` do
be specified. The implementation will wait for a number of
`count` data items and collect these in a list. As soon as
`count` items have been collected, a new, empty item will
be created which holds an array of the collected items in
the attribute specified by `key`.

While waiting for `count` items to arrive, the processor
will return `null` for each collected data item, such that
no subsequent processors will be executed in a process.

After emitting the collected data items, the counter is
reset and the processor starts collecting the next `count`
items.
