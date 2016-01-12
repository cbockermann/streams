AddStatistics
=============

This processor uses a StatisticsService to query current statistics and
add these to the data item that is being processed. Such statistics may
for example be provided by [MemoryUsage](../monitor/MemoryUsage.html).

### Example

The following example defines a simple monitor thread that will be run
every 5 seconds. Each time it is executed, the `MemoryUsage` processor
will estimate the current JVM memory and store it in key `@memory:jvm`.

The `AddStatistics` processor will then add the statistics elements provided
by the `MemoryUsage` service to the current item. Finally the Plotter will
plot the attribute `@memory:jvm`.


     <Monitor interval="5seconds">

        <MemoryUsage id="mem:usage" />

        <stream.statistics.AddStatistics service="mem:usage" />

        <stream.plotter.Plotter keys="@memory:jvm" />
     </Monitor>
