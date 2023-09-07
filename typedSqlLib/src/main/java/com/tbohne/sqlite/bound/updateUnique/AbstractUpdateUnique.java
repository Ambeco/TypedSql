package com.tbohne.sqlite.bound.updateUnique;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_ABORT;
import static com.tbohne.sqlite.bound.UpdateResult.ROW_FAILED_CONDITIONS;
import static com.tbohne.sqlite.bound.UpdateResult.ROW_NOT_FOUND;
import static com.tbohne.sqlite.bound.UpdateResult.UPDATED;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.util.concurrent.FluentFuture;
import com.tbohne.sqlite.bound.UpdateResult;
import com.tbohne.sqlite.bound.WriteValues;
import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;
import com.tbohne.sqlite.exceptions.RowNotFoundException;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

public class AbstractUpdateUnique<Table,
		UpdateValues extends WriteValues,
		UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>, RowId>
		implements UpdateUnique<Table, UpdateValues, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl>,
							 UpdateUniqueValues<Table, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl>,
							 UpdateUniqueComplete<Table>
{
	protected final Supplier<UpdateValues> UpdateValuesBuilder;
	protected final Supplier<UnindexedWhereBuilderImpl> whereBuilder;

	protected final Executor databaseQueryExecutor;
	protected final Executor binderExecutor;

	protected final Supplier<SQLiteDatabase> databaseSync;
	protected final Supplier<FluentFuture<SQLiteDatabase>> databaseAsync;

	protected final String table;
	protected final RowId rowId;
	protected @MonotonicNonNull UpdateValues values;
	protected IndexedWhereBuilderImpl rowIdSelectionAndArgs;
	protected IndexedWhereBuilderImpl selectionAndArgs;

	protected AbstractUpdateUnique(
			Supplier<UpdateValues> UpdateValuesBuilder,
			Supplier<UnindexedWhereBuilderImpl> whereBuilder,
			Executor databaseQueryExecutor,
			Executor binderExecutor,
			Supplier<SQLiteDatabase> databaseSync,
			Supplier<FluentFuture<SQLiteDatabase>> databaseAsync,
			String table,
			RowId rowId,
			IndexedWhereBuilderImpl rowIdSelectionAndArgs)
	{
		this.UpdateValuesBuilder = UpdateValuesBuilder;
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
	public UpdateUniqueValues<Table, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl> set(UpdateValues UpdateValues) {
		if (values != null) {
			throw new IllegalStateException("update already set values once");
		}
		values = UpdateValues;
		return this;
	}

	@Override
	public UpdateUniqueValues<Table, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl> set(Function<UpdateValues,
			UpdateValues> UpdateValuesFunction) {
		return set(UpdateValuesFunction.apply(UpdateValuesBuilder.get()));
	}

	@Override
	public UpdateUniqueComplete<Table> where(IndexedWhereBuilderImpl where) {
		selectionAndArgs.and(where);
		return this;
	}

	@Override
	public UpdateUniqueComplete<Table> where(Function<IndexedWhereBuilderImpl, IndexedWhereBuilderImpl> function) {
		selectionAndArgs.and(function.apply(whereBuilder.get().withSlowTableScan("already indexed")));
		return this;
	}

	protected boolean updateWithoutDetails(SQLiteDatabase db) {
		return db.updateWithOnConflict(table,
																	 values.getValues(),
																	 selectionAndArgs.build(),
																	 selectionAndArgs.getArgs(),
																	 CONFLICT_ABORT) == 1;
	}

	protected UpdateResult updateWithDetails(SQLiteDatabase db) {
		db.beginTransaction();
		UpdateResult result;
		try {
			if (updateWithoutDetails(db)) {
				result = UPDATED;
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

	protected boolean matchedCondition(UpdateResult result) {
		switch (result) {
			case UPDATED:
				return true;
			case ROW_FAILED_CONDITIONS:
				return false;
			case ROW_NOT_FOUND:
				throw new RowNotFoundException("update of row that does not exist");
			default:
				throw new IllegalStateException("result " + result + " is not a valid value");
		}
	}

	@Override
	public boolean withoutDetailsSync() {
		return updateWithoutDetails(databaseSync.get());
	}

	@Override
	public UpdateResult withDetailsSync() {
		return updateWithDetails(databaseSync.get());
	}

	@Override
	public boolean existingRowSync() {
		return matchedCondition(withDetailsSync());
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<Boolean> withoutDetailsAsync() {
		return databaseAsync.get().transform(this::updateWithoutDetails, databaseQueryExecutor);
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<UpdateResult> withDetailsAsync() {
		return databaseAsync.get().transform(this::updateWithDetails, databaseQueryExecutor);
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<Boolean> existingRowAsync() {
		return withDetailsAsync().transform(this::matchedCondition, binderExecutor);
	}
}
