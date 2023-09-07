package com.tbohne.sqlite.bound.deleteUnique;

import static com.tbohne.sqlite.bound.DeleteResult.ROW_FAILED_CONDITIONS;
import static com.tbohne.sqlite.bound.DeleteResult.ROW_NOT_FOUND;
import static com.tbohne.sqlite.bound.DeleteResult.DELETED;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.util.concurrent.FluentFuture;
import com.tbohne.sqlite.bound.DeleteResult;
import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;
import com.tbohne.sqlite.exceptions.RowNotFoundException;

import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

public class AbstractDeleteUnique<Table,
		UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		RowId>
		implements DeleteUnique<Table, IndexedWhereBuilderImpl>,
							 DeleteUniqueComplete<Table>
{
	protected final Supplier<UnindexedWhereBuilderImpl> whereBuilder;

	protected final Executor databaseQueryExecutor;
	protected final Executor binderExecutor;

	protected final Supplier<SQLiteDatabase> databaseSync;
	protected final Supplier<FluentFuture<SQLiteDatabase>> databaseAsync;

	protected final String table;
	protected final RowId rowId;
	protected IndexedWhereBuilderImpl rowIdSelectionAndArgs;
	protected IndexedWhereBuilderImpl selectionAndArgs;

	protected AbstractDeleteUnique(
			Supplier<UnindexedWhereBuilderImpl> whereBuilder,
			Executor databaseQueryExecutor,
			Executor binderExecutor,
			Supplier<SQLiteDatabase> databaseSync,
			Supplier<FluentFuture<SQLiteDatabase>> databaseAsync,
			String table,
			RowId rowId,
			IndexedWhereBuilderImpl rowIdSelectionAndArgs)
	{
		this.whereBuilder = whereBuilder;

		this.databaseQueryExecutor = databaseQueryExecutor;
		this.binderExecutor = binderExecutor;

		this.databaseSync = databaseSync;
		this.databaseAsync = databaseAsync;

		this.table = table;
		this.rowId = rowId;
		this.rowIdSelectionAndArgs = rowIdSelectionAndArgs;
		this.selectionAndArgs = whereBuilder.get().and(rowIdSelectionAndArgs);
	}

	@Override
	public DeleteUniqueComplete<Table> where(IndexedWhereBuilderImpl where) {
		selectionAndArgs.and(where);
		return this;
	}

	@Override
	public DeleteUniqueComplete<Table> where(Function<IndexedWhereBuilderImpl, IndexedWhereBuilderImpl> function) {
		selectionAndArgs.and(function.apply(selectionAndArgs));
		return this;
	}

	protected boolean deleteWithoutDetails(SQLiteDatabase db) {
		return db.delete(table, selectionAndArgs.build(), selectionAndArgs.getArgs()) == 1;
	}

	protected DeleteResult deleteWithDetails(SQLiteDatabase db) {
		db.beginTransaction();
		DeleteResult result;
		try {
			if (deleteWithoutDetails(db)) {
				result = DELETED;
			} else if (DatabaseUtils.queryNumEntries(db, table, rowIdSelectionAndArgs.build(),
																							 rowIdSelectionAndArgs.getArgs()) == 1) {
				//TODO only query row id - ignore other selection criteria
				result = ROW_FAILED_CONDITIONS;
			} else {
				result = ROW_NOT_FOUND;
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (RuntimeException e) {
			db.endTransaction();
			throw e;
		}
		return result;
	}

	protected boolean matchedCondition(DeleteResult result) {
		switch (result) {
			case DELETED:
				return true;
			case ROW_FAILED_CONDITIONS:
				return false;
			case ROW_NOT_FOUND:
				throw new RowNotFoundException("delete of row that does not exist");
			default:
				throw new IllegalStateException("result " + result + " is not a valid value");
		}
	}

	@Override
	public boolean withoutDetailsSync() {
		return deleteWithoutDetails(databaseSync.get());
	}

	@Override
	public DeleteResult withDetailsSync() {
		return deleteWithDetails(databaseSync.get());
	}

	@Override
	public boolean existingRowSync() {
		return matchedCondition(withDetailsSync());
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<Boolean> withoutDetailsAsync() {
		return databaseAsync.get().transform(this::deleteWithoutDetails, databaseQueryExecutor);
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<DeleteResult> withDetailsAsync() {
		return databaseAsync.get().transform(this::deleteWithDetails, databaseQueryExecutor);
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<Boolean> existingRowAsync() {
		return withDetailsAsync().transform(this::matchedCondition, binderExecutor);
	}
}
