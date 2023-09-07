package com.tbohne.sqlite.bound.deleteBulk;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.util.concurrent.FluentFuture;
import com.tbohne.sqlite.binders.SimpleColumnBinder;
import com.tbohne.sqlite.bound.WriteValues;
import com.tbohne.sqlite.bound.columns.AbstractJavaColumn;
import com.tbohne.sqlite.bound.columns.SimpleLongNonNullColumn;
import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

public class AbstractDeleteBulk<Table,
		UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		RowIdBinder extends SimpleColumnBinder<RowId, Long>,
		RowId>
		implements DeleteBulk<Table, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, RowId>,
							 DeleteBulkComplete<Table, RowId>
{
	protected final Supplier<UnindexedWhereBuilderImpl> whereBuilder;
	protected final SimpleLongNonNullColumn<Table, RowId, RowIdBinder, AbstractJavaColumn.IsIndexed> rowIdColumn;

	protected final Executor databaseQueryExecutor;
	protected final Executor binderExecutor;

	protected final Supplier<SQLiteDatabase> databaseSync;
	protected final Supplier<FluentFuture<SQLiteDatabase>> databaseAsync;

	protected final String table;
	protected @MonotonicNonNull IndexedWhereBuilderImpl selectionAndArgs;

	protected AbstractDeleteBulk(
			Supplier<UnindexedWhereBuilderImpl> whereBuilder,
			SimpleLongNonNullColumn<Table, RowId, RowIdBinder, AbstractJavaColumn.IsIndexed> rowIdColumn,
			Executor databaseQueryExecutor,
			Executor binderExecutor,
			Supplier<SQLiteDatabase> databaseSync,
			Supplier<FluentFuture<SQLiteDatabase>> databaseAsync,
			String table,
			IndexedWhereBuilderImpl selectionAndArgs)
	{
		this.whereBuilder = whereBuilder;
		this.rowIdColumn = rowIdColumn;

		this.databaseQueryExecutor = databaseQueryExecutor;
		this.binderExecutor = binderExecutor;

		this.databaseSync = databaseSync;
		this.databaseAsync = databaseAsync;

		this.table = table;
		this.selectionAndArgs = selectionAndArgs;
	}

	@Override
	public DeleteBulkComplete<Table, RowId> where(IndexedWhereBuilderImpl where) {
		selectionAndArgs.and(where);
		return this;
	}

	@Override
	public DeleteBulkComplete<Table, RowId> where(Function<UnindexedWhereBuilderImpl,
			? extends IndexedWhereBuilderImpl> where) {
		selectionAndArgs.and(where.apply(whereBuilder.get()));
		return this;
	}

	protected int deleteWithoutDetails(SQLiteDatabase db) {
		return db.delete(table, selectionAndArgs.build(), selectionAndArgs.getArgs());
	}

	protected HashSet<RowId> deleteExistingRows(SQLiteDatabase db) {
		String[] columns = new String[1];
		columns[0] = rowIdColumn.getQualifiedSqlColumnName(0);
		Cursor cursor = null;
		db.beginTransaction();
		try {
			cursor = db.queryWithFactory(/*cursorFactory=*/null,
					/*cursorFactory=*/ false, table, columns, selectionAndArgs.build(), selectionAndArgs.getArgs(),
					/*groupBy=*/ null,
					/*having*/  null,
					/*orderBy*/ null,
					/*limit*/ null,
					/*cancellationSignal=*/ null);
			deleteWithoutDetails(db);
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (RuntimeException e) {
			db.endTransaction();
			if (cursor != null) cursor.close();
			throw e;
		}
		try (Cursor c = cursor) {
			HashSet<RowId> results = new HashSet<>();
			while(c.moveToNext())
				results.add(rowIdColumn.fromSql(c.getLong(0)));
			return results;
		}
	}

	@Override
	public int withoutDetailsSync() {
		return deleteWithoutDetails(databaseSync.get());
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<Integer> withoutDetailsAsync() {
		return databaseAsync.get().transform(this::deleteWithoutDetails, databaseQueryExecutor);
	}

	@Override
	public Set<RowId> withDetailsSync() {
		return deleteExistingRows(databaseSync.get());
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<Set<RowId>> withDetailsAsync() {
		return databaseAsync.get().transform(this::deleteExistingRows, databaseQueryExecutor);
	}
}
