package com.tbohne.sqlite.bound.select;

import android.database.Cursor;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.common.util.concurrent.ClosingFuture;
import com.google.common.util.concurrent.FluentFuture;
import com.google.errorprone.annotations.MustBeClosed;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

interface Selection<SqlCursor extends AbstractTypedCursorWrapper, JavaCursor extends AbstractTypedCursorWrapper, Row> {

	RawQueryParams getRawQueryParameters();

	@Nullable Row asNullableRowSync();

	FluentFuture<@Nullable Row> asNullableRowAsync();

	Optional<Row> asOptionalRowSync();

	FluentFuture<Optional<Row>> asOptionalRowAsync();

	Row asRowOrThrowSync();

	FluentFuture<Row> asRowOrThrowAsync();

	List<Row> asListSync();

	FluentFuture<ArrayList<Row>> asListAsync();

	Set<Row> asSetSync();

	FluentFuture<Set<Row>> asSetAsync();

	@RequiresApi(api = Build.VERSION_CODES.N)
	@MustBeClosed
	Stream<Row> asStreamSync();

	@RequiresApi(api = Build.VERSION_CODES.N)
	Stream<Row> asUncheckedStreamSync();

	//	@RequiresApi(api = Build.VERSION_CODES.N)
	//  ClosingFuture<Stream<Row>> asStreamAsync();

	@RequiresApi(api = Build.VERSION_CODES.N)
	FluentFuture<Stream<Row>> asUncheckedStreamAsync();

	@MustBeClosed
	JavaCursor asJavaCursorSync();

	ClosingFuture<JavaCursor> asJavaCursorAsync();

	JavaCursor asUncheckedJavaCursorSync();

	FluentFuture<JavaCursor> asUncheckedJavaCursorAsync();

	@MustBeClosed
	SqlCursor asSqlCursorSync();

	ClosingFuture<SqlCursor> asSqlCursorAsync();

	SqlCursor asUncheckedSqlCursorSync();

	FluentFuture<SqlCursor> asUncheckedSqlCursorAsync();

	@MustBeClosed
	Cursor asRawCursorSync();

	ClosingFuture<Cursor> asRawCursorAsync();

	Cursor asUncheckedRawCursorSync();

	FluentFuture<Cursor> asUncheckedRawCursorAsync();

	@MustBeClosed
	CursorIterator<JavaCursor, Row> asIteratorSync();

	CursorIterator<JavaCursor, Row> asUncheckedIteratorSync();

	ClosingFuture<CursorIterator<JavaCursor, Row>> asIteratorAsync();

	FluentFuture<CursorIterator<JavaCursor, Row>> asUncheckedIteratorAsync();

	//These methods let you use the typedSql Selection code, but bypass the easySql query itself
	interface RawQueryParams {
		boolean getRawDistinct();

		String getRawTables();

		String[] getColumns();

		@Nullable String getRawSelectionString();

		@Nullable String[] getRawSelectionArgs();

		@Nullable String getRawGroupBy();

		@Nullable String getRawHaving();

		@Nullable String getRawOrderBy();

		@Nullable String getLimit();
	}

	class CursorIterator<JavaCursor extends AbstractTypedCursorWrapper, Row>
			implements Iterator<Row>, Closeable
	{
		private final Function<JavaCursor, Row> rowCursorFactory;
		private final JavaCursor cursor;

		@MustBeClosed
		CursorIterator(Function<JavaCursor, Row> rowCursorFactory, JavaCursor cursor) {
			this.rowCursorFactory = rowCursorFactory;
			this.cursor = cursor;
		}

		public int getCount() {
			return cursor.getCount();
		}

		@Override
		public boolean hasNext() {
			return !cursor.isLast();
		}

		@Override
		public Row next() {
			cursor.moveToNext();
			return rowCursorFactory.apply(cursor);
		}

		@Override
		public void close() {
			cursor.close();
		}
	}
}
