package com.tbohne.sqlite.bound.select;

import android.util.ArrayMap;

import com.google.common.util.concurrent.FluentFuture;

public interface MappableSelection<SqlCursor extends AbstractTypedCursorWrapper, JavaCursor extends AbstractTypedCursorWrapper, Row, RowId>
		extends Selection<SqlCursor, JavaCursor, Row>
{

	ArrayMap<RowId, Row> asMapSync();

	FluentFuture<ArrayMap<RowId, Row>> asMapAsync();
}
