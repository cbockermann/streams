Package stream.data
===================

This package provides processors that perform transformations or mangling
of the data items themselves. Examples for such processors are `CreateID`,
which adds a sequential ID attribute to each processed item or the
`RemoveKeys` processor which removes attributes by name.

Other useful processors provide numerical binning (`NumericalBinning`), 
setting of values in various scopes (`SetValue`) and the like.
