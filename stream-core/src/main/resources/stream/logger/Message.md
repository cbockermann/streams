Message
=======

This processor prints a simple message to standard output. The message
is printed for each processed item and may contain macros that will be
evaluated for each item.

The *Message* processor is a conditioned processor, which allows for
specifying conditions for its execution.

The following example defines a processor that will output a messags with
the items `@id` attribute for each item that has an `alert` value higher
than 10:

     &lt;Message message="Alert for item %{data.@id}!"
                 condition="%{data.alert} @gt 10" /&gt;
