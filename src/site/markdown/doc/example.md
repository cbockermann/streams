# A Simple Example

In this section we will give a short walk-through for a simple example.
There are two steps required for this:

  - Create a process definition, e.g. in a file `example.xml`
  - Start the process using the stream-runner

The structure of the XML process definition is rather simple. We
create a new file called `example.xml` and start with an empty
container element:

     <container>
     </container>

Subsequently, we need to add elements for the stream we want to process,
as well as the processors, that should be doing work on the data items
of the stream.


### Adding a Stream

We start by adding a simple stream definition: Suppose we want to
process a stream of data that is available in CSV format. The data is
available from a web-server at the URL

<div style="margin:auto; text-align: center;">
   <a href="http://www.sfb876.de/streams/sample.csv">http://www.sfb876.de/streams/sample.csv</a>
</div>

The following snippet shows the first 4 lines of that data as it
is stored on that location (raw, without any parsing):


We start our process definition by adding a stream element to the
container, using the `stream.io.CsvStream` class which implements
a data stream from CSV formatted data:

      <container>

         <stream id="myStream" class="stream.io.CsvStream"
                 url="http://www.sfb876.de/streams/sample.csv" />

      </container>

We used the `id` attribute to define the stream with name `myStream`. This
is later used for referencing the stream.


### Adding a Process

To process the stream, we need to add a `process` element to our container,
which uses the stream as input. This is - again - rather simple:

      <container>

         <stream id="myStream" class="stream.io.CsvStream"
                 url="http://www.sfb876.de/streams/sample.csv" />

         <process input="myStream"></process>
      </container>

The process will simply consume all the items of the stream without taking
any action. To make this a little more interesting, we need to add *processors*.
Processors are simple Java classes that act upon single data items.

A very simple processor is defined in class `stream.data.PrintData`, which
simply prints out each data item to the system output. A processor can
be directly added to the process with its Java class name:

     ...
       <process input="myStream">
           <stream.data.PrintData />
       </process>
     ...

This makes the overall container definition look as follows:

      <container>

         <stream id="myStream" class="stream.io.CsvStream"
                 url="http://www.sfb876.de/streams/sample.csv" />

         <process input="myStream">
             <stream.data.PrintData />
         </process>

      </container>


### Running the Container

After we added a processor to the process, we are ready to start the container
and run this stream-process. The *streams* framework provides a simple stream-runner
that can be found for download at
<div style="text-align: center;">
     <a href="http://www.sfb876.de/streams/stream-runner.jar">http://www.sfb876.de/streams/stream-runner.jar</a>
</div>

After downloading that file, we can start the container by issuing:

      # java -jar stream-runner.jar example.xml


<table>
   <tr>
     <th>key1</th>
     <th>key2</th>
     <th>key3</th>
   </tr>
   <tr>
     <td>val11</td>
     <td>val12</td>
     <td>val13</td>
   </tr>
   <tr>
     <td>val21</td>
     <td>val22</td>
     <td>val23</td>
   </tr>
</table>      