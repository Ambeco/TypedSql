package com.tbohne.sqlite.bound.updateUnique;

import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;

import java.util.function.Function;

public interface UpdateUniqueValues<Table,
		UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>
		> extends UpdateUniqueComplete<Table>
{
	UpdateUniqueComplete<Table> where(IndexedWhereBuilderImpl where);

	UpdateUniqueComplete<Table> where(
			Function<IndexedWhereBuilderImpl, IndexedWhereBuilderImpl> where);
}
