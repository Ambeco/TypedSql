package com.tbohne.sqlite.sample.generated;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.util.concurrent.FluentFuture;
import com.tbohne.sqlite.RowId;
import com.tbohne.sqlite.binders.RowIdBinder;
import com.tbohne.sqlite.binders.RowIdNonNullBinder;
import com.tbohne.sqlite.binders.StringNonNullBinder;
import com.tbohne.sqlite.binders.instant.InstantJulianDoubleNonNullBinder;
import com.tbohne.sqlite.bound.AbstractTableSql;
import com.tbohne.sqlite.bound.WriteValues;
import com.tbohne.sqlite.bound.columns.SimpleLongColumn;
import com.tbohne.sqlite.bound.deleteBulk.AbstractDeleteBulk;
import com.tbohne.sqlite.bound.deleteBulk.DeleteBulk;
import com.tbohne.sqlite.bound.deleteUnique.AbstractDeleteUnique;
import com.tbohne.sqlite.bound.deleteUnique.DeleteUnique;
import com.tbohne.sqlite.bound.insert.AbstractInsert;
import com.tbohne.sqlite.bound.updateBulk.AbstractUpdateBulk;
import com.tbohne.sqlite.bound.updateBulk.UpdateBulk;
import com.tbohne.sqlite.bound.updateUnique.AbstractUpdateUnique;
import com.tbohne.sqlite.bound.updateUnique.UpdateUnique;
import com.tbohne.sqlite.bound.where.IndexedWhereBuilder;
import com.tbohne.sqlite.bound.columns.AbstractJavaColumn;
import com.tbohne.sqlite.bound.select.MappableSelectable;
import com.tbohne.sqlite.bound.select.AbstractMappableSelectable;
import com.tbohne.sqlite.bound.columns.SimpleLongNonNullColumn;
import com.tbohne.sqlite.bound.columns.SimpleRealNonNullColumn;
import com.tbohne.sqlite.bound.columns.SimpleStringNonNullColumn;
import com.tbohne.sqlite.bound.select.TypedCursor;
import com.tbohne.sqlite.bound.select.AbstractTypedCursorWrapper;
import com.tbohne.sqlite.bound.where.UnindexedWhereBuilder;
import com.tbohne.sqlite.bound.where.AbstractWhereBuilder;
import com.tbohne.sqlite.exceptions.FieldNotSetException;
import com.tbohne.sqlite.sample.datatypes.Money;
import com.tbohne.sqlite.sample.datatypes.MoneyBinder;
import com.tbohne.sqlite.sample.schema.DepartmentTable;
import com.tbohne.sqlite.sample.schema.PersonTable;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.util.BitSet;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import javax.annotation.Generated;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * {@link com.tbohne.sqlite.sample.schema.DepartmentTable}
 */
@Generated(value = "com.tbohne.sqlite.processor.typedSqlProcessor",
					 comments = "from com.tbohne.sqlite.sample.schema.DepartmentTable")
//TODO inheritence
public class DepartmentTableSql extends AbstractTableSql<String> {
	//TODO Add members
	protected final Columns columns;

	@Inject
	public DepartmentTableSql(
			Provider<TransactionListeners<String>> transactionListeners,
			Columns columns,
			Executor databaseQueryExecutor,
			Executor binderExecutor,
			Supplier<SQLiteDatabase> databaseSync,
			Supplier<FluentFuture<SQLiteDatabase>> databaseAsync)
	{
		super(transactionListeners, databaseQueryExecutor, binderExecutor, databaseSync, databaseAsync);
		this.columns = columns;
	}

	public String getCreateTable(boolean ifNotExists) {
		return "CREATE TABLE "
					 + (ifNotExists ? "IF NOT EXISTS " : "")
					 + "Department ("
					 + "DepartmentID INTEGER PRIMARY KEY, "
					 + "Name TEXT NOT NULL, "
					 + "Budget INTEGER NOT NULL, "
					 + "StartDate REAL NOT NULL DEFAULT julianday(\'now\',\'subsec\'), "
					 + "Administrator INTEGER REFERENCES Person ON DELETE SET NULL"
					 + ")";
	}

