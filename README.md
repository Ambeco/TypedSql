# Typed SQL for Android

> A thin type-safe wrapper around Android's SQLiteDatabase

This is a simple Java Processor that can auto-generate helper strings, methods, and classes that
provide compile-time guarantees of correctness and type safety. These also expose listeners
for you to automatically attach metrics or logging.

Values:

- Type Safety (obviously).
- Small binary size (after Proguard).
- Code mirrors SQL syntax, functioning as a learning stepping stone.
- You can use as much or as little as you like. You can fully intermix usages of this library with
  direct use of SQLiteDatabase, even within the same table or even the same selection.
- A smart IDE should be able to suggest and auto-complete the next part of the syntax.
- Every part can be written without referring to its own generated files, so IDEs are much more
  helpful.

# Table of Contents
- [Basic Usage](#basic-usage)
    - [Creating a Table](#creating-a-table)
    - [Projections](#projections)
    - [Selections](#selections)
    - [WhereBuilders](#wherebuilders)
        - [WhereBuilder filters](#wherebuilder-filters)
    - [Selecting](#selecting)
    - [Inserting](#inserting)
    - [Updating](#updating)
        - [Updating a single row](#updating-a-single-row)
        - [Bulk updating rows](#bulk-updating-rows)
    - [Deleting](#deleting) (TODO: document)
        - [Deleting a single row](#deleting-a-single-row)
        - [Bulk deleting rows](#bulk-deleting-rows)
- [Definitions](#definitions)
    - [Binders](#binders)
    - [Multibindings](#multibindings) (TODO: document)
- [Advanced Usage](#advanced-usage)
  1. [Generated Sql statements](#layer-1-generated-sql-statements-for-creating-tables-indecies-and-views-and-also-projections-and-selections)
  2. [Helper classes for WHERE clauses](#layer-2-helper-classes-for-where-clauses)
      - [WhereBuilder Advanced Notes](#wherebuilder-advanced-notes)
  3. [SQL-typed cursors](#layer-3-sql-typed-cursors)
  4. [Java-Typed cursors](#layer-4-java-typed-cursors)
      - [Rows](#rows)
  5. [InsertValues and UpdateValues](#layer-5-insertvalues-and-updatevalues)
  6. [Transaction helper methods](#layer-6-transaction-helper-methods) (TODO: plan)
  7. [Helper classes combining the above](#layer-7-helper-classes-combining-all-of-the-above)
  8. [Database upgrade steps](#layer-8-database-upgrade-steps) (TODO: plan)
- [FAQ](#faq)


# Basic Usage

## Creating a table

You create a table by putting a `@CreateTable` annotation on a class or interface. To the best of my
ability, this mirrors the sqlite syntax for `CREATE TABLE` statements, and therefore primarily
revolves around the column definitions
{@link https://www.sqlite.org/lang_createtable.html}.

`@CreateTable` generates a class by the same name as the class, with `Sql` appended. So if the
annotationsare on a class named `DepartmentTable`, then the generated code will be inside
`DepartmentTableSql`.

Each column should usually define a [`Binder`](#Binders) which translates between the Sql types and the
type-safe java types. Without a binder, the raw Sql types will be used, which is often less
type-safe.

Indexes (`@CreateIndex`) must be declared as annotations on inner classes or interfaces, so that
TypedSql code generation knows all of the indecies on each table.

Each `@CreateTable` also automatically generates two [`@Selection`](#Selections)s: One with all of
the columns in that table, and one that just selects the rowId. (Note that if your primary key is not
a unique, non-NULL, Long, then SQLite generates a separate `rowId` column).  It is common to declare
additional projections and selections inside the same class for convenience, though those also can
be defined anywhere.

The current annotation is overly complex, and there is a TODO to simplify it.

```java
@CreateTable(
		javaName = "Department",
		columns = {
				@TableColumn(
						name = "DepartmentID",
						affinity = Affinity.INTEGER,
						primaryKey = @ColumnPrimaryKey,
						binder = @ColumnBinder(
								value = RowIdBinder.class,
								binderGenerics = {DepartmentTable.class})), 
                @TableColumn(
                    name = "Name",
                    affinity = Affinity.TEXT,
                    notNull = @NotNull), 
                @TableColumn(
                    name = "Budget",
                    affinity = Affinity.INTEGER,
                    binder = @ColumnBinder(MoneyBinder.class),
                    notNull = @NotNull), 
                @TableColumn(
                    name = "StartDate",
                    affinity = Affinity.REAL,
                    binder = @ColumnBinder(InstantJulianDoubleNonNullBinder.class),
                    notNull = @NotNull,
                    defaultValue = @DefaultValue("julianday(\\'now\\',\\'subsec\\')")), 
                @TableColumn(
                    name = "Administrator",
                    affinity = Affinity.INTEGER,
                    foreignKey = @ColumnForeignKey(
                            value = PersonTable.class,
                            onDelete = ForeignKeyAction.SET_NULL))})
public class DepartmentTable {

	@CreateIndex(@IndexedColumn("Administrator"))
	interface AdministratorIndex {
	}

	//Selection for just the Administrator's Name as a String. We join to the Department table
	//so we can query by Department
	@Selection(
			select = @SelectColumns(
					projection = StringProjection.class,
					expressions = {
							@SelectExpression(
									name = "value",
									expression = "Name"),}),
			fromTable = @SelectTable(PersonTable.class),
			joins = {
					@SelectJoin(
							operator = JoinOperator.UNSPECIFIED,
							fromTable = @SelectTable(DepartmentTable.class))})
	interface AdministratorNameSelection {
	}
}
```

This example generates a class named `DepartmentTableSql`. It also generates `AdministratorIndexSql` and also
`NameSelectionSql` containing the generated classes for those. 

## Projections
Projections function as interfaces for the results of queries.
n order to select in a type safe manner, TypedSql requires you to define Projections (The columns
returned), and Selections (The tables queried) at compile time, as annotations on classes or 
interfaces, similar to `@CreateTable`.

Projections should generally be limited, having as few columns as possible. The more columns in a
projection, the fewer rows can be returned in any individual query. Projection columns can be
standalone values, or references to columns from specific tables.  Standalone values are more
flexible, but require you to specify their definition in the `Selection`, which can be annoying.
Table references are simply a shorthand, allowing the `Selection` to automatically deduce the
query definition.

Here's an example projection that contains one String value, and one specific table reference.
```java
@CreateProjection(
		expressions = @ProjectionExpression(name = "value", affinity = Affinity.TEXT),
        tables = {@ProjectionTable(value=DepartmentTable.class, columns={"Budget"})})
public class TextAndBudgetProjection {
}
```

The TypedSql library exposes a list of projections for single value queries, but building custom
projections should be common.

- `BlobProjection`
- `BooleanProjection`
- `DoubleProjection`
- `LongProjection`
- `StringProjection`

## Selections
A selection defines which tables a query uses, and how to join them. These mimic the first part of
a Sqlite `SELECT` statement, except for the `WHERE` clause. These are also defined via
annotations at compile time. Note that `Selection`s are for defining *columns*, not *rows*. Rows
are always specified at runtime.

The `select` field of the annotation tells TypedSql which projection to use, and the expression
definitions for each standalone value in the Projection. Table references will be deduced
automatically, and do not need definitions. In the example below, the `AdministratorNameSelection`
selection uses the generic `StringProjection`, and defines the `StringProjection`'s `value`
as being set from the table's `Name` column.

The rest of the `Selection` annotation sets the primary table, and joined tables, and defines
how the join should take place.  If  no table or expression is specified to define the join, but 
the remote table contains a single foreign key to the primary table's primary key, then it will
automatically join on that column.

```java
@Selection(
        select = @SelectColumns(
                projection = StringProjection.class,
                expressions = {
                        @SelectExpression(
                                name = "value",
                                expression = "Name"),}),
        fromTable = @SelectTable(PersonTable.class),
        joins = {
                @SelectJoin(fromTable = @SelectTable(DepartmentTable.class))})
interface AdministratorNameSelection {
}
```

The selections are the normal entry point into TypedSql. You can either inject them directly, or,
if they're an inner class of a table, you can inject the table, and use a method to access the
selection. More details on using the selections is below in the [Selecting](#Selecting) section.

```java
    DepartmentTable departmentTable = ...;
    Set<String> names = departmentTable.selectAdministratorNameSelection().asSetSync();
```

## WhereBuilders

Each [`Selection`](#Selection) has a `WhereBuilder` class, for selecting which
rows to return, mimicking the `WHERE` clause of Sqlite. Unlike Sqlite, TypedSql requires the first
column referenced to either be indexed, or explicitly specify that this query may have a slow table
scan.

Note that most whereBuilder clauses require Java values to be passed in, to compare against.

Commonly, a whereBuilder will only specify a rowid or primary key column, which looks like this:
```java
  CompleteWhereBuilder<DepartmentTableAllColumns> whereBuilder 
      = departmentTableAllColumns.newWhereBuilder()
          .where(w- > w.whereIndexedEquals(w.Administrator, administratorId));
```

But additional complexity is supported:
```java
  CompleteWhereBuilder<DepartmentTableAllColumns> whereBuilder 
      = departmentTableAllColumns.newWhereBuilder()
        .where(w- > w.withSlowTableScan("query is rare and clearly documented as slow")
        .whereLessThan(w.Budget, new Money(0))
		.whereEquals(w.Administrator, currentAdmin));
```

### WhereBuilder filters

A WhereBuilder is usually exposed at first via the `UnindexedWhereBuilder` interface, which
"forces" you to select on an indexed column, or explicitly opt for a slow table scan. Its most
common methods are:
```java
	IndexedWhereBuilderImpl whereIndexedEquals(AbstractJavaColumn indexedColumn, JavaType value);

	IndexedWhereBuilderImpl whereIndexedNotEquals(AbstractJavaColumn indexedColumn, JavaType value);

	IndexedWhereBuilderImpl whereIndexedLessThan(AbstractJavaColumn indexedColumn, JavaType value);

	IndexedWhereBuilderImpl whereIndexedLessEqualTo(AbstractJavaColumn indexedColumn, JavaType value);

	IndexedWhereBuilderImpl whereIndexedGreaterEqualTo(AbstractJavaColumn indexedColumn, JavaType value);

	IndexedWhereBuilderImpl whereIndexedGreaterThan(AbstractJavaColumn indexedColumn, JavaType value);

    IndexedWhereBuilderImpl whereIndexedBetween(
		AbstractJavaColumn indexedColumn,
		JavaType minInclusive,
		JavaType maxInclusive);

    IndexedWhereBuilderImpl whereIndexedNotBetween(
		AbstractJavaColumn column,
		JavaType minInclusive,
		JavaType maxInclusive);

    IndexedWhereBuilderImpl whereIndexedIn(
		AbstractJavaColumn indexedColumn,
		Collection<JavaType> values);

    IndexedWhereBuilderImpl whereAnyOfIndexed(
		Function<UnindexedWhereBuilderImpl,
		IndexedWhereBuilderImpl>... subBuilders);

    // This is an escape hatch which allows developers to explicitly choose to do a slow table scan
    // It requires an explanation parmeter for readability, but the parameter is ignored internally.
    IndexedWhereBuilderImpl withSlowTableScan(String explanation);
```

Each of these methods returns a `IndexedWhereBuilderImpl`, which has similar methods, and allows
access to all of the unindexed columns as well.
```java

	IndexedWhereBuilderImpl whereEquals(AbstractJavaColumn column, JavaType value);

	IndexedWhereBuilderImpl whereNotEquals(AbstractJavaColumn column, JavaType value);

	IndexedWhereBuilderImpl whereLessThan(AbstractJavaColumn column, JavaType value);

	IndexedWhereBuilderImpl whereLessEqualTo(AbstractJavaColumn column, JavaType value);

	IndexedWhereBuilderImpl whereGreaterEqualTo(AbstractJavaColumn column, JavaType value);

	IndexedWhereBuilderImpl whereGreaterThan(AbstractJavaColumn column, JavaType value);

	IndexedWhereBuilderImpl whereIsNull(AbstractJavaColumn column);

	IndexedWhereBuilderImpl whereNotNull(AbstractJavaColumn column);

	IndexedWhereBuilderImpl whereBetween(
			AbstractJavaColumn column,
			JavaType minInclusive,
			JavaType maxInclusive);

	IndexedWhereBuilderImpl whereNotBetween(
			AbstractJavaColumn column,
			JavaType minInclusive,
			JavaType maxInclusive);

	IndexedWhereBuilderImpl whereIn(AbstractJavaColumn column, Collection<JavaType> values);

	IndexedWhereBuilderImpl whereNotIn( AbstractJavaColumn column, Collection<JavaType> values);

	IndexedWhereBuilderImpl whereLike( AbstractJavaColumn indexedColumn, String value);

	IndexedWhereBuilderImpl whereNotLike( AbstractJavaColumn indexedColumn, String value);

    IndexedWhereBuilderImpl whereAnyOf(Function<UnindexedWhereBuilderImpl, UnindexedWhereBuilderImpl>... subBuilders);
```

## Selecting
Basic usage is straightforward and intuitive, given the prior parts:
```java
  DepartmentTableAllColumnsRow Department
      = departmentsTable.selectAllColumnsFromDepartmentTable()
		.whereRowIdEquals(departmentId))
		.asRowOrThrowSync();
```

Or for the slightly more complex case:
```java
  Set<DepartmentTableAllColumnsRow> administratorDepartments
      = departmentsTable.selectAllColumnsFromDepartmentTable()
		.where(w -> w.withSlowTableScan("query is rare and clearly documented as slow")
		             .whereEquals(w.Administrator(), administrator))
		.asSetSync();
```

The most common ways of "gathering" the results are:
```java
	@Nullable Row asNullableRowSync();

	Optional<Row> asOptionalRowSync();

	Row asRowOrThrowSync();

	List<Row> asListSync();

	Set<Row> asSetSync();

	@MustBeClosed Stream<Row> asStreamSync();
	
	@MustBeClosed JavaCursor asJavaCursorSync();

    @MustBeClosed CursorIterator<JavaCursor, Row> asIteratorSync();
```
The first five methods also offer `Async` variants that return a `FluentFuture` of the result.

The later five methods also offer `Async` variants that return a `ClosableFuture` of the result, and
also offer `unchecked` variants that lack the `@MustBeClosed` annotations,  which allows those to be
used more freely, but also allows you to leak the cursors.

## Inserting

Insertion has two parts. The first part is a `InsertValues` class, with getters and setters for the
various columns.
```java
   DatabaseTableInsertValues insertValues = databaseTable.newInsertValues()
       .setName("Finance")
       .setBudget(new Money(100))
       .setStartDate(Instant.now())
       .setAdministrator(karen);
```
Insertion uses a simple syntax.

```java
	departmentsTable.insertIntoDepartmentTable()
        .values(insertValues)
        .sync();
```

There is also a `values` method that takes a `Function<InsertValues,InsertValues>`, allowing you
to set the values via a lambda, which is a convenient syntax, though the resulting binary is
slightly larger.
```java
	departmentsTable.insertIntoDepartmentTable()
        .values(v -> v.setName("Finance")
            .setBudget(new Money(100))
            .setStartDate(Instant.now())
            .setAdministrator(karen))
        .sync();
```

## Updating

TypedSql has two subtly different update APIs. One for updating
single rows, and one for updating multiple rows.

Like insertion, updating uses a `UpdateValues` object, with getters and setters for the
various columns.
```java
   DatabaseTableUpdateValues updateValues = databaseTable.newUpdateValues(rowId)
       .setName("Finance")
       .setBudget(new Money(100))
       .setStartDate(Instant.now())
       .setAdministrator(karen);
```

### Updating a single row

Updating a single row by id is straightforward:

```java
    boolean didUpdate = departmentTable.updateDepartmentTable(departmentId)
        .set(v -> v.value(v.Administrator(), newAdministrator))
        .where(w -> w.whereEquals(v.Administrator(), oldAdministrator))
        .existingRowSync();
```

(This syntax requires the rowId, so if you are updating a single row based on some other condition,
you must use the [bulk update syntax](#bulk-updating-rows)

The `where` method takes a `IndexedWhereBuilder` instance, or a `Function<IndexedWhereBuilder, IndexedWhereBuilder>`,
in exactly the same way as [Selecting](#selecting).
The `set` method can take an `UpdateValues` instance, which is a simple
java class with getters and setters
```java
    public boolean hasDepartmentID();
    public RowId<DepartmentTable> getDepartmentID();

    public boolean hasName();
    public String getName();
    public Chained setName(String name);

    public boolean hasBudget();
    public Money getBudget();
    public Chained setBudget(Money budget);

    //etc, one set for each Java column
```
```java
    // Like the others, you can simply fetch this from the generated class
    DepartmentTableUpdateValues updateValues 
            = departmentTable.newUpdateValues(departmentId);
```
or the `set` method has an overload that takes a `Function<UpdateValues,UpdateValues>`, leading to the
lambda syntax shown in the example above.
Most code will instead use `existingRowSync` and `existingRowAsync` which do a second query on
failure to check if the row already existed, and throw a
`com.tbohne.sqlite.exceptions.RowNotFoundException` for that case.  This extra
query incurs a very minor runtime penalty.

TypedSql also offers `withDetailsSync` and `withDetailsAsync` Apis, which also do the followup query  
 on failure, but instead they return a `UpdateResult` enum. This allows
developers to distinguish between a row-not-existing and some other constraint failure.

```java
public enum UpdateResult {
	UPDATED,
	ROW_FAILED_CONDITIONS, //The row existed, but didn't match the 'where' clause
	ROW_NOT_FOUND,
}
```

The fastest update methods are `withoutDetailsSync` and `withoutDetailsAsync`, just do a direct
sql update and return `true` if the operation succeeded, or `false` if there was an error of any
sort, including if the row wasn't found.

### Bulk updating rows

Bulk updating rows is similar to updating a single row, but the return types are different

```
  Set<RowId<DepartmentTable>> departments
    = departmentTable.updateDepartmentTable()
        .set(v -> v.value(v.Administrator(), newAdministrator))
        .where(w -> w.withSlowTableScan("replace by Admin is rare and documented")
            .whereEquals(w.Administrator(), oldAdministrator))
        .withDetailsSync();
```
For bulk updates, the `updateDepartmentTable` takes no rowId, and the `where` method requires an
`UnindexedWhereBuilder` instead of an `IndexedWhereBuilder`, and so the first condition must be
either an indexed column, or `withSlowTableScan`.

The `withDetailsSync` and `withDetailsAsync` methods do an unconditional additional query to check which rows will update
before doing the update, in order to return the affected row ids. This extra
query incurs a very minor runtime penalty. The `withoutDetailsSync` and
`withoutDetailsAsync` methods just do a direct sql update, and simply return the number of updated rows.

## Deleting

Deleting is very similar to updating, except there is no `UpdateValues` parameter.

### Deleting a single row

```java
    DeleteResult result = departmentsTable.deleteFromDepartmentTableWhereIdEquals(departmentId)
        .existingRowSync();
```

For the rare cases where you want a conditional delete, you can use the `where` method.

```java
    DeleteResult result = departmentsTable.deleteFromDepartmentTableWhereIdEquals(departmentId)
        .where(w -> w.whereEquals(w.Administrator(), oldAdministrator))
        .existingRowSync();
```

Similar to updates, `existingRowSync`, and `existingRowAsync` methods will do a subsequent query
if the deletion failed to determine if the row had already existed or not, and throw a
`com.tbohne.sqlite.exceptions.RowNotFoundException` for that case.  This extra
query incurs a very minor runtime penalty.

TypedSql also offers `withDetailsSync` and `withDetailsAsync` Apis, which also do the followup query  
 on failure, but instead they return a `DeleteResult` enum. This allows
developers to distinguish between a row-not-existing and some other constraint failure.

```java
public enum DeleteResult {
	DELETED,
	ROW_FAILED_CONDITIONS, //The row existed, but didn't match the 'where' clause
	ROW_NOT_FOUND,
}
```

The fastest update methods are `withoutDetailsSync` and `withoutDetailsAsync`, just do a direct
sql deletion and return `true` if the operation succeeded, or `false` if there was an error of any
sort, including if the row wasn't found.

### Bulk deleting rows

Bulk deleting rows is similar to deleting a single row, but the return types are different

```
  Set<RowId<DepartmentTable>> departments
    = departmentTable.deleteFromDepartmentTable()
        .where(w -> w.withSlowTableScan("replace by Admin is rare and documented")
            .whereEquals(w.Administrator(), oldAdministrator))
        .withDetailsSync();
```
For bulk deletes, the `deleteDepartmentTable` takes no rowId, and the `where` method requires an
`UnindexedWhereBuilder` instead of an `IndexedWhereBuilder`, and so the first condition must be
either an indexed column, or `withSlowTableScan`.

The `withDetailsSync` and `withDetailsAsync` methods do an unconditional additional query to check which rows will update
before doing the update, in order to return the affected row ids. This extra
query incurs a very minor runtime penalty. The `withoutDetailsSync` and
`withoutDetailsAsync` methods just do a direct sql update, and simply return the number of updated rows.


# Definitions

## Binders

Each SQL column has a "binder" that transforms between the Java types to the Sql types. Most binders
are "single" binders, which bind a single sql column to a single java type.

Each "Single" binder usually offers just two methods:
```java
public interface SimpleColumnBinder<JavaType, SqlType> {
	JavaType fromSql(SqlType columnValue);
	SqlType toSql(JavaType javaValue);
}
```

In addition to conversions, these also tell TypedSql which Java types to use for each column, and
also control the exact format that is read and written to/from the database.

The TypedSql library exposes a list of binders for common types:

- `BitSetNonNullBinder`
- `BooleanNonNullBinder`
- `ByteArrayNonNullBinder`
- `DoubleNonNullBinder`
- `Duration8601NonNullBinder`
- `EnumNonNullBinder<T>`
- `Instant8601NonNullBinder`
- `IntNonNullBinder`
- `LocaleNonNullBinder`
- `LongNonNullBinder`
- `RowIdNonNullBinder`
- `StringNonNullBinder`
- `UriNonNullBinder`
- `UuidBytesNonNullBinder`
- `UuidStringNonNullBinder`

Each of these also has a `@Nullable` version as well, and there's also a long list of alternative 
`Instant` and `Duration` binders, depending on the exact format you want to store in the database.
This list is not limiting: you are *strongly* encouraged to make binders for your Java types that
you want to store in your database.

There's also a few special binders:
- `AffinityBinder` will use whichever Java type matches the Sql column affinity. This is the default.
- `RawBinder` will generate per-Sql-type methods for this column, so that you can read or write
multiple types into/from a single column. This should not be used in new code, and exists solely to
be able to read weirdly typed values that were already pre-existing.
- `VoidBinder` hides the Sql column from the Java code entirely.

### Multibindings

It is possible to bind multiple sql types to a single java type. This is called "Multibinding".

TODO: document

# Advanced Usage

This library comes in "layers".

1. Generated SQL statements for creating tables, indecies, and views, and also projections and
   selections
   (This doesn't offer any true type safety, but does offer compile time correctness of syntax,
   which is basically the same thing)
2. Helper classes for WHERE clauses.
3. SQL-typed cursors (Cursors expose columns as Sql types: Long, Double, String, and byte[]).
4. Java-typed cursors (Cursors expose columns as Java types: RowId, Instant, UUID, Coordinate).
5. InsertValues and UpdateValues
6. Transaction helper methods that expose listeners for attaching metrics or logging.
7. Helper classes combining all of the above. These can return SQL-typed cursors, Java-typed
   cursors,
   or even Nullable/Optional/List/Set/Maps of auto-generated "row" classes.
8. Future: Database upgrade steps

## Layer 1: Generated SQL statements for creating tables, indecies, and views, and also projections and selections

This layer provides a Strings, providing compile time guarantees that these string literals are
correct. Its not super useful by itself, but exists regardless. Yhis is the only layer that is
stateless and does not require constructing instances of generated classes.

```java
String createTableStatement = DepartmentTableSql.getCreateTable(/*ifNotExists=*/false);
// "CREATE TABLE IF NOT EXISTS Department (DepartmentID INTEGER PRIMARY KEY, Name TEXT NOT NULL..."

String creatAdministratorIndexStatement = DepartmentTableSql.getCreateAdministratorIndex(/*ifNotExists=*/false);
//"CREATE INDEX IF NOT EXISTS AdministratorIndex ..."
//alternatively:
String[] createStatements = DepartmentTableSql.getCreateStatements(/*ifNotExists=*/false);

String[] projection = AdministratorNameSelection.getProjection();
// {"Person.Name"}
String join = AdministratorNameSelection.getJoin();
// "JOIN Department ON Person.PersonId = Department.AdministratorId"
```

Note that the selection does not have static methods for selecting which row im this layer. That
would require converting Java types to SQL types, which might require state.

## Layer 2: Helper classes for WHERE clauses

This layer provides helper classes for building up WHERE clauses in a type safe manner. Each
`@Selection` provides a WHERE builder, allowing access to the relevant columns. (This example
focuses on the `DepartmentTableAllColumns` selection, which selects all the columns in the
`DepartmentTable`).


### WhereBuilder Advanced Notes

There's three ways to build WhereBuilders.

1. You can inject a `WhereBuilderFactory` and call its `create` method.
2. You can inject the `Sql` class, and call `#newWhereBuilder()`.
3. You can directly construct one:

```java
//Many TypedSql classes use this columns object
DepartmentTableSql.Columns columns = new DepartmentTableSql.Columns(
		new RowIdNonNullBinder<DepartmentTable>(),
		new MoneyBinder(),
		new InstantJulianDoubleNonNullBinder(),
		new RowIdNonNullBinder<PersonTable>()
    );
   //WhereBuilders are meant to be constructed and used all at once, so we almost never assign them
    // to a variable
   new DepartmentTableSql.DepartmentTableAllColumns.WhereBuilderImpl(columns);
```

WhereBuilder will detect when the same parameter is passed multiple times and only forward them to
Sql once.

```java
  Money oldMinimumBudget = ...;
  Money newMinimumBudget = ...;
  Instant policyChangeDate = ...;
	
  CompleteWhereBuilder<DepartmentTableAllColumns> whereBuilder 
      = departmentTableAllColumns.newWhereBuilder()
    .withSlowTableScan("query is rare and clearly documented as slow")
    .whereAnyOf(
      w -> w.whereLessThan(w.StartDate, policyChangeDate)
          .whereLessThan(w.Budget, oldMinimumBudget),
      w -> w.whereGreaterEqualTo(w.StartDate, policyChangeDate)
		  .whereLessThan(w.Budget, newMinimumBudget));

  String whereClause = whereBuilder.build();
  String[] whereArgs = whereBuilder.getArgs(); //policyChangeDate is only here once, not twice
```

## Layer 3: SQL-typed cursors

On the other end of an SQL selection is the Cursor. Android Cursors are notoriously not-type-safe,
and TypedSql offers a pair of cursor wrappers, that are very simple type-safe wrappers.
The first is `SqlCursorWrapper`, which exposes the raw Sql types. One is generated for each 
`@Projection`, and they look similar to this:

```java
class DepartmentTableAllColumnsSqlCursorWrapperImpl extends TypedCursor{
	public Long SqlDepartmentId();
	public String SqlName();
	public Long SqlBudget();
	public Double SqlStartDate();
	public @Nullable Long SqlAdministrator();
}
```

When writing new code, these can be ignored, but these can be useful when upgrading legacy code, 
if you're not ready or able to jump all the way to the Java-typed cursors.

## Layer 4: Java-typed cursors

More commonly, you'll want to use the `JavaCursorWrapper`s which expose the exact Java type you want
to use, instead of the raw Sql types. Like `SqlCursorWrapper`, one is generated for each 
`@Projection`, and they look similar to this:

```java
class DepartmentTableAllColumnsJavaCursorWrapperImpl extends TypedCursor {
	public DepartmentTableAllColumnsRow getRow();
	public RowId<DepartmentTable> DepartmentId();
	public String Name();
	public Money Budget();
	public Instant StartDate();
	public @Nullable RowId<PersonTable> Administrator();
}
```
Note that this exposes strong types, instead of merely String/Long/Double/byte[].

### Rows

The JavaCursor also exposes a `Row`, which allows you to extract the values for a whole row, close
the cursor, and pass those values around in memory. These rows are very simple data objects:

```java
class DepartmentTableAllColumnsRow {
	public RowId<DepartmentTable> DepartmentId();
	public String Name();
	public Money Budget();
	public Instant StartDate();
	public @Nullable RowId<PersonTable> Administrator();
	public boolean equals(@Nullable Object other); //only compares DepartmentId
	public int hashCode(); //only hashes DepartmentId
}
```

## Layer 5: InsertValues and UpdateValues

These are relatively simple classes with getter-setter functionality:
```java
class DepartmentTableWriteValues 
    implements DatabaseTableUpdateValues, DatabaseTableInsertValues {

	DepartmentTableWriteValues(Columns columns); //all fields default to unset
	DepartmentTableWriteValues(Columns columns, AllColumnsRow row); //all fields set from this row
	
	public ContentValues getValues();
	
	public void setDepartmentID(RowId<DepartmentTable> departmentID); //this is hidden from DatabaseTableUpdateValues
	public boolean hasDepartmentID();
	public RowId<DepartmentTable> getDepartmentID();

	public boolean hasName();
	public String getName();
	public void setName(String name);

	public boolean hasBudget();
	public Money getBudget();
	public void setBudget(Money budget);

	public boolean hasStartDate();
	public Instant getStartDate();
	public void setStartDate(Instant startDate);

	public boolean hasAdministrator();
	public @Nullable RowId<PersonTable> getAdministrator();
	public void setAdministrator(@Nullable RowId<PersonTable> administrator);
}
```
Attempting to read a field that isn't yet set with throw a `FieldNotSetException` (even if it is
nullable).

If the field is "immutable", then the setter methods will not be visible from the `DatabaseTableUpdateValues` interface.

You can construct these two ways:
1. Inject the `Sql` class and call `#newInsertValues()` or `#newUpdateValues(RowId)` or `#newUpdateValues(Row)`
2. Construct the `DepartmentTableWriteValues` directly.

## Layer 6: Transaction helper methods

TODO: plan

## Layer 7: Helper classes combining all of the above.

Most code will focus on this layer, which uses a few helper methods to wrap all of the above.

## Layer 8: Database upgrade steps

TODO: plan

# FAQ

### Question

Answer