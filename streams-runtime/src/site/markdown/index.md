
The <code>stream-runtime</code> Module
======================================

The *stream-runtime* module provides a light-weight runtime environment for
processors implemented using the [stream-api](http://www.jwall.org/streams/stream-api/).

The runtime module itself provides a simple multi-threaded execution engine that
builds on top of a plain Java thread model and is intended for being embedded into
your application.

A standalone version is also available that includes the stream-api, the stream-core
module and the runtime itself. This standalone version is ready to be fired up with
your custom classes (optional) and an XML file.


Starting a simple Process
-------------------------

A simple process within the streams library is shown in the following XML snippet:

     <container>
         <stream id="sample" url="http://www.jwall.org/streams/sample.csv"
                 class="stream.io.CsvStream" />

         <process input="sample">
            <PrintData/>
         </process>
     </container>

This sample snippet defines a single CSV stream that is fetched from the specified
URL and is processed by a single process. The process itself will just print out any
data item that is read from the stream.

To start this sample, simply run:

      java -cp stream-rt.jar stream.run example.xml

This will produce the following output:

