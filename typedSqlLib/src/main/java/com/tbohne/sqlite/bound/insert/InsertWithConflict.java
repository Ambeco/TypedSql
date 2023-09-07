package com.tbohne.sqlite.bound.insert;

import com.tbohne.sqlite.bound.WriteValues;

import java.util.function.Function;

public interface InsertWithConflict<Table, InsertValues extends WriteValues, RowId> {
	InsertWithValues<Table, RowId> values(InsertValues insertValues);
	InsertWithValues<Table, RowId> values(
			Function<InsertValues,
					InsertValues> insertValuesFunction);
}