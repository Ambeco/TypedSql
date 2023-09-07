package com.tbohne.sqlite.sample.schema;

import com.tbohne.sqlite.RowId;
import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.createTable.ColumnBinder;
import com.tbohne.sqlite.annotations.createTable.ColumnForeignKey;
import com.tbohne.sqlite.annotations.createTable.ColumnPrimaryKey;
import com.tbohne.sqlite.annotations.createTable.NotNull;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.binders.RowIdBinder;

import javax.inject.Inject;

/**
 * {@link com.tbohne.sqlite.sample.schema.StudentGradeTableSql}
 *
 * <p> </p>From https://learn.microsoft.com/en-us/ef/ef6/resources/school-database
 */
@CreateTable(javaName = "StudentGrade", columns = {
		@TableColumn(name = "EnrollmentID",
								 affinity = Affinity.INTEGER,
								 primaryKey = @ColumnPrimaryKey(autoIncrement = true),
								 binder = @ColumnBinder(value = RowIdBinder.class, binderGenerics = {StudentGradeTable.class})),
		//TODO does this need an index?
		@TableColumn(name = "CourseID",
								 affinity = Affinity.INTEGER,
								 foreignKey = @ColumnForeignKey(CourseTable.class),
								 notNull = @NotNull),
		//TODO does this need an index?
		@TableColumn(name = "StudentID",
								 affinity = Affinity.INTEGER,
								 foreignKey = @ColumnForeignKey(PersonTable.class),
								 notNull = @NotNull), @TableColumn(name = "Grade", affinity = Affinity.REAL)})
public class StudentGradeTable {

//		private final Provider<StudentGradeTableSql> table;
//
//		@Inject
//		public StudentGradeTable(Provider<StudentGradeTableSql> table) {
//			this.table = table;
//		}
//
//		public StudentGradeTableSql.StudentGradeTableRow GetStudentGrades(RowId<PersonTable> StudentID) {
//			table.get().selectAllColumns()
//					.where(w -> w.withSlowTableScan(
//																	 "Its not worth indexing student ids because name lookup is rare and clearly
//																	 marked")
//															 w.whereIndexedEquals(w.StudentID(), StudentID))
//					.asSingleItemOrThrowSync();
//		}
}
