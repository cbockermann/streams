GKQuantiles
===========

This process implements the algorithm proposed by Greenwald and Khanna to
estimate quantiles over a high volume stream using limited memory resources.
The algorithm requires a parameter `epsilon` to be provided, which gives some
error bound and influences the memory consumption.

An example is given by the following config:

       <stream.quantiles.GKQuantiles key="x" epsilon="0.01" />
