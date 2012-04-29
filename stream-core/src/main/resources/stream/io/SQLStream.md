SQLStream
=========

This class implements a DataStream that reads items from
a SQL database table. The class requires a `jdbc` URL string,
a username and password as well as a `select` parameter that
will select the data from the database.

The following XML snippet demonstrates the definition of a SQL
stream from a database table called `TEST_TABLE`:

     <Stream class="stream.io.SQLStream"
             url="jdbc:mysql://localhost:3306/TestDB"
             username="SA" password=""
             select="SELECT * FROM TEST_TABLE" />

The database connection is established using the user `SA` and
no password (empty string). The above example connects to a
MySQL database.

As the SQL database drivers are not part of the streams library,
you will need to provide the database driver library for your
database on the class path.
