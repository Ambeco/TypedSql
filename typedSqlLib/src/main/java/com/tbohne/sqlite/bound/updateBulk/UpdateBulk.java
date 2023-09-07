package com.tbohne.sqlite.bound.updateBulk;

import com.tbohne.sqlite.bound.WriteValues;
import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;

import java.util.function.Function;

public interface UpdateBulk<Table, UpdateValues extends WriteValues,
		UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		RowId> {
	UpdateBulkValues<Table, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, RowId> set(UpdateValues insertValues);
	UpdateBulkValues<Table, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, RowId> set(Function<UpdateValues,
			UpdateValues> insertValuesFunction);
}
