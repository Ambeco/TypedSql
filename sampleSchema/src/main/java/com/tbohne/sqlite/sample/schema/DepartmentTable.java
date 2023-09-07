package com.tbohne.sqlite.sample.schema;

import com.tbohne.sqlite.RowId;
import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.Selection;
import com.tbohne.sqlite.annotations.createTable.ColumnBinder;
import com.tbohne.sqlite.annotations.createTable.ColumnForeignKey;
import com.tbohne.sqlite.annotations.createTable.ColumnPrimaryKey;
import com.tbohne.sqlite.annotations.createTable.DefaultValue;
import com.tbohne.sqlite.annotations.createTable.NotNull;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.annotations.enums.ForeignKeyAction;
import com.tbohne.sqlite.annotations.enums.JoinOperator;
import com.tbohne.sqlite.annotations.query.SelectColumns;
import com.tbohne.sqlite.annotations.query.SelectExpression;
import com.tbohne.sqlite.annotations.query.SelectJoin;
import com.tbohne.sqlite.annotations.query.SelectTable;
import com.tbohne.sqlite.binders.RowIdBinder;
import com.tbohne.sqlite.binders.instant.InstantJulianDoubleNonNullBinder;
import com.tbohne.sqlite.bound.DeleteResult;
import com.tbohne.sqlite.bound.UpdateResult;
import com.tbohne.sqlite.exceptions.RowNotFoundException;
import com.tbohne.sqlite.generalProjections.StringProjection;
import com.tbohne.sqlite.sample.datatypes.Money;
import com.tbohne.sqlite.sample.datatypes.MoneyBinder;
import com.tbohne.sqlite.sample.generated.DepartmentTableSql;
import com.tbohne.sqlite.sample.generated.DepartmentTableSql.DepartmentTableAllColumns.DepartmentTableAllColumnsRow;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * {@link com.tbohne.sqlite.sample.generated.DepartmentTableSql}
 *
 * <p> </p>From https://learn.microsoft.com/en-us/ef/ef6/resources/school-database
 */
@CreateTable(javaName = "Department", columns = {
		@TableColumn(name = "DepartmentID",
								 affinity = Affinity.INTEGER,
								 primaryKey = @ColumnPrimaryKey,
								 binder = @ColumnBinder(value = RowIdBinder.class, binderGenerics = {DepartmentTable.class})),
		@TableColumn(name = "Name", affinity = Affinity.TEXT, notNull = @NotNull),
		@TableColumn(name = "Budget",
								 affinity = Affinity.INTEGER,
								 binder = @ColumnBinder(MoneyBinder.class),
								 notNull = @NotNull),
		@TableColumn(name = "StartDate",
								 affinity = Affinity.REAL,
								 binder = @ColumnBinder(InstantJulianDoubleNonNullBinder.class),
								 notNull = @NotNull,
								 defaultValue = @DefaultValue("julianday(\\'now\\',\\'subsec\\')")),
		@TableColumn(name = "Administrator",
								 affinity = Affinity.INTEGER,
								 foreignKey = @ColumnForeignKey(value = PersonTable.class, onDelete = ForeignKeyAction.SET_NULL))})
public class DepartmentTable {

//	@CreateIndex(@IndexedColumn("Administrator"))
//	interface AdministratorIndex {
//	}

	private final Provider<DepartmentTableSql> table;

	@Inject
	public DepartmentTable(
			Provider<DepartmentTableSql> table)
	{
		this.table = table;
	}
	//
	//	public String GetDepartmentName(RowId<DepartmentTable> ID) {
	////			//Raw selection looks like this:
	////			table.get().selectName()
	////					.where(w -> w.whereIndexedEquals(w.DepartmentID(), ID))
	////					.asSingleItemOrThrowSync();
	//		//but there's a helper method for this:
	//		return table.get().selectName().whereRowIdEquals(ID).asSingleItemOrThrowSync();
	//	}

	//These methods are just demonstrating syntax, and aren't from https://learn.microsoft.com/en-us/ef/ef6/resources/school-database
	DepartmentTableAllColumnsRow getDepartmentRow(RowId<DepartmentTable> departmentId) {
		return table.get().selectAllColumnsFromDepartmentTable().whereRowIdEquals(departmentId).asRowOrThrowSync();
	}

	Set<DepartmentTableAllColumnsRow> slowlyGetDepartmentListByAdmin(RowId<PersonTable> administrator) {
		return table.get()
								.selectAllColumnsFromDepartmentTable()
								.where(w -> w.withSlowTableScan(
																 "Its not worth indexing names because admin lookup is rare and clearly marked")
														 .whereEquals(w.Administrator(), administrator))
								.asSetSync();
	}

//	Map<RowId<DepartmentTable>, DepartmentTableAllColumnsRow> countDepartmentsForAdmin(RowId<PersonTable> administrator) {
//		return table.get().selectCountFromDepartmentTable()
//								.where(w -> w.withSlowTableScan("count by Admin is rare and documented")
//														 .whereIndexedEquals(w.Administrator(), administrator)).sync();
//	}

	Map<RowId<DepartmentTable>, DepartmentTableAllColumnsRow> getDepartmentsForAdmin(RowId<PersonTable> administrator) {
		return table.get().selectAllColumnsFromDepartmentTable()
								.where(w -> w.withSlowTableScan("selection by Admin is rare and documented")
													 .whereEquals(w.Administrator(), administrator))
								.asMapSync();
	}

	RowId<DepartmentTable> insert(String name, Money budget, Instant startDate) {
		return table.get().insertIntoDepartmentTable()
								.values(name, budget, startDate)
								.sync();
	}