	//TODO implement
	public MappableSelectable<DepartmentTableAllColumns, DepartmentTableAllColumns.UnindexedWhereBuilderImpl,
			DepartmentTableAllColumns.IndexedWhereBuilderImpl,
			DepartmentTableAllColumns.DepartmentTableAllColumnsSqlCursorWrapperImpl,
			DepartmentTableAllColumns.DepartmentTableAllColumnsJavaCursorWrapperImpl, DepartmentTableAllColumns.DepartmentTableAllColumnsRow, RowId<DepartmentTable>>
	selectAllColumnsFromDepartmentTable()
	{
		return new DepartmentTableAllColumns.SelectableImpl(columns,
																										databaseQueryExecutor,
																										binderExecutor,
																										databaseSync,
																										databaseAsync);
	}

	//TODO implement
//	public Selectable<DepartmentTableAllColumns, DepartmentTableAllColumns.UnindexedWhereBuilderImpl,
//				DepartmentTableAllColumns.IndexedWhereBuilderImpl,
//				RowIdNonNullProjectionSql.RowIdNonNullProjectionCursorWrapperImpl,
//			  RowIdNonNullProjectionSql.RowIdNonNullProjectionJavaCursorWrapperImpl,
//			  RowIdNonNullProjectionSql.RowIdNonNullProjectionRow, RowId<DepartmentTable>>
//	selectAllColumnsFromDepartmentTable()
//	{
//		return new RowIdNonNullProjectionSql.SelectableImpl(columns,
//																												databaseQueryExecutor,
//																												binderExecutor,
//																												databaseSync,
//																												databaseAsync);
//	}

	public interface SqlColumnNumbers {
		int DepartmentID = 0;

		int Name = 1;

		int Budget = 2;

		int StartDate = 3;

		int Administrator = 4;
	}

	public interface SqlColumnNames {
		String DepartmentID = "_department_i_d";

		String Name = "_name";

		String Budget = "_budget";

		String StartDate = "_start_date";

		String Administrator = "_administrator";
	}

	public interface JavaColumnNumbers {
		int DepartmentID = 0;

		int Name = 1;

		int Budget = 2;

		int StartDate = 3;

		int Administrator = 4;
	}

	//TODO: Replace Binders with Columns
	public static final class Columns {
		SimpleLongNonNullColumn<DepartmentTable, RowId<DepartmentTable>, RowIdNonNullBinder<DepartmentTable>,
				AbstractJavaColumn.IsIndexed>
				DepartmentId;

		SimpleStringNonNullColumn<DepartmentTable, String, StringNonNullBinder, AbstractJavaColumn.NotIndexed> Name;

		SimpleLongNonNullColumn<DepartmentTable, Money, MoneyBinder, AbstractJavaColumn.NotIndexed> Budget;

		SimpleRealNonNullColumn<DepartmentTable, Instant, InstantJulianDoubleNonNullBinder, AbstractJavaColumn.NotIndexed>
				StartDate;

		SimpleLongColumn<DepartmentTable, @Nullable RowId<PersonTable>, RowIdBinder<PersonTable>,
				AbstractJavaColumn.NotIndexed>
				Administrator;

		@Inject
		public Columns(
				RowIdNonNullBinder<DepartmentTable> departmentIdBinder,
				StringNonNullBinder stringNonNullBinder,
				MoneyBinder moneyBinder,
				InstantJulianDoubleNonNullBinder instantJulianRealNonNullBinder,
				RowIdBinder<PersonTable> personTableRowIdBinder)
		{
			DepartmentId = new SimpleLongNonNullColumn<>(departmentIdBinder, "DepartmentId");
			Name = new SimpleStringNonNullColumn<>(stringNonNullBinder, "Name");
			Budget = new SimpleLongNonNullColumn<>(moneyBinder, "Budget");
			StartDate = new SimpleRealNonNullColumn<>(instantJulianRealNonNullBinder, "StartDate");
			Administrator = new SimpleLongColumn<>(personTableRowIdBinder, "Administrator");
		}


