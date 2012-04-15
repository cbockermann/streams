
The <code>streams</code> Framework
=======================

The `streams` framework is a Java implementation of a variety of online machine
learning algorithms. It provides several classifier implementations as well as
algorithms for online counting or book-keeping statistics (e.g. *top-k* statistics,
and *quantiles*).

In addition to the learning algorithms, it provides a simple plugin structure to
data preprocessing and evaluation of the classifiers using a *test-then-train*
strategy.


Overview
--------

The `streams` framework comprises several modules, of which the central one is the
*stream-api* module. The *stream-api* defines the basic interfaces and data structures
used in all other modules.

The basic modules are
  
  * [stream-api](stream-api/index.html) -- the basic API interfaces and classes. This
    module is used as glue element, new modules/extensions can include this module as
    their base dependency.
    Among the basic interfaces, the stream-api module provides a very small number of 
    generic processors, e.g. for executing JavaScript or JRuby (if the jruby library
    is available in the class path).

  * [stream-core](stream-core/index.html) -- a library providing various preprocessing
    functions (processors) as well as several data stream sources (stream.io) for
    reading from CSV files, SVMlight format, URLs, etc.

  * [stream-runtime](stream-runtime/index.html) -- this modules provides a generic
    runtime environment for setting up and running stream processes that have been
    defined in an XML file.

The modules are again maven projects, each having a separate project page and
documentation.



Source Code & Usage
-------------------

The source code of the framework is available at [bitbucket.org](https://bitbucket.org/cbockermann/streams/).

Each of the modules can easily be integrated into your own code and used as library by
listing it as maven dependency. The libraries are currently available via the following
maven repository:

      <repository>
         <id>jwall</id>
         <name>jwall.org Maven Repository</name>
         <url>http://secure.jwall.org/maven/repository/all</url>
      </repository>


