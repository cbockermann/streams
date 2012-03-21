FactEventReader
===============

This operator provides a *data stream handle* that reads data
from FITS files as produced by the FACT telescope. Each data item
that can be retrieved from the stream resembles a single FACT
event, i.e. an observation by the telescope.

The single data items consists of several features, such as the
`EventNum`, the `TriggerType` and also the `Data`, which is a large
array of 432000 elements (1440 x 300).


DRS Calibration
---------------

The *FactEventReader* operator also provides DRS calibration if a
DRS calibration data file has been specified. In that case, the
`Data` feature is converted into a 432000 element array with calibrated
data values.


Event Keys
----------

Despite the `EventNum`, `TriggerType` and `Data` each event provides
additional attributes/keys as outlined in the following.


<table>

<tr>
  <th>Key</th>
  <th>Description</th>
</tr>

<tr>
  <td><b>@id</b></td>
  <td>This attributes contains a special string that uniquely identified
      a single event by its recording date, run-number and event number.
      For example the id `2011/11/27/42/1` refers to the 1st event recorded
      in run 42 on the 27th of November 2011.
  </td>
</tr>

</table>
