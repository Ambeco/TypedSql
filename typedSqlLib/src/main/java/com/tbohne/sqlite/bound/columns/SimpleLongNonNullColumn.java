package com.tbohne.sqlite.bound.columns;

import android.content.ContentValues;

import com.tbohne.sqlite.binders.SimpleColumnBinder;

import org.checkerframework.checker.nullness.qual.Nullable;

public class SimpleLongNonNullColumn<Table, JavaType, Binder extends SimpleColumnBinder<JavaType, Long>, IsIndexed extends AbstractJavaColumn.IndexedFlag>
		implements AbstractJavaColumn<Table, JavaType, IsIndexed, AbstractJavaColumn.SingleBinder>
{
	private final Binder binder;
	private final String sqlColumnName;

	public SimpleLongNonNullColumn(Binder binder, String sqlColumnName) {
		this.binder = binder;
		this.sqlColumnName = sqlColumnName;
	}

	@Override
	public int getSqlColumnCount() {
		return 1;
	}

	@Override
	public String getQualifiedSqlColumnName(int index) {
		if (index != 0) {
			throw new IllegalArgumentException("Invalid index " + index + " passed to JavacColumn with only 1 sqlColumn");
		}
		return sqlColumnName;
	}

	public JavaType fromSql(Long sqlValue) {
		return binder.fromSql(sqlValue);
	}

	@Override
	public void toSql(JavaType javaValue, @Nullable String[] results) {
		results[0] = String.valueOf(binder.toSql(javaValue));
	}

	@Override
	public void toSql(JavaType javaValue, ContentValues values) {
		values.put(sqlColumnName, binder.toSql(javaValue));
	}

	@SuppressWarnings("unchecked")
	public <OtherTable> SimpleLongNonNullColumn<OtherTable, JavaType, Binder, IsIndexed> asOtherTable() {
		return (SimpleLongNonNullColumn<OtherTable, JavaType, Binder, IsIndexed>) this;
	}
}
