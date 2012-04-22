JRuby
=====

This processor executes JRuby (Ruby) scripts using the Java ScriptingEngine
interface. To use this processor, the JRuby implementation needs to be available
in the classpath.

The script is evaluated for each processed item and will be provided to the
script as variable `$data`.
