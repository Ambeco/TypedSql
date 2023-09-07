package com.tbohne.sqlite.sample.schema;

import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.createTable.ColumnBinder;
import com.tbohne.sqlite.annotations.createTable.ColumnForeignKey;
import com.tbohne.sqlite.annotations.createTable.ColumnPrimaryKey;
import com.tbohne.sqlite.annotations.createTable.NotNull;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.binders.RowIdBinder;

/**
 * {@link com.tbohne.sqlite.sample.schema.CourseTableSql}
 *
 * <p> </p>From https://learn.microsoft.com/en-us/ef/ef6/resources/school-database
 */
@CreateTable(javaName = "Course", columns = {
		@TableColumn(name = "CourseID",
								 affinity = Affinity.INTEGER,
								 primaryKey = @ColumnPrimaryKey,
								 binder = @ColumnBinder(value = RowIdBinder.class, binderGenerics = {CourseTable.class})),
		@TableColumn(name = "Title", affinity = Affinity.TEXT, notNull = @NotNull),
		@TableColumn(name = "Credits", affinity = Affinity.INTEGER, notNull = @NotNull),
		@TableColumn(name = "DepartmentId",
								 affinity = Affinity.INTEGER,
								 foreignKey = @ColumnForeignKey(DepartmentTable.class),
								 notNull = @NotNull)})
public interface CourseTable {
}
