package com.tbohne.sqlite.binders;

//SqlType must usually be @Nullable
//This interface merely provides a simple type safe base for a multibinder to two sql columns, but you can implement
// a binder without inheriting from this.
public interface TwoColumnBinder<JavaType, SqlType1, SqlType2> {
	//must implement these two calls, but they may be static
	JavaType fromSql(SqlType1 columnValue1, SqlType2 columnValue2);

	Columns<SqlType1, SqlType2> toSql(JavaType javaValue);

	final class Columns<SqlType1, SqlType2> {
		private final SqlType1 columnValue1;
		private final SqlType2 columnValue2;

		public Columns(SqlType1 columnValue1, SqlType2 columnValue2) {
			this.columnValue1 = columnValue1;
			this.columnValue2 = columnValue2;
		}

		public SqlType1 getColumnValue1() {
			return columnValue1;
		}

		public SqlType2 getColumnValue2() {
			return columnValue2;
		}
	}
}
