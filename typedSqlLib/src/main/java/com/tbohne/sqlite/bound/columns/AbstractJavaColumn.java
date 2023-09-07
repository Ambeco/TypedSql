package com.tbohne.sqlite.bound.columns;

import android.content.ContentValues;

//This is the base type of columns. Each @Binder generates one implementation
public interface AbstractJavaColumn<Table, JavaType, IsIndexed extends AbstractJavaColumn.IndexedFlag, IsSingleBinde extends AbstractJavaColumn.SingleBinderFlag> {

	int getSqlColumnCount();

	String getQualifiedSqlColumnName(int index);

	void toSql(JavaType javaValue, String[] results);

	void toSql(JavaType javaValue, ContentValues values);

	interface IndexedFlag {
	}

	interface IsIndexed extends IndexedFlag {
	}

	interface NotIndexed extends IndexedFlag {
	}

	interface SingleBinderFlag {
	}

	interface SingleBinder extends SingleBinderFlag {
	}

	interface MultiBinder extends SingleBinderFlag {
	}
}
