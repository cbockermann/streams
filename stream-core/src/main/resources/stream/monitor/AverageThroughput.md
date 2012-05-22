AverageThroughput
=================

The `AverageThroughput` processor is a processor list that may be wrapped
around a set of inner processors. For each data item the throughput processor
list will determine the time required to process the item by all of its inner
processors.

The processor implements the `StatisticsService` interface and may therefore
be registered and queried for the average throughput (data items per second)
and the average time required for a single item.

### Example

In the following example, several processors are monitored for their throughput.
The results are added to the current data item and are plotted by a plotter.

      <process>
         <AverageThroughput id="throughput">
             <ParseDouble keys="Humidity,Temperature" />
             <NaiveBayes label="play" />
             <SQLWriter table="golf" ... />
         </AverageThroughput>

         <AddStatistics service-ref="throughput" />

         <Plotter keys="@avg:milliseconds-per-item" />
      </process>
