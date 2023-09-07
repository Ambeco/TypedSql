package com.tbohne.sqlite.bound.deleteUnique;

import com.tbohne.sqlite.bound.WriteValues;
import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;

import java.util.function.Function;

public interface DeleteUnique<Table,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>>
		extends DeleteUniqueComplete<Table>
{
	DeleteUniqueComplete<Table> where(IndexedWhereBuilderImpl where);

	DeleteUniqueComplete<Table> where(Function<IndexedWhereBuilderImpl, IndexedWhereBuilderImpl> where);
}