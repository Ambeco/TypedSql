package com.tbohne.sqlite.bound.updateUnique;

import com.tbohne.sqlite.bound.WriteValues;
import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;

import java.util.function.Function;

public interface UpdateUnique<Table, UpdateValues extends WriteValues,
		UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>> {
	UpdateUniqueValues<Table, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl> set(UpdateValues insertValues);
	UpdateUniqueValues<Table, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl> set(Function<UpdateValues,
			UpdateValues> insertValuesFunction);
}
