AsJSON
======

This processor will serialize the current item into a JSON string and
add this string to the item under the key specified in the `key` parameter.
If no `key` parameter is provided, the JSON string will be added with
key `@json`.

This can for example be useful to store the JSON string of an item in
a file or database table in connection with the *SQLWriter* processor.

The following example shows the serialization into JSON and storage of
the item in a database using the *AsJSON* and *SQLWriter* processor. In
Addition, the *CreateID* processor is used to add an ID to the item before
storing it into the database table `data_items`:

      <AsJSON key="@json" />
      <CreateID key="@id" />
     
      <SQLWriter url="jdbc:mysql://localhost:3306/test_db"
                 username="TEST" password="TEST" table="data_items"
                 keys="@id,@json" />
