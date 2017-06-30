

### Distributed stream processing

Distributed stream processing systems (DSPS) that allow for distributed computation among a large set of computing nodes.
Common for all modern DSPS is the definition of the jobs as directed acyclic graphs (DAG) whereas the data flows from a source node through multiple processing nodes.
With ``streams`` framework it is possible to define jobs as DAGs which can be mapped onto DSPS.

Currently, ``streams`` jobs can be translated into the native inside of:

  * Storm ([streams-storm](https://bitbucket.org/cbockermann/streams-storm))
  * Spark & Spark Streaming ([batch](https://bitbucket.org/mbunse/streams-spark), [streaming](https://github.com/alexeyegorov/streams-flink))
  * Flink ([streams-flink](https://github.com/alexeyegorov/streams-flink))



The need for DSPS emerged from real world requirements to process data continuously and being able to define custom streaming applications for specific business use cases.
providing low-level means – such as API function – for application programming, scalability and fault-tolerance.