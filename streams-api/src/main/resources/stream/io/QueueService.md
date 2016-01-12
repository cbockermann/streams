QueueService
============

The *QueueService* is a simple interface for reading (`poll()`) from or
writing (`enqueue(Data)`) to. As services need to fulfil the anytime
property, neither of these methods should be blocking.

This in turn provides no guarantees that these methods work.

In case of the reading with `poll()`, the return value might be `null`,
i.e. if the queue provided by this service is empty.

For enqueuing, the `enqueue(Data)` method returns *true* if the enqueuing
was successful and *false* otherwise.
