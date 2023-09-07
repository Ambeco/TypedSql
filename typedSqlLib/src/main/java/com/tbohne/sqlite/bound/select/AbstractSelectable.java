package com.tbohne.sqlite.bound.select;

import static java.util.Objects.requireNonNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.common.util.concurrent.ClosingFuture;
import com.google.common.util.concurrent.FluentFuture;
import com.google.errorprone.annotations.MustBeClosed;
import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;
import com.tbohne.sqlite.exceptions.RowNotFoundException;
import com.tbohne.sqlite.exceptions.TooManyRowsException;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AbstractSelectable<Table,
		UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		SqlCursor extends AbstractTypedCursorWrapper,
		JavaCursor extends AbstractTypedCursorWrapper, Row>
		implements Selectable<Table, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, SqlCursor, JavaCursor, Row>,
							 Selection.RawQueryParams
{
	protected final Supplier<UnindexedWhereBuilderImpl> whereBuilder;
	protected final Function<Cursor, SqlCursor> sqlCursorFactory;
	protected final Function<Cursor, JavaCursor> javaCursorFactory;
	protected final Function<JavaCursor, Row> rowCursorFactory;

	protected final Executor databaseQueryExecutor;
	protected final Executor binderExecutor;

	protected final Supplier<SQLiteDatabase> databaseSync;
	protected final Supplier<FluentFuture<SQLiteDatabase>> databaseAsync;

	protected final boolean distinct;
	protected final String table;
	protected final String[] columns;
	protected final @MonotonicNonNull String groupBy;
	protected final @MonotonicNonNull String having = null; //TODO
	protected final @MonotonicNonNull String orderBy = null; //TODO
	protected final @MonotonicNonNull String limit = null; //TODO
	protected final IndexedWhereBuilderImpl selectionAndArgs;

	protected AbstractSelectable(
			Supplier<UnindexedWhereBuilderImpl> whereBuilder,
			Function<Cursor, SqlCursor> sqlCursorFactory,
			Function<Cursor, JavaCursor> javaCursorFactory,
			Function<JavaCursor, Row> rowCursorFactory,
			Executor databaseQueryExecutor,
			Executor binderExecutor,
			Supplier<SQLiteDatabase> databaseSync,
			Supplier<FluentFuture<SQLiteDatabase>> databaseAsync,
			boolean distinct,
			String table,
			String[] columns,
			IndexedWhereBuilderImpl selectionAndArgs,
			@Nullable String groupBy)
	{
		this.whereBuilder = whereBuilder;
		this.sqlCursorFactory = sqlCursorFactory;
		this.javaCursorFactory = javaCursorFactory;
		this.rowCursorFactory = rowCursorFactory;

		this.databaseQueryExecutor = databaseQueryExecutor;
		this.binderExecutor = binderExecutor;

		this.databaseSync = databaseSync;
		this.databaseAsync = databaseAsync;
		this.table = table;
		this.columns = columns;

		this.distinct = distinct;
		this.selectionAndArgs = selectionAndArgs;
		this.groupBy = groupBy;
	}

	@Override
	public Selection<SqlCursor, JavaCursor, Row> where(IndexedWhereBuilderImpl whereClause) {
		selectionAndArgs.and(whereClause);
		return this;
	}

	@Override
	public Selection<SqlCursor, JavaCursor, Row> where(
			Function<UnindexedWhereBuilderImpl, ? extends IndexedWhereBuilderImpl> whereFunction)
	{
		selectionAndArgs.and(whereFunction.apply(whereBuilder.get()));
		return this;
	}

	@Override
	public RawQueryParams getRawQueryParameters() {
		return this;
	}

	@Override
	public boolean getRawDistinct() {
		return distinct;
	}

	@Override
	public String getRawTables() {
		return table;
	}

	@Override
	public String[] getColumns() {
		return columns.clone();
	}

	@Override
	public @MonotonicNonNull String getRawSelectionString() {
		return selectionAndArgs.build();
	}

	@Override
	public @MonotonicNonNull String[] getRawSelectionArgs() {
		return selectionAndArgs.getArgs();
	}

	@Override
	public @MonotonicNonNull String getRawGroupBy() {
		return groupBy;
	}

	@Override
	public @MonotonicNonNull String getRawHaving() {
		return having;
	}

	@Override
	public @MonotonicNonNull String getRawOrderBy() {
		return orderBy;
	}

	@Override
	public @MonotonicNonNull String getLimit() {
		return limit;
	}

	@Override
	public @MonotonicNonNull Row asNullableRowSync() {
		try (JavaCursor cursor = asJavaCursorSync()) {
			if (cursor.getCount() == 0) {
				return null;
			}
			if (cursor.getCount() > 1) {
				throw new TooManyRowsException("Expected at most 1 row but found " + cursor.getCount());
			}
			return rowCursorFactory.apply(cursor);
		}
	}

	@Override
	public Optional<Row> asOptionalRowSync() {
		return Optional.ofNullable(asNullableRowSync());
	}

	@Override
	public Row asRowOrThrowSync() {
		Row r = asNullableRowSync();
		if (r == null) {
			throw new RowNotFoundException("Row not found");
		}
		return r;
	}

	@Override
	public ArrayList<Row> asListSync() {
		try (JavaCursor cursor = asJavaCursorSync()) {
			ArrayList<Row> arrayList = new ArrayList<>(cursor.getCount());
			while (cursor.moveToNext()) {
				arrayList.add(rowCursorFactory.apply(cursor));
			}
			return arrayList;
		}
	}

	@Override
	public HashSet<Row> asSetSync() {
		try (JavaCursor cursor = asJavaCursorSync()) {
			HashSet<Row> set = new HashSet<>();
			while (cursor.moveToNext()) {
				set.add(rowCursorFactory.apply(cursor));
			}
			return set;
		}
	}

	@Override
	@RequiresApi(api = Build.VERSION_CODES.N)
	@MustBeClosed
	public Stream<Row> asStreamSync() {
		return asUncheckedStreamSync();
	}

	@Override
	@RequiresApi(api = Build.VERSION_CODES.N)
	public Stream<Row> asUncheckedStreamSync() {
		CursorIterator<JavaCursor, Row> iterator = asUncheckedIteratorSync();
		try {
			return StreamSupport.stream(Spliterators.spliterator(iterator,
																													 iterator.getCount(),
																													 Spliterator.ORDERED
																													 | Spliterator.DISTINCT
																													 | Spliterator.IMMUTABLE
																													 | Spliterator.NONNULL
																													 | Spliterator.SIZED
																													 | Spliterator.ORDERED
																													 | Spliterator.SUBSIZED), false).onClose(iterator::close);
		} catch (RuntimeException e) {
			iterator.close();
			throw e;
		}
	}

	@Override
	@MustBeClosed
	public JavaCursor asJavaCursorSync() {
		return asUncheckedJavaCursorSync();
	}

	@Override
	public JavaCursor asUncheckedJavaCursorSync() {
		Cursor rawCursor = asRawCursorSync();
		try {
			return javaCursorFactory.apply(rawCursor);
		} catch (RuntimeException e) {
			rawCursor.close();
			throw e;
		}
	}

	@Override
	@MustBeClosed
	public SqlCursor asSqlCursorSync() {
		return asUncheckedSqlCursorSync();
	}

	public SqlCursor asUncheckedSqlCursorSync() {
		Cursor rawCursor = asRawCursorSync();
		try {
			return sqlCursorFactory.apply(rawCursor);
		} catch (RuntimeException e) {
			rawCursor.close();
			throw e;
		}
	}

	@Override
	@MustBeClosed
	public Cursor asRawCursorSync() {
		return asUncheckedRawCursorSync();
	}

	@Override
	public Cursor asUncheckedRawCursorSync() {
		return asUncheckedRawCursor(databaseSync.get());
	}

	protected Cursor asUncheckedRawCursor(SQLiteDatabase db) {
		return db.queryWithFactory(/*cursorFactory=*/null, /*cursorFactory=*/
																								 distinct,
																								 table,
																								 columns,
																								 selectionAndArgs.build(),
																								 selectionAndArgs.getArgs(),
																								 groupBy,
																								 having,
																								 orderBy,
																								 limit, /*cancellationSignal=*/
																								 null);
	}

	@Override
	@MustBeClosed
	public CursorIterator<JavaCursor, Row> asIteratorSync() {
		return asUncheckedIteratorSync();
	}

	@Override
	public CursorIterator<JavaCursor, Row> asUncheckedIteratorSync() {
		JavaCursor cursor = asUncheckedJavaCursorSync();
		try {
			return new CursorIterator<>(rowCursorFactory, cursor);
		} catch (RuntimeException e) {
			cursor.close();
			throw e;
		}
	}

	@Override
	public FluentFuture<@Nullable Row> asNullableRowAsync() {
		return asJavaCursorAsync().transform((closer, cursor) -> {
			if (requireNonNull(cursor).getCount() == 0) {
				return null;
			}
			if (cursor.getCount() > 1) {
				throw new TooManyRowsException("Expected at most 1 row but found " + cursor.getCount());
			}
			return rowCursorFactory.apply(cursor);
		}, binderExecutor).finishToFuture();
	}


	@Override
	public FluentFuture<Optional<Row>> asOptionalRowAsync() {
		return asNullableRowAsync().transform(Optional::ofNullable, binderExecutor);
	}

	@Override
	public FluentFuture<Row> asRowOrThrowAsync() {
		return asNullableRowAsync().transform((r) -> {
			if (r == null) {
				throw new RowNotFoundException("Row not found");
			}
			return r;
		}, binderExecutor);
	}

	@Override
	public FluentFuture<ArrayList<Row>> asListAsync() {
		return asJavaCursorAsync().transform((closer, cursor) -> {
			ArrayList<Row> arrayList = new ArrayList<>(requireNonNull(cursor).getCount());
			while (cursor.moveToNext()) {
				arrayList.add(rowCursorFactory.apply(cursor));
			}
			return arrayList;
		}, binderExecutor).finishToFuture();
	}

	@Override
	public FluentFuture<Set<Row>> asSetAsync() {
		return asJavaCursorAsync().transform((closer, cursor) -> {
			Set<Row> set = new HashSet<>();
			while (cursor.moveToNext()) {
				set.add(rowCursorFactory.apply(cursor));
			}
			return set;
		}, binderExecutor).finishToFuture();
	}

	//	@Override
	//	@RequiresApi(api = Build.VERSION_CODES.N)
	//    public ClosingFuture<Stream<Row>> asStreamAsync() {
	//        return asIterator()
	//                .transform((closer, iterator) -> closer.eventuallyClose(StreamSupport.stream(
	//                    Spliterators.spliterator(iterator,
	//                            requireNonNull(iterator).getCount(),
	//                            Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.SIZED | Spliterator.ORDERED | Spliterator.SUBSIZED),
	//                    false)
	//        , binderExecutor)
	//                        , binderExecutor);
	//    }

	@SuppressWarnings("UnstableApiUsage")
	@Override
	@RequiresApi(api = Build.VERSION_CODES.N)
	public FluentFuture<Stream<Row>> asUncheckedStreamAsync() {
		return asUncheckedIteratorAsync().transform(iterator -> StreamSupport.stream(Spliterators.spliterator(iterator,
																																																					requireNonNull(
																																																							iterator).getCount(),
																																																					Spliterator.ORDERED
																																																					| Spliterator.DISTINCT
																																																					| Spliterator.IMMUTABLE
																																																					| Spliterator.NONNULL
																																																					| Spliterator.SIZED
																																																					| Spliterator.ORDERED
																																																					| Spliterator.SUBSIZED),
																																								 false), binderExecutor);
	}

	@Override
	public ClosingFuture<JavaCursor> asJavaCursorAsync() {
		return asRawCursorAsync().transform(
				(closer, rawCursor) -> closer.eventuallyClose(javaCursorFactory.apply(rawCursor), binderExecutor),
				binderExecutor);
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<JavaCursor> asUncheckedJavaCursorAsync() {
		return asUncheckedRawCursorAsync().transform(javaCursorFactory::apply, binderExecutor);
	}

	@Override
	public ClosingFuture<SqlCursor> asSqlCursorAsync() {
		return asRawCursorAsync().transform((closer, rawCursor) -> closer.eventuallyClose(sqlCursorFactory.apply(rawCursor),
																																											binderExecutor), binderExecutor);
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<SqlCursor> asUncheckedSqlCursorAsync() {
		return asUncheckedRawCursorAsync().transform(sqlCursorFactory::apply, binderExecutor);
	}

	@Override
	public ClosingFuture<Cursor> asRawCursorAsync() {
		return ClosingFuture.from(databaseAsync.get()).transform(
				(closer, db) -> closer.eventuallyClose(asUncheckedRawCursor(requireNonNull(db)), binderExecutor),
				databaseQueryExecutor);
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<Cursor> asUncheckedRawCursorAsync() {
		return databaseAsync.get().transform(this::asUncheckedRawCursor, databaseQueryExecutor);
	}

	@Override
	public ClosingFuture<CursorIterator<JavaCursor, Row>> asIteratorAsync() {
		return asJavaCursorAsync().transform(
				(closer, cursor) -> closer.eventuallyClose(new CursorIterator<>(rowCursorFactory, cursor), binderExecutor),
				binderExecutor);
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<CursorIterator<JavaCursor, Row>> asUncheckedIteratorAsync() {
		return asUncheckedJavaCursorAsync().transform(cursor -> new CursorIterator<>(rowCursorFactory, cursor),
																									binderExecutor);
	}
}
