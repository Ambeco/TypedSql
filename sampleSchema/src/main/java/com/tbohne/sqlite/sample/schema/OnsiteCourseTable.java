package com.tbohne.sqlite.sample.schema;

import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.createTable.ColumnBinder;
import com.tbohne.sqlite.annotations.createTable.ColumnForeignKey;
import com.tbohne.sqlite.annotations.createTable.ColumnPrimaryKey;
import com.tbohne.sqlite.annotations.createTable.NotNull;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.binders.duration.DurationSecondsDoubleBinder;
import com.tbohne.sqlite.sample.datatypes.DaysSelectedBinder;

/**
 * {@link com.tbohne.sqlite.sample.schema.OnsiteCourseTableSql}
 *
 * <p> </p>From https://learn.microsoft.com/en-us/ef/ef6/resources/school-database
 */
@CreateTable(javaName = "OnsiteCourse", columns = {
		@TableColumn(name = "CourseID",
								 affinity = Affinity.INTEGER,
								 primaryKey = @ColumnPrimaryKey,
								 foreignKey = @ColumnForeignKey(CourseTable.class)),
		@TableColumn(name = "Location", affinity = Affinity.TEXT, notNull = @NotNull),
		@TableColumn(name = "Days",
								 affinity = Affinity.TEXT,
								 binder = @ColumnBinder(DaysSelectedBinder.class),
								 notNull = @NotNull),
		@TableColumn(name = "Time", affinity = Affinity.REAL, binder = @ColumnBinder(DurationSecondsDoubleBinder.class))})
public interface OnsiteCourseTable {
}
