package com.tbohne.sqlite.annotations.createTable;

import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.annotations.enums.Collation;

import java.lang.annotation.Target;

/*
 * Describes a column of a TableSchema.
 * The member should be `public static final` of a type `long, String, double, or byte[]`.
 * The member name represents the java facing name of the column.
 */
@Target({})
public @interface TableColumn {
	String name(); //the java name for this column

	String sqlName() default ""; //defaults to the java facing column name

	Affinity affinity();

	ColumnPrimaryKey primaryKey() default @ColumnPrimaryKey(false);

	NotNull notNull() default @NotNull(false);

	ColumnUnique unique() default @ColumnUnique(false);

	ColumnCheck[] checks() default {};

	DefaultValue defaultValue() default @DefaultValue("");

	ColumnCollation collation() default @ColumnCollation(Collation.UNSPECIFIED);

	ColumnForeignKey foreignKey() default @ColumnForeignKey(Void.class);

	// Binder class must have a public `SqlType binder#toSql(JavaType)` method.
	// Binder class must have a public `JavaType #fromSql(SqlType)` method.
	// SqlTypes must be marked @Nullable, unless the column is @NonNull.
	// Code is smaller/simpler if the methods are static.
	ColumnBinder binder() default @ColumnBinder();
}
