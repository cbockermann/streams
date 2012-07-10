Timestamp
=========

This processor parses the date time from an attribute using a specified
format string and stores the parsed time as a long value into the
`@timestamp` key by default.

The processor requires at least a `format` and a `from` parameter. The
`format` specifies a date format to parse the time from. The `from`
parameter determines the key attribute from which the date is to be parsed.

The following example shows a timestamp parser that parses the `DATE` key
using the format `yyyy-MM-dd-hh:mm:ss`. The resulting timestamp (milliseconds
UNIX time) is stored under key `@time`:


      <Timestamp key="@time" format="yyyy-MM-dd-hh:mm:ss"
                 from="DATE" />
