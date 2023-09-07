package com.tbohne.sqlite.bound.deleteBulk;

import com.tbohne.sqlite.bound.WriteValues;
import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;

import java.util.function.Function;

public interface DeleteBulk<Table,
		UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		RowId> {
	DeleteBulkComplete<Table, RowId> where(IndexedWhereBuilderImpl where);

	DeleteBulkComplete<Table, RowId> where(
			Function<UnindexedWhereBuilderImpl, ? extends IndexedWhereBuilderImpl> where);
}