		SimpleLongNonNullColumn<DepartmentTable, RowId<DepartmentTable>, RowIdNonNullBinder<DepartmentTable>,
				AbstractJavaColumn.IsIndexed> DepartmentId() {
			return DepartmentId;
		}

		SimpleStringNonNullColumn<DepartmentTable, String, StringNonNullBinder, AbstractJavaColumn.NotIndexed> Name() {
			return Name;
		}

		SimpleLongNonNullColumn<DepartmentTable, Money, MoneyBinder, AbstractJavaColumn.NotIndexed> Budget() {
			return Budget;
		}

		SimpleRealNonNullColumn<DepartmentTable, Instant, InstantJulianDoubleNonNullBinder, AbstractJavaColumn.NotIndexed> StartDate() {
			return StartDate;
		}

		SimpleLongColumn<DepartmentTable, @Nullable RowId<PersonTable>, RowIdBinder<PersonTable>, AbstractJavaColumn.NotIndexed> Administrator() {
			return Administrator;
		}
	}

	/**
	 * {@link DepartmentTable}
	 */
	//    @CreateProjection(
	//            tables = @ProjectionTable(DepartmentTable.class)
	//    )
	public static final class DepartmentTableProjection {
		public static String[] getProjectionStrings() {
			return new String[]{
					SqlColumnNames.DepartmentID,
					SqlColumnNames.Name,
					SqlColumnNames.Budget,
					SqlColumnNames.StartDate,
					SqlColumnNames.Administrator,};
		}
	}

	/**
	 * {@link DepartmentTable}
	 */
	//    @Selection(
	//            select = @SelectColumns(projection = DepartmentTableProjection.class),
	//            fromTable = @SelectTable(DepartmentTable.class)
	//    )
	//TODO rename from DepartmentTableSelection to DepartmentTableAllColumns
	public static final class DepartmentTableAllColumns {

		//TODO implement
		public static class DepartmentTableAllColumnsRow {
			private final RowId<DepartmentTable> DepartmentId;
			private final String Name;
			private final Money Budget;
			private final Instant StartDate;
			private final @Nullable RowId<PersonTable> Administrator;

			public DepartmentTableAllColumnsRow(
					RowId<DepartmentTable> DepartmentId,
					String Name,
					Money Budget,
					Instant StartDate,
					@Nullable RowId<PersonTable> Administrator)
			{
				this.DepartmentId = DepartmentId;
				this.Name = Name;
				this.Budget = Budget;
				this.StartDate = StartDate;
				this.Administrator = Administrator;
			}

			public RowId<DepartmentTable> DepartmentId() {
				return DepartmentId;
			}

			public String Name() {
				return Name;
			}

			public Money Budget() {
				return Budget;
			}

			public Instant StartDate() {
				return StartDate;
			}

			public @Nullable RowId<PersonTable> Administrator() {
				return Administrator;
			}

			@Override
			public boolean equals(@Nullable Object other) {
				if (!(other instanceof DepartmentTableAllColumnsRow)) return false;
				return DepartmentId.equals(((DepartmentTableAllColumnsRow) other).DepartmentId);
			}

			@Override
			public int hashCode(){
				return DepartmentId.hashCode();
			}
		}

