package com.tbohne.sqlite.bound.select;

import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;

import java.util.function.Function;

public interface MappableSelectable<Table, UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>, SqlCursor extends AbstractTypedCursorWrapper,
		JavaCursor extends AbstractTypedCursorWrapper,
		Row,
		RowId>
		extends Selectable<Table, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, SqlCursor, JavaCursor, Row>,
						MappableSelection<SqlCursor, JavaCursor, Row, RowId>
{

	MappableSelection<SqlCursor, JavaCursor, Row, RowId> where(IndexedWhereBuilderImpl where);

	MappableSelection<SqlCursor, JavaCursor, Row, RowId> where(
			Function<UnindexedWhereBuilderImpl, ? extends IndexedWhereBuilderImpl> where);

	MappableSelection<SqlCursor, JavaCursor, Row, RowId> whereRowIdEquals(RowId id);
}
