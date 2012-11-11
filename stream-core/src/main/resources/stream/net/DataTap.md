DataTap
=======

This processor opens a TCP endpoint which clients can connect
to. The clients will receive all serialized data items that are
processed by this processor (from the point in time where the
client has connected).

This is convenient in combination with the `tcp://...` URLs
supported by the *streams* library. It allows a container to
access a stream within another container by a simple TCP
connection.
