RandomStream
============

This stream is a generator for a random stream source that by default provides
a single attribute `att1` of type *Double*. The values for that attribute are
drawn from a Gaussian distribution with mean *0.0* and standard deviation *1.0*.

The `keys` parameter of the stream allows the specification of a set of attributes
that will all be Gaussian distributed as described above.

The following example defines a random stream with three Gaussian distributed
attributes:

       &lt;Stream class="stream.io.RandomStream" keys="x1,x2,x3" /&gt;

The random numbers are provided by the `java.math.Random` class. For each key,
a different random generator is initialized. The seed for the *i*-th key is
`i * 1000`.

