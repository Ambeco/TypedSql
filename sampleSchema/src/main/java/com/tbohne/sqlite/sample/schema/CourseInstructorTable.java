package com.tbohne.sqlite.sample.schema;

import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.createTable.ColumnForeignKey;
import com.tbohne.sqlite.annotations.createTable.ColumnPrimaryKey;
import com.tbohne.sqlite.annotations.createTable.NotNull;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.Affinity;

/**
 * {@link com.tbohne.sqlite.sample.schema.CourseInstructorTableSql}
 *
 * <p> </p>From https://learn.microsoft.com/en-us/ef/ef6/resources/school-database
 */
@CreateTable(javaName = "CourseInstructor", columns = {
		@TableColumn(name = "CourseID",
								 affinity = Affinity.INTEGER,
								 primaryKey = @ColumnPrimaryKey(autoIncrement = true),
								 foreignKey = @ColumnForeignKey(OnsiteCourseTable.class)),
		@TableColumn(name = "PersonID",
								 affinity = Affinity.INTEGER,
								 foreignKey = @ColumnForeignKey(PersonTable.class),
								 notNull = @NotNull)})
public interface CourseInstructorTable {
}
