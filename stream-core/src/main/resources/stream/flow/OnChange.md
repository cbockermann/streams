OnChange
========

The *OnChange* processor is a processor list that executes all nested
processors if some state has changed. This is similar to the *If*
processor, but provides support for a process state check.

In the following example, the *Message* processor is only executed if
the context variable `status` changes from `green` to `yellow`:

      ...
      <OnChange from=`green` to=`yellow`>
         <Message message="Status change detected!" />
      </OnChange>