		//TODO: Add IndexedWhereBuilderImpl, UnindexedWhereBuilderImpl, WhereBuilder
		public interface UnindexedWhereBuilderImpl
				extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, DepartmentTableAllColumns>
		{
			AbstractJavaColumn<DepartmentTableAllColumns, RowId<DepartmentTable>, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> DepartmentId();

			AbstractJavaColumn<DepartmentTableAllColumns, String, AbstractJavaColumn.NotIndexed, AbstractJavaColumn.SingleBinder> Name();

			AbstractJavaColumn<DepartmentTableAllColumns, Money, AbstractJavaColumn.NotIndexed, AbstractJavaColumn.SingleBinder> Budget();

			AbstractJavaColumn<DepartmentTableAllColumns, Instant, AbstractJavaColumn.NotIndexed, AbstractJavaColumn.SingleBinder> StartDate();

			AbstractJavaColumn<DepartmentTableAllColumns, @Nullable RowId<PersonTable>, AbstractJavaColumn.NotIndexed, AbstractJavaColumn.SingleBinder> Administrator();
		}

		public interface IndexedWhereBuilderImpl
				extends IndexedWhereBuilder<IndexedWhereBuilderImpl, DepartmentTableAllColumns>,
								UnindexedWhereBuilderImpl
		{
		}

		public static final class WhereBuilderImpl
				extends AbstractWhereBuilder<UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, DepartmentTableAllColumns>
				implements UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl
		{
			final Columns columns;

			public WhereBuilderImpl(Columns columns) {
				this.columns = columns;
			}

			@Override
			public AbstractJavaColumn<DepartmentTableAllColumns, RowId<DepartmentTable>, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> DepartmentId() {
				return columns.DepartmentId().<DepartmentTableAllColumns>asOtherTable();
			}

			@Override
			public AbstractJavaColumn<DepartmentTableAllColumns, String, AbstractJavaColumn.NotIndexed, AbstractJavaColumn.SingleBinder> Name() {
				return columns.Name().<DepartmentTableAllColumns>asOtherTable();
			}

			@Override
			public AbstractJavaColumn<DepartmentTableAllColumns, Money, AbstractJavaColumn.NotIndexed, AbstractJavaColumn.SingleBinder> Budget() {
				return columns.Budget().<DepartmentTableAllColumns>asOtherTable();
			}

			@Override
			public AbstractJavaColumn<DepartmentTableAllColumns, Instant, AbstractJavaColumn.NotIndexed, AbstractJavaColumn.SingleBinder> StartDate() {
				return columns.StartDate().<DepartmentTableAllColumns>asOtherTable();
			}

			@Override
			public AbstractJavaColumn<DepartmentTableAllColumns, @Nullable RowId<PersonTable>, AbstractJavaColumn.NotIndexed, AbstractJavaColumn.SingleBinder> Administrator() {
				return columns.Administrator().<DepartmentTableAllColumns>asOtherTable();
			}

			@Override
			protected UnindexedWhereBuilderImpl unindexedSelf() {
				return this;
			}

			@Override
			protected IndexedWhereBuilderImpl indexedSelf() {
				return this;
			}
		}

		public static final class WhereBuilderFactory {
			final Columns columns;

			@Inject
			public WhereBuilderFactory(Columns columns) {
				this.columns = columns;
			}

			UnindexedWhereBuilder create() {
				return new WhereBuilderImpl(columns);
			}
		}

		interface DepartmentTableAllColumnsJavaCursor extends TypedCursor {
			public DepartmentTableAllColumnsRow getRow();

			public RowId<DepartmentTable> DepartmentId();

			public String Name();

			public Money Budget();

			public Instant StartDate();

			public @Nullable RowId<PersonTable> Administrator();
		}

		interface DepartmentTableAllColumnsSqlCursor extends TypedCursor{

			public Long SqlDepartmentId();

			public String SqlName();

			public Long SqlBudget();

			public Double SqlStartDate();

			public @Nullable Long SqlAdministrator();
		}

