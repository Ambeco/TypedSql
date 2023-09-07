package com.tbohne.sqlite.binders;

//SqlType must usually be @Nullable
//This interface merely provides a simple type safe base, but you can implement a binder without inheriting from this.
//In fact, multibinders have subtly different methods, so can't inherit from any specific interface.
public interface SimpleColumnBinder<JavaType, SqlType> {
	//must implement these two calls, but they may be static
	JavaType fromSql(SqlType columnValue);

	SqlType toSql(JavaType javaValue);
}
