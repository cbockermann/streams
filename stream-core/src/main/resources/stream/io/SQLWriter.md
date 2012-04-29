SQLWriter
=========

This processor inserts processed items into a SQL database
table. At initialization time, it checks for existence of the
table and creates the table based on the keys of the first item
if the table does not exist.

If the table exists beforehand, the table schema will be extracted
and only keys with corresponding table columns will be inserted.

The following example shows the configuration of the SQLWriter to
insert the keys `@id` and `attr1`, `attr2` into the table `DATA`:

    <SQLWriter keys="@id,attr1,attr2"
               url="jdbc:hsqldb:file:/tmp/test.db"
               username="SA" password=""
               table="DATA" />

The parameters `url`, `username` and `password` define the connection
to the database, whereas the `table` parameter defines the table into
which data is to be inserted.

### Dropping existing Tables

The *SQLWriter* also allows to drop existing tables at initialization
time by specifying the parameter `dropTable` as `true`.
