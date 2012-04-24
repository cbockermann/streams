Package stream.io
=================

Reading data is usually the first step in data processing. This package
provides a set of data stream sources for data files/resources in various
formats.

Most of the streams provided by this package do read from URLs, which
allows reading from files as well as from network URLs such as HTTP urls.

The streams provide an iterative access to the data and use the default
`DataFactory` for creating data. They do usually share some common parameters
supported by most of the streams such as `limit` or `username` and `password`.

