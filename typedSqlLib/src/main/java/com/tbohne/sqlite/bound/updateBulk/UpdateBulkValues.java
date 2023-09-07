package com.tbohne.sqlite.bound.updateBulk;

import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;

import java.util.function.Function;

public interface UpdateBulkValues<Table,
		UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		RowId
		> {
	UpdateBulkComplete<Table, RowId> where(IndexedWhereBuilderImpl where);

	UpdateBulkComplete<Table, RowId> where(
			Function<UnindexedWhereBuilderImpl, ? extends IndexedWhereBuilderImpl> where);
}
