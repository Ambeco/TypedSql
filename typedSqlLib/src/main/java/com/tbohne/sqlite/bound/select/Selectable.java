package com.tbohne.sqlite.bound.select;

import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;

import java.util.function.Function;

public interface Selectable<Table, UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>, SqlCursor extends AbstractTypedCursorWrapper,
		JavaCursor extends AbstractTypedCursorWrapper,
		Row>
		extends Selection<SqlCursor, JavaCursor, Row>
{
	Selection<SqlCursor, JavaCursor, Row> where(IndexedWhereBuilderImpl where);

	Selection<SqlCursor, JavaCursor, Row> where(
			Function<UnindexedWhereBuilderImpl, ? extends IndexedWhereBuilderImpl> where);
}