		//TODO implement
		public static final class DepartmentTableAllColumnsJavaCursorWrapperImpl
				extends AbstractTypedCursorWrapper
				implements DepartmentTableAllColumnsJavaCursor
		{
			Columns columns;

			public DepartmentTableAllColumnsJavaCursorWrapperImpl(Columns columns, Cursor delegate) {
				super(delegate);
				this.columns = columns;
			}

			public DepartmentTableAllColumnsRow getRow() {
				return new DepartmentTableAllColumnsRow(DepartmentId(), Name(), Budget(), StartDate(), Administrator());
			}

			public RowId<DepartmentTable> DepartmentId() {
				return columns.DepartmentId().fromSql(delegate.getLong(0));
			}

			public String Name() {
				return columns.Name.fromSql(delegate.getString(1));
			}

			public Money Budget() {
				return columns.Budget.fromSql(delegate.getLong(2));
			}

			public Instant StartDate() {
				return columns.StartDate.fromSql(delegate.getDouble(3));
			}

			public @Nullable RowId<PersonTable> Administrator() {
				return columns.Administrator.fromSql(delegate.getLong(4));
			}
		}

		//TODO implement
		public static final class DepartmentTableAllColumnsSqlCursorWrapperImpl
				extends AbstractTypedCursorWrapper
				implements DepartmentTableAllColumnsSqlCursor
		{
			Columns columns;

			public DepartmentTableAllColumnsSqlCursorWrapperImpl(Columns columns, Cursor delegate) {
				super(delegate);
				this.columns = columns;
			}

			public Long SqlDepartmentId() {
				return delegate.getLong(0);
			}

			public @Nullable String SqlName() {
				return delegate.getString(1);
			}

			public Long SqlBudget() {
				return delegate.getLong(2);
			}

			public Double SqlStartDate() {
				return delegate.getDouble(3);
			}

			public @Nullable Long SqlAdministrator() {
				return delegate.getLong(4);
			}
		}

		//TODO implement
		public static final class SelectableImpl
				extends AbstractMappableSelectable<DepartmentTableAllColumns, UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, DepartmentTableAllColumnsSqlCursorWrapperImpl, DepartmentTableAllColumnsJavaCursorWrapperImpl, DepartmentTableAllColumnsRow, RowId<DepartmentTable>>
		{
			public SelectableImpl(
					Columns columns,
					Executor databaseQueryExecutor,
					Executor binderExecutor,
					Supplier<SQLiteDatabase> databaseSync,
					Supplier<FluentFuture<SQLiteDatabase>> databaseAsync)
			{
				super(() -> new WhereBuilderImpl(columns),
							rawCursor -> new DepartmentTableAllColumnsSqlCursorWrapperImpl(columns, rawCursor),
							rawCursor -> new DepartmentTableAllColumnsJavaCursorWrapperImpl(columns, rawCursor),
							javaCursor -> javaCursor.getRow(),
							row -> row.DepartmentId(),
							databaseQueryExecutor,
							binderExecutor,
							databaseSync,
							databaseAsync,
							columns.DepartmentId().<DepartmentTableAllColumns>asOtherTable(),
							false,
							"Department",
							DepartmentTableProjection.getProjectionStrings(),
							new WhereBuilderImpl(columns),
							null);
			}
		}
	}


	//TODO extend WriteValues
	public static class DepartmentTableWriteValues<Chained extends DepartmentTableWriteValues<Chained>> implements WriteValues
	{
		//TODO: Replace Binders with Columns
		private final Columns columns;

		private final BitSet fieldsSet = new BitSet(5);

		private final ContentValues values = new ContentValues();

		@Nullable RowId<DepartmentTable> DepartmentID;

		@Nullable String Name;

		@Nullable Money Budget;

		@Nullable Instant StartDate;

		@Nullable RowId<PersonTable> Administrator;

		protected DepartmentTableWriteValues(Columns columns) {
			this.columns = columns;
		}

		protected DepartmentTableWriteValues(Columns columns, RowId<DepartmentTable> DepartmentId) {
			this.columns = columns;
			setDepartmentID(DepartmentId);
		}

		protected DepartmentTableWriteValues(Columns columns, DepartmentTableAllColumns.DepartmentTableAllColumnsRow row) {
			this.columns = columns;
			setDepartmentID(row.DepartmentId);
			setName(row.Name);
			setBudget(row.Budget);
			setStartDate(row.StartDate);
			setAdministrator(row.Administrator);
		}

