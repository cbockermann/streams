Lookup
========

This processor provides a way to use a `stream.service.LookupService` and 
add the provided dataitem to the processed dataitem. For example 

      <Lookup key="testkey" lookup-ref="lookupService" />
      
will use the `stream.service.LookupService` lookupService to lookup the 
key testkey and merge the to dataitems.

This is useful, e.g. when static information are needed.

It is also possible to use macros as key.
