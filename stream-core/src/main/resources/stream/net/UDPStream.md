UDPStream
=========

This class implements a stream that is fed by UDP messages. At
startup a UDP socket is opened. Any read on this stream will block
until a data gram has been received.

The resulting data item contains an attribute `udp:data`, which contains
a byte-array of the data gram contents, and attributes `udp:size` which
holds the packet size.

Additionally, the items contain attributes `udp:source` and `udp:port`
which provide the values of the sender address and port of the corresponding
data gram.


Example
-------

The following example snippet defines a UDP stream which serves as input
to a process:

       &lt;container&gt;
          &lt;stream id="syslog" class="stream.net.UDPStream"
                     port="514" &gt;

          &lt;process input="syslog"&gt;
              ...
          &lt;/process&gt;
       &lt;/container&gt;