		public boolean hasDepartmentID() {
			return this.fieldsSet.get(JavaColumnNumbers.DepartmentID);
		}

		public RowId<DepartmentTable> getDepartmentID() {
			if (!fieldsSet.get(JavaColumnNumbers.DepartmentID)) {
				throw new FieldNotSetException("DepartmentID");
			}
			return DepartmentID;
		}

		protected Chained setDepartmentID(RowId<DepartmentTable> javaValue) {
			//TODO: values.put("_department_i_d", binders.rowIdBinder.toSql) with this:
			columns.DepartmentId().toSql(javaValue, this.values);
			this.DepartmentID = javaValue;
			this.fieldsSet.set(JavaColumnNumbers.DepartmentID);
			return (Chained) this;
		}

		public boolean hasName() {
			return this.fieldsSet.get(JavaColumnNumbers.Name);
		}

		public String getName() {
			if (!fieldsSet.get(JavaColumnNumbers.Name)) {
				throw new FieldNotSetException("Name");
			}
			return Name;
		}

		public Chained setName(String javaValue) {
			columns.Name().toSql(javaValue, this.values);
			this.Name = javaValue;
			this.fieldsSet.set(JavaColumnNumbers.Name);
			return (Chained) this;
		}

		public boolean hasBudget() {
			return this.fieldsSet.get(JavaColumnNumbers.Budget);
		}

		public Money getBudget() {
			if (!fieldsSet.get(JavaColumnNumbers.Budget)) {
				throw new FieldNotSetException("Budget");
			}
			return Budget;
		}

		public Chained setBudget(Money javaValue) {
			columns.Budget().toSql(javaValue, this.values);
			this.Budget = javaValue;
			this.fieldsSet.set(JavaColumnNumbers.Budget);
			return (Chained) this;
		}

		public boolean hasStartDate() {
			return this.fieldsSet.get(JavaColumnNumbers.StartDate);
		}

		public Instant getStartDate() {
			if (!fieldsSet.get(JavaColumnNumbers.StartDate)) {
				throw new FieldNotSetException("StartDate");
			}
			return StartDate;
		}

		public Chained setStartDate(Instant javaValue) {
			columns.StartDate().toSql(javaValue, this.values);
			this.StartDate = javaValue;
			this.fieldsSet.set(JavaColumnNumbers.StartDate);
			return (Chained) this;
		}

		public boolean hasAdministrator() {
			return this.fieldsSet.get(JavaColumnNumbers.Administrator);
		}

		public @Nullable RowId<PersonTable> getAdministrator() {
			if (!fieldsSet.get(JavaColumnNumbers.Administrator)) {
				throw new FieldNotSetException("Administrator");
			}
			return Administrator;
		}

		public Chained setAdministrator(@Nullable RowId<PersonTable> javaValue) {
			columns.Administrator().toSql(javaValue, this.values);
			this.Administrator = javaValue;
			this.fieldsSet.set(JavaColumnNumbers.Administrator);
			return (Chained) this;
		}

		//TODO: make public
		public ContentValues getValues() {
			return this.values;
		}
	}
	
	public static class DepartmentTableUpdateValues extends DepartmentTableWriteValues<DepartmentTableUpdateValues> {

		public DepartmentTableUpdateValues(Columns columns) {
			super(columns);
		}

		public DepartmentTableUpdateValues(Columns columns, RowId<DepartmentTable> DepartmentId) {
			super(columns, DepartmentId);
		}

		public DepartmentTableUpdateValues(Columns columns, DepartmentTableAllColumns.DepartmentTableAllColumnsRow row) {
			super(columns, row);
		}
	}

	public static class DepartmentTableInsertValues extends DepartmentTableWriteValues<DepartmentTableInsertValues> {
		public DepartmentTableInsertValues(Columns columns) {
			super(columns);
		}

