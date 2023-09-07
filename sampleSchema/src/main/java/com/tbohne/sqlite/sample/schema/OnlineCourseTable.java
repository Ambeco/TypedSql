package com.tbohne.sqlite.sample.schema;

import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.createTable.ColumnBinder;
import com.tbohne.sqlite.annotations.createTable.ColumnForeignKey;
import com.tbohne.sqlite.annotations.createTable.ColumnPrimaryKey;
import com.tbohne.sqlite.annotations.createTable.NotNull;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.binders.UriNotNullBinder;

/**
 * {@link com.tbohne.sqlite.sample.schema.OnlineCourseTableSql}
 *
 * <p> </p>From https://learn.microsoft.com/en-us/ef/ef6/resources/school-database
 */
@CreateTable(javaName = "OnlineCourse", columns = {
		@TableColumn(name = "CourseID",
								 affinity = Affinity.INTEGER,
								 primaryKey = @ColumnPrimaryKey,
								 foreignKey = @ColumnForeignKey(CourseTable.class)),
		@TableColumn(name = "URL",
								 affinity = Affinity.TEXT,
								 binder = @ColumnBinder(UriNotNullBinder.class),
								 notNull = @NotNull)})
public interface OnlineCourseTable {
}
