package com.tbohne.sqlite.sample.schema;

import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.createTable.ColumnBinder;
import com.tbohne.sqlite.annotations.createTable.ColumnPrimaryKey;
import com.tbohne.sqlite.annotations.createTable.NotNull;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.binders.RowIdBinder;
import com.tbohne.sqlite.binders.instant.InstantJulianDoubleNonNullBinder;

/**
 * {@link com.tbohne.sqlite.sample.schema.PersonTableSql}
 *
 * <p> </p>From https://learn.microsoft.com/en-us/ef/ef6/resources/school-database
 */
@CreateTable(javaName = "Person", columns = {
		@TableColumn(name = "PersonId",
								 affinity = Affinity.INTEGER,
								 primaryKey = @ColumnPrimaryKey(autoIncrement = true),
								 binder = @ColumnBinder(value = RowIdBinder.class, binderGenerics = {PersonTable.class})),
		@TableColumn(name = "LastName", affinity = Affinity.TEXT, notNull = @NotNull),
		@TableColumn(name = "FirstName", affinity = Affinity.TEXT, notNull = @NotNull),
		@TableColumn(name = "HireDate",
								 affinity = Affinity.REAL,
								 binder = @ColumnBinder(InstantJulianDoubleNonNullBinder.class)),
		@TableColumn(name = "EnrollmentDate",
								 affinity = Affinity.REAL,
								 binder = @ColumnBinder(InstantJulianDoubleNonNullBinder.class)),
		@TableColumn(name = "Discriminator", affinity = Affinity.TEXT, notNull = @NotNull)})
public class PersonTable {

	//	private final Provider<OfficeAssignmentTableSql> table;
	//
	//	@Inject
	//	public PersonTable(Provider<OfficeAssignmentTableSql> table) {
	//		this.table = table;
	//	}
	//
	//	public void DeletePerson(RowId<PersonTable> PersonID) {
	//		table.delete(personId);
	//	}
	//
	//	public void UpdatePerson(
	//			RowId<PersonTable> PersonID,
	//			String LastName,
	//			String FirstName,
	//			Instant HireDate,
	//			Instant EnrollmentDate,
	//			String Discriminator) {
	//		table.get().newRowUpdater()
	//				.set(table.LastName(), LastName)
	//				.set(table.FirstName(), FirstName)
	//				.set(table.HireDate(), HireDate)
	//				.set(table.EnrollmentDate(), EnrollmentDate)
	//				.set(table.Discriminator(), Discriminator)
	//				.update(PersonID);
	//	}
	//
	//	public RowId<PersonTable> InsertPerson(
	//			String LastName,
	//			String FirstName,
	//			Instant HireDate,
	//			Instant EnrollmentDate,
	//			String Discriminator) {
	//		return table.get().newRowInserter()
	//				.set(table.LastName(), LastName)
	//				.set(table.FirstName(), FirstName)
	//				.set(table.HireDate(), HireDate)
	//				.set(table.EnrollmentDate(), EnrollmentDate)
	//				.set(table.Discriminator(), Discriminator)
	//				.insert();
	//	}
}