		@Override
		public DepartmentTableInsertValues setDepartmentID(RowId<DepartmentTable> departmentID) {
			return super.setDepartmentID(departmentID);
		}
	}

	public DepartmentTableSql.DepartmentTableInsertValues newInsertValues() {
		return new DepartmentTableSql.DepartmentTableInsertValues(columns);
	}

	public DepartmentTableSql.DepartmentTableUpdateValues newUpdateValues(RowId<DepartmentTable> rowId) {
		return new DepartmentTableSql.DepartmentTableUpdateValues(columns, rowId);
	}

	public DepartmentTableSql.DepartmentTableUpdateValues newUpdateValues(DepartmentTableAllColumns.DepartmentTableAllColumnsRow row) {
		return new DepartmentTableSql.DepartmentTableUpdateValues(columns, row);
	}

	public class UpdateUniqueImpl extends AbstractUpdateUnique<DepartmentTableAllColumns, DepartmentTableUpdateValues,
			DepartmentTableAllColumns.UnindexedWhereBuilderImpl,
			DepartmentTableAllColumns.IndexedWhereBuilderImpl,
			RowId<DepartmentTable>> {
		public UpdateUniqueImpl(
				Columns columns,
												Executor databaseQueryExecutor,
				Executor binderExecutor,
				Supplier<SQLiteDatabase> databaseSync,
				Supplier<FluentFuture<SQLiteDatabase>> databaseAsync,
				RowId<DepartmentTable> departmentID)
		{
			super(
					() -> new DepartmentTableUpdateValues(columns),
					() -> new DepartmentTableAllColumns.WhereBuilderImpl(columns),
					databaseQueryExecutor,
					binderExecutor,
					databaseSync,
					databaseAsync,
					"DepartmentTable",
					departmentID,
					new DepartmentTableAllColumns.WhereBuilderImpl(columns));
		}
	}

	public UpdateUnique<DepartmentTableAllColumns, DepartmentTableUpdateValues,
			DepartmentTableAllColumns.UnindexedWhereBuilderImpl,
			DepartmentTableAllColumns.IndexedWhereBuilderImpl> updateDepartmentTable(RowId<DepartmentTable> departmentID) {
		return new UpdateUniqueImpl(
				columns,
				databaseQueryExecutor,
				binderExecutor,
				databaseSync,
				databaseAsync,
				departmentID);
	}

	public class UpdateBulkImpl extends AbstractUpdateBulk<DepartmentTableAllColumns, DepartmentTableUpdateValues,
			DepartmentTableAllColumns.UnindexedWhereBuilderImpl,
			DepartmentTableAllColumns.IndexedWhereBuilderImpl, RowIdNonNullBinder<DepartmentTable>,
			RowId<DepartmentTable>>
	{
		public UpdateBulkImpl(
				Columns columns,
				Executor databaseQueryExecutor,
				Executor binderExecutor,
				Supplier<SQLiteDatabase> databaseSync,
				Supplier<FluentFuture<SQLiteDatabase>> databaseAsync)
		{
			super(
					() -> new DepartmentTableUpdateValues(columns),
					() -> new DepartmentTableAllColumns.WhereBuilderImpl(columns),
					columns.DepartmentId.<DepartmentTableAllColumns>asOtherTable(),
					databaseQueryExecutor,
					binderExecutor,
					databaseSync,
					databaseAsync,
					"DepartmentTable",
					new DepartmentTableAllColumns.WhereBuilderImpl(columns));
		}
	}

	public UpdateBulk<DepartmentTableAllColumns, DepartmentTableUpdateValues,
			DepartmentTableAllColumns.UnindexedWhereBuilderImpl,
			DepartmentTableAllColumns.IndexedWhereBuilderImpl,
			RowId<DepartmentTable>>
	updateDepartmentTable() {
		return new UpdateBulkImpl(
				columns,
				databaseQueryExecutor,
				binderExecutor,
				databaseSync,
				databaseAsync);
	}


