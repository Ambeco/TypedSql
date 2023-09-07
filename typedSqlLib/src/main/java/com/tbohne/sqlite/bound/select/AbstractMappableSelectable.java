package com.tbohne.sqlite.bound.select;

import static java.util.Objects.requireNonNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArrayMap;

import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.FluentFuture;
import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.columns.AbstractJavaColumn;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Provider;

public abstract class AbstractMappableSelectable<Table,
		UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		SqlCursor extends AbstractTypedCursorWrapper,
		JavaCursor extends AbstractTypedCursorWrapper,
		Row,
		RowId>
		extends AbstractSelectable<Table, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, SqlCursor, JavaCursor, Row>
		implements MappableSelectable<Table, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, SqlCursor, JavaCursor, Row, RowId>
{
	private final Function<Row, RowId> rowIdMapper;
	private final AbstractJavaColumn<Table, RowId, AbstractJavaColumn.IsIndexed, ?> idColumn;


	protected AbstractMappableSelectable(
			Supplier<UnindexedWhereBuilderImpl> whereBuilder,
			Function<Cursor, SqlCursor> sqlCursorFactory,
			Function<Cursor, JavaCursor> javaCursorFactory,
			Function<JavaCursor, Row> rowCursorFactory,
			Function<Row, RowId> rowIdMapper,
			Executor databaseQueryExecutor,
			Executor binderExecutor,
			Supplier<SQLiteDatabase> databaseSync,
			Supplier<FluentFuture<SQLiteDatabase>> databaseAsync,
			AbstractJavaColumn<Table, RowId, AbstractJavaColumn.IsIndexed, ?> idColumn,
			boolean distinct,
			String table,
			String[] columns,
			IndexedWhereBuilderImpl selectionAndArgs,
			@Nullable String groupBy)
	{
		super(whereBuilder,
					sqlCursorFactory,
					javaCursorFactory,
					rowCursorFactory,
					databaseQueryExecutor,
					binderExecutor,
					databaseSync,
					databaseAsync,
					distinct,
					table,
					columns,
					selectionAndArgs,
					groupBy);
		this.rowIdMapper = rowIdMapper;
		this.idColumn = idColumn;
	}

	@Override
	public MappableSelection<SqlCursor, JavaCursor, Row, RowId> where(IndexedWhereBuilderImpl whereClause) {
		super.where(whereClause);
		return this;
	}

	@Override
	public MappableSelection<SqlCursor, JavaCursor, Row, RowId> where(
			Function<UnindexedWhereBuilderImpl, ? extends IndexedWhereBuilderImpl> whereFunction)
	{
		super.where(whereFunction);
		return this;
	}

	@Override
	public MappableSelection<SqlCursor, JavaCursor, Row, RowId> whereRowIdEquals(RowId id) {
		selectionAndArgs.whereEquals(idColumn, id);
		return this;
	}

	@Override
	public ArrayMap<RowId, Row> asMapSync() {
		try (JavaCursor cursor = asJavaCursorSync()) {
			ArrayMap<RowId, Row> arrayMap = new ArrayMap<>(cursor.getCount());
			while (cursor.moveToNext()) {
				Row row = rowCursorFactory.apply(cursor);
				arrayMap.put(rowIdMapper.apply(row), row);
			}
			return arrayMap;
		}
	}

	@Override
	public FluentFuture<ArrayMap<RowId, Row>> asMapAsync() {
		return asJavaCursorAsync().transform((closer, cursor) -> {
			ArrayMap<RowId, Row> arrayMap = new ArrayMap<>(requireNonNull(cursor).getCount());
			while (cursor.moveToNext()) {
				Row row = rowCursorFactory.apply(cursor);
				arrayMap.put(rowIdMapper.apply(row), row);
			}
			return arrayMap;
		}, binderExecutor).finishToFuture();
	}
}
