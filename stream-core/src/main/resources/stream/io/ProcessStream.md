ProcessStream
=============

This processor executes an external process (programm/script) that produces
data and writes that data to standard output. This can be used to use external
programs that can read files and stream those files in any of the formats
provided by the stream API.

The default format for external processes is expected to be CSV.

In the following example, the Unix command `cat` is used as an example, producing
lines of some CSV file:

       <Stream class="stream.io.ProcessStream"
               command="/bin/cat /tmp/test.csv"
               format="stream.io.CsvStream" />

The process is started at initialization time and the output will be read from
standard input.
