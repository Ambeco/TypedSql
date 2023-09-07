package com.tbohne.sqlite.binders;

//SqlType must usually be @Nullable
//This interface merely provides a simple type safe base for a multibinder to two sql columns, but you can implement
// a binder without inheriting from this.
public interface ThreeColumnBinder<JavaType, SqlType1, SqlType2, SqlType3> {
	//must implement these two calls, but they may be static
	JavaType fromSql(SqlType1 columnValue1, SqlType2 columnValue2, SqlType3 columnValue3);

	Columns<SqlType1, SqlType2, SqlType3> toSql(JavaType javaValue);

	final class Columns<SqlType1, SqlType2, SqlType3> {
		private final SqlType1 columnValue1;
		private final SqlType2 columnValue2;
		private final SqlType3 columnValue3;

		public Columns(SqlType1 columnValue1, SqlType2 columnValue2, SqlType3 columnValue3) {
			this.columnValue1 = columnValue1;
			this.columnValue2 = columnValue2;
			this.columnValue3 = columnValue3;
		}

		public SqlType1 getColumnValue1() {
			return columnValue1;
		}

		public SqlType2 getColumnValue2() {
			return columnValue2;
		}

		public SqlType3 getColumnValue3() {
			return columnValue3;
		}
	}
}
