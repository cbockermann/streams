LineStream
==========

This is a very simple stream that just reads from a URL line-by-line. The
content of the line is stored in the attribute determined by the
`key` parameter. By default the key `LINE` is used.
 
It also supports the specification of a simple format string that can be used
to create a generic parser to populate additional fields of the data item
read from the stream.
 
The parser format is:
 
      %(IP) [%(DATE)] "%(URL)"
 
This will create a parser that is able to read line in the format
 
      127.0.0.1 [2012/03/14 12:03:48 +0100] "http://example.com/index.html"
 
The outcoming data item will have the attribute `IP` set to
`127.0.0.1` and the `DATE` attribute set to
`2012/03/14 12:03:48 +0100`. The `URL` attribute will be set to
`http://example.com/index.html`.
In addition, the `LINE` attribute will contain the complete line string.

