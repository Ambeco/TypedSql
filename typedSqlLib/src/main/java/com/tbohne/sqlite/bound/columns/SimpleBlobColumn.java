package com.tbohne.sqlite.bound.columns;

import android.content.ContentValues;

import com.tbohne.sqlite.binders.SimpleColumnBinder;

import org.checkerframework.checker.nullness.qual.Nullable;

public class SimpleBlobColumn<Table, JavaType, Binder extends SimpleColumnBinder<JavaType, byte @Nullable []>, IsIndexed extends AbstractJavaColumn.IndexedFlag>
		implements AbstractJavaColumn<Table, JavaType, IsIndexed, AbstractJavaColumn.SingleBinder>
{
	private static final char[]
			HEX_CHARS =
			{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private final Binder binder;
	private final String sqlColumnName;

	public SimpleBlobColumn(Binder binder, String sqlColumnName) {
		this.binder = binder;
		this.sqlColumnName = sqlColumnName;
	}

	public static String toBlobExpression(byte @Nullable [] blob, StringBuilder sb) {
		if (blob == null) {
			return "null";
		}
		sb.delete(0, sb.length());
		sb.append("X\\'");
		for (int i = 0; i < blob.length; i++) {
			sb.append(HEX_CHARS[Byte.toUnsignedInt(blob[i]) >> 4]);
			sb.append(HEX_CHARS[Byte.toUnsignedInt(blob[i]) & 0x0F]);
		}
		sb.append("\\'");
		return sb.toString();
	}

	public static String toBlobExpression(byte[] blob) {
		return toBlobExpression(blob, new StringBuilder());
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

	public JavaType fromSql(byte @Nullable [] sqlValue) {
		return binder.fromSql(sqlValue);
	}

	@Override
	public void toSql(JavaType javaValue, @Nullable String[] results) {
		results[0] = toBlobExpression(binder.toSql(javaValue));
	}

	@Override
	public void toSql(JavaType javaValue, ContentValues values) {
		values.put(sqlColumnName, binder.toSql(javaValue));
	}

	@SuppressWarnings("unchecked")
	public <OtherTable> AbstractJavaColumn<OtherTable, JavaType, IsIndexed, AbstractJavaColumn.SingleBinder> asOtherTable() {
		return (AbstractJavaColumn<OtherTable, JavaType, IsIndexed, SingleBinder>) this;
	}
}
