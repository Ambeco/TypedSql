package com.tbohne.sqlite.sample.schema;

import com.tbohne.sqlite.annotations.CreateProjection;
import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.Selection;
import com.tbohne.sqlite.annotations.createTable.ColumnBinder;
import com.tbohne.sqlite.annotations.createTable.ColumnForeignKey;
import com.tbohne.sqlite.annotations.createTable.ColumnPrimaryKey;
import com.tbohne.sqlite.annotations.createTable.DefaultValue;
import com.tbohne.sqlite.annotations.createTable.NotNull;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.annotations.query.ProjectionExpression;
import com.tbohne.sqlite.annotations.query.SelectColumns;
import com.tbohne.sqlite.annotations.query.SelectTable;
import com.tbohne.sqlite.binders.instant.InstantJulianDoubleNonNullBinder;
import com.tbohne.sqlite.binders.instant.InstantUnixMillisBinder;

/**
 * {@link com.tbohne.sqlite.sample.schema.OfficeAssignmentTableSql}
 *
 * <p> </p>From https://learn.microsoft.com/en-us/ef/ef6/resources/school-database
 */
@CreateTable(javaName = "OfficeAssignment", columns = {
		@TableColumn(name = "InstructorID",
								 affinity = Affinity.INTEGER,
								 primaryKey = @ColumnPrimaryKey,
								 foreignKey = @ColumnForeignKey(PersonTable.class)),
		@TableColumn(name = "Location", affinity = Affinity.TEXT, notNull = @NotNull),
		@TableColumn(name = "Timestamp",
								 affinity = Affinity.REAL,
								 binder = @ColumnBinder(InstantJulianDoubleNonNullBinder.class),
								 notNull = @NotNull,
								 defaultValue = @DefaultValue("julianday(\\'now\\',\\'subsec\\')")),})
public class OfficeAssignmentTable {

	//	private final Provider<OfficeAssignmentTableSql> table;
	//
	//	@Inject
	//	public OfficeAssignmentTable(Provider<OfficeAssignmentTableSql> table) {
	//		this.table = table;
	//	}
	//
	//	public Instant InsertOfficeAssignment(RowId<PersonTable> InstructorID, String Location) {
	//		return table.get().getDatabase().inTransaction(() -> {
	////			//Raw insertion looks like this:
	////			table.get().newRowInserter()
	////					.set(table.InstructorID(), InstructorID)
	////					.set(table.Location(), Location)
	////					.insert();
	//			//but there's a helper method for this:
	//			table.get().insert(InstructorId, Location);
	//
	//			//Raw selection would be
	////			return table.get().selectTimestamp()
	////					.where(w -> w.whereIndexedEquals(w.InstructorId(), InstructorID))
	////					.asSingleItemOrThrowSync();
	//			//but there's a helper method for this:
	//			return table.get().selectTimestamp().whereRowIdEquals(InstructorID).asSingleItemOrThrowSync();
	//		});
	//	}
	//
	//	public Instant UpdateOfficeAssignment(
	//			RowId<PersonTable> InstructorID,
	//			String Location,
	//			Instant OrigTimestamp) {
	//		return table.get().getDatabase().inTransaction(() -> {
	//			//there's an update helper method, but it doesn't check the timestamp:
	////			table.get().newRowUpdater().set(table.Location(), Location).update(InstructorID);
	//			table.get().newRowUpdater()
	//					.set(table.Location(), Location)
	//					.where(w ->
	//							w.whereIndexedEquals(w.InstructorId(), InstructorID)
	//									.whereEquals(w.Timestamp(), OrigTimestamp))
	//					.update();
	//
	//			return table.get().selectTimestamp().whereRowIdEquals(InstructorID).asSingleItemOrThrowSync();
	//		});
	//	}

	//Selection for just the Timestamp
	@CreateProjection(expressions = {
			@ProjectionExpression(name = "Timestamp", affinity = Affinity.INTEGER, binder = InstantUnixMillisBinder.class)})
	@Selection(select = @SelectColumns(projection = TimestampSelection.class),
						 fromTable = @SelectTable(OfficeAssignmentTable.class))
	interface TimestampSelection {
	}
}
