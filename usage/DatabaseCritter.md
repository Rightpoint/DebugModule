# Database Critter

Ever wanted to view a current database easily without root? This critter attempts
to address part of the problem by providing a very simple SQLite browser of the
current database. Currently it _only_ supports viewing tables and rows.

## Configuration


You _must_ specify a `SQLiteDatabase` to use with the `Critter` to load up the database.
The `DatabaseCritter` handles the rest!

```java

DatabaseCritter databaseCritter = new DatabaseCritter();

// register with the debugger
Debugger.getInstance().use("Database Browser", databaseCritter);

databaseCritter.setDatabase(FlowManager.getDatabase(AppDatabase.NAME).getWritableDatabase(), useBlacklist);

```

In this example, we are using [DBFlow](https://github.com/Raizlabs/DBFlow) to retrieve the proper database.

```useBlacklist``` ignores tables that are created by the Android system, such as ```android_metadata``` and
```sqlite_sequence```.
