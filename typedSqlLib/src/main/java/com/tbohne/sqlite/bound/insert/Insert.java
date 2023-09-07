package com.tbohne.sqlite.bound.insert;

import com.tbohne.sqlite.annotations.enums.ConflictAction;
import com.tbohne.sqlite.bound.WriteValues;
import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;

import java.util.function.Function;

public interface Insert<Table, InsertValues extends WriteValues, RowId> extends InsertWithConflict<Table, InsertValues, RowId> {
	InsertWithConflict<Table, InsertValues, RowId> or(ConflictAction onConflict);
}