	/**
	 * Updates the administrator for a department
	 *
	 * <p>Note that this ignores the prior value, so if two threads both update, then the second one will overwrite the
	 * first.
	 *
	 * @throws {@link RowNotFoundException} if the department doesn't exist
	 */
	void updateAdministrator(RowId<DepartmentTable> departmentId, RowId<PersonTable> administrator) {
		table.get().updateDepartmentTable(departmentId)
				.set(v -> v.setAdministrator(administrator))
				.existingRowSync();
	}

	/**
	 * Updates the administrator for a department
	 *
	 * <p>Note that this ignores the prior value, so if two threads both update, then the second one will overwrite the
	 * first.</p>
	 *
	 * <p>Note that updateIfExistsSync requires a query and update, so is slower than a pure update.</p>
	 *
	 * @return
	 * <ul>
	 *   <li>UPDATED if the row existed and administrator was oldAdministrator and therefore the update occurred</li>
	 *   <li>ROW_FAILED_MATCH if the row existed but administrator was another value, and therefore no update occurred</li>
	 *   <li>ROW_NOT_FOUND if the row did not exist, and therefore no update occurred</li>
	 * </ul>
	 */
	UpdateResult updateAdministratorIfDepartmentStillExists(RowId<DepartmentTable> departmentId,
																								 RowId<PersonTable> administrator) {
		return table.get().updateDepartmentTable(departmentId)
				 .set(v -> v.setAdministrator(administrator))
				 .withDetailsSync();
	}

	/**
	 * Updates the administrator for a department atomically
	 *
	 * <p>Note that the where clause requires a query and update, so is slower than a pure update.</p>
	 *
	 * @return true if the administrator was oldAdministrator and therefore the update occurred, or false if the
	 * administrator was any other value, and therefore the update did not occur.
	 * @throws {@link RowNotFoundException} if the department doesn't exist
	 */
	boolean atomicReplaceAdministrator(RowId<DepartmentTable> departmentId, RowId<PersonTable> oldAdministrator,
																		 RowId<PersonTable> newAdministrator) {
		return table.get().updateDepartmentTable(departmentId)
								.set(v -> v.setAdministrator(newAdministrator))
								.where(w -> w.whereEquals(w.Administrator(), oldAdministrator))
								.existingRowSync();
		//https://stackoverflow.com/questions/31227149/why-is-this-type-inference-not-working-with-this-lambda-expression-scenario
	}

	/**
	 * Updates the administrator for a department atomically, if it wasn't deleted
	 *
	 * <p>Note that updateIfExistsSync requires a query and update, so is slower than a pure update.
	 *
	 * @return
	 * <ul>
	 *   <li>UPDATED if the row existed and administrator was oldAdministrator and therefore the update occurred</li>
	 *   <li>ROW_FAILED_MATCH if the row existed but administrator was another value, and therefore no update occurred</li>
	 *   <li>ROW_NOT_FOUND if the row did not exist, and therefore no update occurred</li>
	 * </ul>
	 */
	UpdateResult atomicReplaceAdministratorIfStillExists(RowId<DepartmentTable> departmentId,
																											 RowId<PersonTable> oldAdministrator,
																											 RowId<PersonTable> newAdinistrator) {
		return table.get().updateDepartmentTable(departmentId)
								.set(v -> v.setAdministrator(newAdinistrator))
								.where(w -> w.whereEquals(w.Administrator(), oldAdministrator))
								.withDetailsSync();
	}

	Set<RowId<DepartmentTable>> replaceAdministratorForAllDepartmentSlowly(RowId<PersonTable> oldAdministrator,
																							 RowId<PersonTable> newAdministrator) {
		return table.get().updateDepartmentTable()
				 .set(v -> v.setAdministrator(newAdministrator))
				 .where(w -> w.withSlowTableScan("replace by Admin is rare and documented")
											.whereEquals(w.Administrator(), oldAdministrator))
				 .withDetailsSync();
	}

	DeleteResult delete(RowId<DepartmentTable> departmentId) {
		//table.get().deleteFromDepartmentTable()
		// .where(w -> w.whereIndexedEquals(w.DepartmentID(), ID))
		// .sync();
		//but there's a helper method for this:
		return table.get().deleteFromDepartmentTableWhereIdEquals(departmentId).withDetailsSync();
	}

	//This is a stupid method and exists for sample purposes only
	Set<RowId<DepartmentTable>> deleteDepartmentsByAdminSlowly(RowId<PersonTable> administrator) {
		return table.get().deleteFromDepartmentTable()
		 .where(w -> w.withSlowTableScan("deleting by Admin is rare and documented")
									.whereEquals(w.Administrator(), administrator))
		 .withDetailsSync();
	}

	//Selection for just the Name as a String
	@Selection(select = @SelectColumns(projection = StringProjection.class, expressions = {
			@SelectExpression(name = "value", expression = "Name"),}), fromTable = @SelectTable(DepartmentTable.class))
	interface DepartmentNameSelection {
	}

	//Selection for just the Administrator's Name as a String
	@Selection(
			select = @SelectColumns(
					projection = StringProjection.class,
					expressions = {@SelectExpression(name = "value", expression = "Name"),}),
			fromTable = @SelectTable(PersonTable.class),
			joins = {
					@SelectJoin(
							operator = JoinOperator.UNSPECIFIED,
							fromTable = @SelectTable(DepartmentTable.class)
					)
			}
	)
	interface AdministratorNameSelection {
	}
}
