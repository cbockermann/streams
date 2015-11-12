# Streams Performance

Streams-performance package allows measuring performance of a list of processors (e.g. bundled as process) even if they are distributed over severyl physical machines by Apache Storm, Spark, Flink or other frameworks.

In order to use this processor you will need to package it with a profile ``standalone`` and then start the receiver as followed:

```
# mvn -P standalone package
# java -cp target/streams-performance.jar streams.net.PerformanceReceiver 
```

The receiver is running on the machine on port 6001.
In your configuration XML you can now add the lines:

```
<streams.performance.Performance host="127.0.0.1">
	...
</streams.performance.Performance>
```


## Performance Logging Example

A simple example is provided in the `examples/test.xml` file of the *streams-performance* module. This
example processes a synthetic gaussian stream and emits performance statistics to a local performance
receiver every 10 items.

You need to start the `PerformanceReceiver` as mentioned above and then use the same `streams-performance.jar`
to run the example:

       # java -jar target/streams-performance.jar examples/test.xml

