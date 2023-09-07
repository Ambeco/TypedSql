package com.tbohne.sqlite.bound.insert;


import android.database.sqlite.SQLiteDatabase;

import com.google.common.util.concurrent.FluentFuture;
import com.tbohne.sqlite.annotations.enums.ConflictAction;
import com.tbohne.sqlite.binders.SimpleColumnBinder;
import com.tbohne.sqlite.bound.WriteValues;
import com.tbohne.sqlite.bound.columns.AbstractJavaColumn;
import com.tbohne.sqlite.bound.columns.SimpleLongNonNullColumn;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

public class AbstractInsert<Table, InsertValues extends WriteValues, RowIdBinder extends SimpleColumnBinder<RowId,
		Long>, RowId>
		implements Insert<Table, InsertValues, RowId>,
							 InsertWithConflict<Table, InsertValues, RowId>,
							 InsertWithValues<Table, RowId>
{
	private final int[] CONFLICT_MAP = {
			SQLiteDatabase.CONFLICT_NONE,
			SQLiteDatabase.CONFLICT_ROLLBACK,
			SQLiteDatabase.CONFLICT_ABORT,
			SQLiteDatabase.CONFLICT_FAIL,
			SQLiteDatabase.CONFLICT_IGNORE,
			SQLiteDatabase.CONFLICT_REPLACE
	};

	protected final Supplier<InsertValues> insertValuesBuilder;
	protected final Supplier<String> nonIdColumnName;
	protected final SimpleLongNonNullColumn<Table, RowId, RowIdBinder, AbstractJavaColumn.IsIndexed> rowIdColumn;

	protected final Executor databaseQueryExecutor;
	protected final Executor binderExecutor;

	protected final Supplier<SQLiteDatabase> databaseSync;
	protected final Supplier<FluentFuture<SQLiteDatabase>> databaseAsync;

	protected final String table;

	protected ConflictAction conflictAction = ConflictAction.UNSPECIFIED;
	protected @MonotonicNonNull InsertValues values;

	protected AbstractInsert(
			Supplier<InsertValues> insertValuesBuilder,
			Supplier<String> nonIdColumnName,
			SimpleLongNonNullColumn<Table, RowId, RowIdBinder, AbstractJavaColumn.IsIndexed> rowIdColumn,
			Executor databaseQueryExecutor,
			Executor binderExecutor,
			Supplier<SQLiteDatabase> databaseSync,
			Supplier<FluentFuture<SQLiteDatabase>> databaseAsync,
			String table)
	{
		this.insertValuesBuilder = insertValuesBuilder;
		this.nonIdColumnName = nonIdColumnName;
		this.rowIdColumn = rowIdColumn;

		this.databaseQueryExecutor = databaseQueryExecutor;
		this.binderExecutor = binderExecutor;

		this.databaseSync = databaseSync;
		this.databaseAsync = databaseAsync;

		this.table = table;
	}

	@Override
	public InsertWithConflict<Table, InsertValues, RowId> or(ConflictAction onConflict) {
		if (conflictAction != ConflictAction.UNSPECIFIED)
			throw new IllegalStateException("setting onConflictAction twice");
		if (onConflict == ConflictAction.UNSPECIFIED)
			throw new IllegalArgumentException("cannot specify UNSPECIFIED");
		conflictAction = onConflict;
		return this;
	}

	@Override
	public InsertWithValues<Table, RowId> values(InsertValues insertValues) {
		if (this.values != null)
			throw new IllegalStateException("setting values twice");
		values = insertValues;
		return this;
	}

	@Override
	public InsertWithValues<Table, RowId> values(Function<InsertValues, InsertValues> insertValuesInsertValuesFunction) {
		if (this.values != null)
			throw new IllegalStateException("setting values twice");
		values = insertValuesInsertValuesFunction.apply(insertValuesBuilder.get());
		return this;
	}

	protected long insert(SQLiteDatabase db) {
		return db.insertWithOnConflict(table, nonIdColumnName.get(), values.getValues(),
																										CONFLICT_MAP[conflictAction.ordinal()]);
	}

	@Override
	public RowId sync() {
		return rowIdColumn.fromSql(insert(databaseSync.get()));
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public FluentFuture<RowId> async() {
		return databaseAsync.get()
				.transform(this::insert, databaseQueryExecutor)
				.transform(rowIdColumn::fromSql, binderExecutor);
	}
}
