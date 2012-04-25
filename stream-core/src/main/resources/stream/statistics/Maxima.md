Maxima
======

This processor computes the continuous maximum values for all numeric keys
found in the processed items or the keys specified in the `keys` parameter.
Non-numeric attributes are ignored.

For each processed item, the maximum value of the observed keys are computed
and the attribute in the item is replaced by the current maximum value for
each selected keys (or all of parameter `keys` is not set).

Anytime StatisticsService
-------------------------

By providing an additional `id` attribute, this processor will be registered
as anytime *StatisticsService* and can then be queried for statistics (maxima)
of all observed keys.

For more details, see the (StatisticsService)[StatisticsService.html]
documentation.
