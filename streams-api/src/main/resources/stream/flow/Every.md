Every
=====

This processor requires a parameter `n` and will then execute all inner
processors every `n` data items, i.e. if the number of observed items
modulo `n` equals 0.

In all other cases, the inner processors will simply be skipped.
