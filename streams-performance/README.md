# Streams Performance

Streams-performance package allows measuring performance of a list of processors (e.g. bundled as process) even if they are distributed over severyl physical machines by Apache Storm, Spark, Flink or other frameworks.

In order to use this processor you will need to package it with a profile ``standalone`` and then start the receiver as followed:

```
mvn -P standalone package
java -cp target/streams-performance.jar streams.net.PerformanceReceiver 
```

The receiver is running on the machine on port 6001.
If you want to define your own port, just add that certain number as an argument while starting Performance Receiver.
In your configuration XML you can now add the lines:

```
<streams.performance.Performance host="127.0.0.1">
	...
</streams.performance.Performance>
```
