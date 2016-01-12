Average
=======

The *Average* processor computes a continuous average over all numeric
keys provided in the data items processed. If the `keys` parameter is
specified, then only the selected keys are averaged.

Non-numberic keys/attributes will be ignored.

The processor will replace the value of the processed items with the
current average. In the following example only the `error` key is averaged
and the current average is stored in the processed item:

     <Average keys="error" />


Anytime StatisticsService
-------------------------

By providing an additional `id` attribute, this processor will be registered
as anytime *StatisticsService* and can then be queried for statistics (averages)
of all observed keys.

For more details, see the (StatisticsService)[StatisticsService.html]
documentation.