	public class DeleteUniqueImpl extends AbstractDeleteUnique<DepartmentTableAllColumns,
				DepartmentTableAllColumns.UnindexedWhereBuilderImpl,
				DepartmentTableAllColumns.IndexedWhereBuilderImpl,
				RowId<DepartmentTable>>
	{
		public DeleteUniqueImpl(
				Columns columns,
				Executor databaseQueryExecutor,
				Executor binderExecutor,
				Supplier<SQLiteDatabase> databaseSync,
				Supplier<FluentFuture<SQLiteDatabase>> databaseAsync,
				RowId<DepartmentTable> departmentID)
		{
			super(
					() -> new DepartmentTableAllColumns.WhereBuilderImpl(columns),
					databaseQueryExecutor,
					binderExecutor,
					databaseSync,
					databaseAsync,
					"DepartmentTable",
					departmentID,
					new DepartmentTableAllColumns.WhereBuilderImpl(columns));
		}
	}

	public DeleteUnique<DepartmentTableAllColumns,
				DepartmentTableAllColumns.IndexedWhereBuilderImpl>
	deleteFromDepartmentTableWhereIdEquals(RowId<DepartmentTable> departmentID) {
		return new DeleteUniqueImpl(
				columns,
				databaseQueryExecutor,
				binderExecutor,
				databaseSync,
				databaseAsync,
				departmentID);
	}

	public class DeleteBulkImpl extends AbstractDeleteBulk<DepartmentTableAllColumns,
				DepartmentTableAllColumns.UnindexedWhereBuilderImpl,
				DepartmentTableAllColumns.IndexedWhereBuilderImpl, RowIdNonNullBinder<DepartmentTable>,
				RowId<DepartmentTable>>
	{
		public DeleteBulkImpl(
				Columns columns,
				Executor databaseQueryExecutor,
				Executor binderExecutor,
				Supplier<SQLiteDatabase> databaseSync,
				Supplier<FluentFuture<SQLiteDatabase>> databaseAsync)
		{
			super(
					() -> new DepartmentTableAllColumns.WhereBuilderImpl(columns),
					columns.DepartmentId.<DepartmentTableAllColumns>asOtherTable(),
					databaseQueryExecutor,
					binderExecutor,
					databaseSync,
					databaseAsync,
					"DepartmentTable",
					new DepartmentTableAllColumns.WhereBuilderImpl(columns));
		}
	}

	public DeleteBulk<DepartmentTableAllColumns,
				DepartmentTableAllColumns.UnindexedWhereBuilderImpl,
				DepartmentTableAllColumns.IndexedWhereBuilderImpl,
				RowId<DepartmentTable>>
	deleteFromDepartmentTable() {
		return new DeleteBulkImpl(
				columns,
				databaseQueryExecutor,
				binderExecutor,
				databaseSync,
				databaseAsync);
	}

	public class InsertImpl extends AbstractInsert<DepartmentTable, DepartmentTableInsertValues,
			RowIdNonNullBinder<DepartmentTable>,
				RowId<DepartmentTable>>
	{
		public InsertImpl(
				Columns columns,
				Executor databaseQueryExecutor,
				Executor binderExecutor,
				Supplier<SQLiteDatabase> databaseSync,
				Supplier<FluentFuture<SQLiteDatabase>> databaseAsync)
		{
			super(
					() -> new DepartmentTableInsertValues(columns),
					() -> "Name",
					columns.DepartmentId,
					databaseQueryExecutor,
					binderExecutor,
					databaseSync,
					databaseAsync,
					"DepartmentTable");
		}

		public InsertImpl values(String Name, Money Budget, Instant StartDate) {
			super.values.setName(Name).setBudget(Budget).setStartDate(StartDate);
			return this;
		}
	}

	public InsertImpl insertIntoDepartmentTable() {
		return new InsertImpl(
				columns,
				databaseQueryExecutor,
				binderExecutor,
				databaseSync,
				databaseAsync);
	}

}
