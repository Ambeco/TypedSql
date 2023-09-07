package com.tbohne.sqlite.processor.models;

import com.google.common.base.Strings;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.createTable.ColumnForeignKey;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.createTable.TableForeignKey;
import com.tbohne.sqlite.annotations.enums.ForeignKeyAction;
import com.tbohne.sqlite.annotations.enums.ForeignKeyDeferMode;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.ProcessorHelpers;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * {@link com.tbohne.sqlite.annotations.createTable.TableForeignKey}
 */
public class ForeignKeyModel {
	public final @Nullable String constraintName;
	public final ImmutableList<String> localColumnNames;
	public final Supplier<List<@Nullable TableColumnModel>> localModels;
	public final @Nullable Supplier<CreateTableModel> foreignTableIfAutogen;
	public final String foreignTableName;
	public final @Nullable Supplier<List<@Nullable TableColumnModel>> foreignColumnsIfAutogen;
	public final ImmutableList<String> explicitForeignColumnNames; //can be empty
	public final ForeignKeyAction onUpdate;
	public final ForeignKeyAction onDelete;
	public final ForeignKeyDeferMode deferred;

	private ForeignKeyModel(
			@Nullable String constraintName,
			ImmutableList<String> localColumnNames,
			Supplier<List<@Nullable TableColumnModel>> localModels,
			@Nullable Supplier<CreateTableModel> foreignTableIfAutogen,
			String foreignTableName,
			@Nullable Supplier<List<@Nullable TableColumnModel>> foreignColumnsIfAutogen,
			ImmutableList<String> explicitForeignColumnNames,
			ForeignKeyAction onUpdate,
			ForeignKeyAction onDelete,
			ForeignKeyDeferMode deferred)
	{
		this.constraintName = constraintName;
		this.localColumnNames = localColumnNames;
		this.localModels = localModels;
		this.foreignTableIfAutogen = foreignTableIfAutogen;
		this.foreignTableName = foreignTableName;
		this.foreignColumnsIfAutogen = foreignColumnsIfAutogen;
		this.explicitForeignColumnNames = explicitForeignColumnNames;
		this.onUpdate = onUpdate;
		this.onDelete = onDelete;
		this.deferred = deferred;
	}

	public static ImmutableList<ForeignKeyModel> buildList(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			CreateTable createTable,
			ImmutableMap<String, TableColumnModel> sqlNameColumnMap,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		TableForeignKey[] foreignKeys = createTable.foreignKeys();
		ImmutableList.Builder<ForeignKeyModel> list = ImmutableList.builder();
		for (int i = 0; i < foreignKeys.length; i++) {
			TableForeignKey foreignKey = foreignKeys[i];
			ForeignKeyModel
					model =
					buildForTable(environment,
												messager,
												tableElement,
												createTable,
												foreignKey,
												sqlNameColumnMap,
												pathToTableCache);
			if (model != null) {
				list.add(model);
			}
		}
		return list.build();
	}

	public static @Nullable ForeignKeyModel buildForTable(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			CreateTable createTable,
			TableForeignKey foreignKey,
			ImmutableMap<String, TableColumnModel> sqlNameColumnMap,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		TypeMirror voidMirror = environment.getElementUtils().getTypeElement(Void.class.getName()).asType();
		if (messager != null && !StringHelpers.validOrEmptySqlId(foreignKey.constraintName())) {
			messager.error(tableElement, "Table check %s has invalid name", foreignKey.constraintName());
		}
		if (foreignKey.columns().length != foreignKey.foreignColumns().length) {
			if (messager != null) {
				messager.error(tableElement,
											 "ForeignKey constraint %s has %d local columns and %d foreign columns, but the counts must match",
											 foreignKey.constraintName(),
											 foreignKey.columns().length,
											 foreignKey.foreignColumns().length);
			}
			return null;
		}
		ImmutableList<String> localColumnNames = ImmutableList.copyOf(foreignKey.columns());
		Supplier<List<@Nullable TableColumnModel>> localModels = Suppliers.memoize(() -> {
			ArrayList<@Nullable TableColumnModel> result = new ArrayList<>();
			for (int i = 0; i < foreignKey.columns().length; i++) {
				String localName = foreignKey.columns()[i];
				TableColumnModel column = sqlNameColumnMap.get(localName);
				result.add(column);
				if (messager != null && !sqlNameColumnMap.containsKey(localName)) {
					messager.error(tableElement,
												 "ForeignKey constraint %s local column %d has name %s but there appears to be no column by that name",
												 foreignKey.constraintName(),
												 i,
												 sqlNameColumnMap);
				}
			}
			return result;
		});
		for (int i = 0; messager != null && i < foreignKey.foreignColumns().length; i++) {
			if (foreignKey.foreignColumns()[i].isEmpty()) {
				messager.error(tableElement,
											 "ForeignKey constraint %s foreign column %d is an empty string",
											 foreignKey.constraintName(),
											 i);
			}
		}
		ImmutableList<String> explicitForeignColumnNames = ImmutableList.copyOf(foreignKey.foreignColumns());
		TypeMirror foreignTableMirror = ProcessorHelpers.getTypeMirror(foreignKey::foreignTable);
		boolean isAutoGen = !environment.getTypeUtils().isSameType(foreignTableMirror, voidMirror);
		if (messager != null && isAutoGen && !foreignKey.tableIfNotAutogen().isEmpty()) {
			messager.error(
					tableElement,
					"ForeignKey constraint %s specifies both foreignTable and also tableIfNotAutogen. Only use foreignTable",
					foreignKey.constraintName());
		}
		if (isAutoGen) {
			return buildTableAutogen(environment,
															 messager,
															 tableElement,
															 createTable,
															 foreignKey,
															 localColumnNames,
															 localModels,
															 foreignTableMirror,
															 explicitForeignColumnNames,
															 pathToTableCache);
		}
		if (foreignKey.tableIfNotAutogen().isEmpty()) {
			if (messager != null) {
				messager.error(tableElement,
											 "ForeignKey constraint %s should specify a foreignTable or use tableIfNotAutogen",
											 foreignKey.constraintName());
			}
			return null;
		}
		return new ForeignKeyModel(Strings.emptyToNull(foreignKey.constraintName()),
															 localColumnNames,
															 localModels,
															 null,
															 foreignKey.tableIfNotAutogen(),
															 null,
															 explicitForeignColumnNames,
															 foreignKey.onUpdate(),
															 foreignKey.onDelete(),
															 foreignKey.deferred());
	}

	public static @Nullable ForeignKeyModel buildTableAutogen(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			CreateTable createTable,
			TableForeignKey foreignKey,
			ImmutableList<String> localColumnNames,
			Supplier<List<@Nullable TableColumnModel>> localModels,
			TypeMirror foreignTableMirror,
			ImmutableList<String> explicitForeignColumnNames,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		if (foreignTableMirror.getKind() != TypeKind.DECLARED) {
			if (messager != null) {
				messager.error(tableElement,
											 "ForeignKey constraint %s references remote table %s which does not appear to be a class or interface",
											 foreignKey.constraintName(),
											 foreignTableMirror);
			}
			return null;
		}
		DeclaredType foreignTableType = (DeclaredType) foreignTableMirror;
		TypeElement foreignTableElement = (TypeElement) foreignTableType.asElement();
		CreateTable foreignCreateTable = foreignTableElement.getAnnotation(CreateTable.class);
		if (foreignCreateTable == null) {
			if (messager != null) {
				messager.error(tableElement,
											 "ForeignKey constraint %s references remote table %s which does not appear to have the @CreateTable annotation",
											 foreignKey.constraintName(),
											 foreignTableMirror);
			}
			return null;
		}
		Supplier<CreateTableModel> foreignTableIfAutogen = Suppliers.memoize(() -> {
			CreateTableModel cached = pathToTableCache.get(foreignTableElement.toString());
			if (cached != null) {
				return cached;
			}
			return CreateTableModel.build(environment, messager, tableElement, foreignCreateTable, pathToTableCache);
		});
		Supplier<List<@Nullable TableColumnModel>>
				foreignColumnsIfAutogen =
				buildTableForeignColumnsIfAutogen(environment,
																					messager,
																					tableElement,
																					foreignKey,
																					localModels,
																					foreignTableIfAutogen);
		return new ForeignKeyModel(Strings.emptyToNull(foreignKey.constraintName()),
															 localColumnNames,
															 localModels,
															 foreignTableIfAutogen,
															 CreateTableModel.getJavaName(messager, createTable, tableElement),
															 foreignColumnsIfAutogen,
															 explicitForeignColumnNames,
															 foreignKey.onUpdate(),
															 foreignKey.onDelete(),
															 foreignKey.deferred());
	}

	private static Supplier<List<@Nullable TableColumnModel>> buildTableForeignColumnsIfAutogen(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			TableForeignKey foreignKey,
			Supplier<List<@Nullable TableColumnModel>> localModels,
			Supplier<CreateTableModel> foreignTableIfAutogen)
	{
		return Suppliers.memoize(() -> {
			CreateTableModel foreignTable = foreignTableIfAutogen.get();
			List<@Nullable TableColumnModel> localColumnList = localModels.get();
			List<@Nullable TableColumnModel> foreignColumnList = new ArrayList<>(foreignKey.foreignColumns().length);
			boolean allColumnsValid = true;
			for (int i = 0; foreignTable != null && i < foreignKey.foreignColumns().length; i++) {
				String foreignColumnName = foreignKey.foreignColumns()[i];
				TableColumnModel localColumn = localColumnList.get(i);
				TableColumnModel foreignColumn = foreignTable.columnSqlMap.get(foreignColumnName);
				foreignColumnList.set(i, foreignColumn);
				if (localColumn == null) {
					continue;
				}
				if (foreignColumn == null) {
					if (messager != null) {
						messager.error(tableElement,
													 "ForeignKey constraint %s references remote column %s which does not seem to exist",
													 foreignKey.constraintName(),
													 foreignColumnName);
					}
					allColumnsValid = false;
					continue;
				}
				if (messager != null && localColumn.affinity != foreignColumn.affinity) {
					messager.error(tableElement,
												 "ForeignKey constraint %s local column %s is %s, but references remote column %s which is %s",
												 foreignKey.constraintName(),
												 localColumn.sqlName,
												 localColumn.affinity,
												 foreignColumn.sqlName,
												 foreignColumn.affinity);
				}
			}
			if (messager != null && allColumnsValid && foreignTable != null) {
				ImmutableSet.Builder<String> foreignColumnSet = ImmutableSet.builder();
				for (int i = 0; i < foreignColumnList.size(); i++) {
					foreignColumnSet.add(Objects.requireNonNull(foreignColumnList.get(i)).sqlName);
				}
				if (!foreignTable.allUniques.contains(foreignColumnSet.build())) {
					if (foreignColumnList.size() == 1) {
						messager.error(tableElement,
													 "ForeignKey constraint %s refers to %s.%s, but that column is not unique or primary",
													 foreignKey.constraintName(),
													 foreignTable.sqlName,
													 Objects.requireNonNull(foreignColumnList.get(0)).sqlName);
					} else {
						messager.error(tableElement,
													 "ForeignKey constraint %s references columns that are not unique nor primary",
													 foreignKey.constraintName());
					}
				}
			}
			return foreignColumnList;
		});
	}

	public static @Nullable ForeignKeyModel buildForColumn(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			String columnSqlName,
			TableColumn column,
			HashMap<String, TableColumnModel> sqlNameColumnMap,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		TypeMirror voidMirror = environment.getElementUtils().getTypeElement(Void.class.getName()).asType();
		ColumnForeignKey foreignKey = column.foreignKey();
		TypeMirror foreignTableMirror = ProcessorHelpers.getTypeMirror(foreignKey::value);
		boolean isAutoGen = !environment.getTypeUtils().isSameType(foreignTableMirror, voidMirror);
		if (foreignKey.constraintName().isEmpty()
				&& !isAutoGen
				&& foreignKey.tableIfNotAutogen().isEmpty()
				&& foreignKey.column().isEmpty()
				&& foreignKey.onUpdate() == ForeignKeyAction.UNSPECIFIED
				&& foreignKey.onDelete() == ForeignKeyAction.UNSPECIFIED
				&& foreignKey.deferred() == ForeignKeyDeferMode.UNSPECIFIED)
		{
			return null;
		}
		if (messager != null && !StringHelpers.validOrEmptySqlId(foreignKey.constraintName())) {
			messager.error(tableElement,
										 "Column %s foreign key constraint has invalid name %s",
										 columnSqlName,
										 foreignKey.constraintName());
		}
		Supplier<TableColumnModel> localColumnModel = Suppliers.memoize(() -> {
			TableColumnModel model = sqlNameColumnMap.get(columnSqlName);
			if (model == null) {
				throw new IllegalStateException("local column not found");
			}
			return model;
		});
		Supplier<List<@Nullable TableColumnModel>> localColumnModelList = Suppliers.memoize(() -> {
			List<@Nullable TableColumnModel> result = new ArrayList<>();
			result.add(localColumnModel.get());
			return result;
		});
		ImmutableList<String>
				explicitForeignColumnNames =
				!foreignKey.column().isEmpty() ? ImmutableList.of(foreignKey.column()) : ImmutableList.of();
		if (messager != null && isAutoGen && !foreignKey.tableIfNotAutogen().isEmpty()) {
			messager.error(
					tableElement,
					"Column %s foreign key constraint specifies both foreignTable and also tableIfNotAutogen. Only use foreignTable",
					columnSqlName);
		}
		if (!isAutoGen && foreignKey.tableIfNotAutogen().isEmpty()) {
			if (messager != null) {
				messager.error(tableElement,
											 "Column %s foreign key should specify a foreign Table or use tableIfNotAutogen",
											 columnSqlName);
			}
			return null;
		}
		if (messager != null && isAutoGen && !foreignKey.tableIfNotAutogen().isEmpty()) {
			messager.error(tableElement,
										 "Column %s foreign key specifies a table already, should not use tableIfNotAutogen",
										 columnSqlName);
		}
		if (!isAutoGen) {
			return new ForeignKeyModel(Strings.emptyToNull(foreignKey.constraintName()),
																 ImmutableList.of(columnSqlName),
																 localColumnModelList,
																 null,
																 foreignKey.tableIfNotAutogen(),
																 null,
																 explicitForeignColumnNames,
																 foreignKey.onUpdate(),
																 foreignKey.onDelete(),
																 foreignKey.deferred());
		}

		if (foreignTableMirror.getKind() != TypeKind.DECLARED) {
			if (messager != null) {
				messager.error(tableElement,
											 "Column %s foreign key references remote table %s which does not appear to be a class or interface",
											 columnSqlName,
											 foreignTableMirror);
			}
			return null;
		}
		DeclaredType foreignTableType = (DeclaredType) foreignTableMirror;
		TypeElement foreignTableElement = (TypeElement) foreignTableType.asElement();
		CreateTable foreignCreateTable = foreignTableElement.getAnnotation(CreateTable.class);
		if (foreignCreateTable == null) {
			if (messager != null) {
				messager.error(tableElement,
											 "Column %s foreign key references remote table %s which does not appear to have the @CreateTable annotation",
											 columnSqlName,
											 foreignTableMirror);
			}
			return null;
		}
		Supplier<CreateTableModel> foreignTableIfAutogen = Suppliers.memoize(() -> {
			CreateTableModel cached = pathToTableCache.get(foreignTableElement.toString());
			if (cached != null) {
				return cached;
			}
			return CreateTableModel.build(environment, messager, tableElement, foreignCreateTable, pathToTableCache);
		});
		Supplier<List<@Nullable TableColumnModel>>
				foreignColumnsIfAutogen =
				buildColumnForeignColumnsIfAutogen(environment,
																					 messager,
																					 tableElement,
																					 foreignKey,
																					 localColumnModel,
																					 foreignTableIfAutogen);
		return new ForeignKeyModel(Strings.emptyToNull(foreignKey.constraintName()),
															 ImmutableList.of(columnSqlName),
															 localColumnModelList,
															 foreignTableIfAutogen,
															 CreateTableModel.getJavaName(messager, foreignCreateTable, tableElement),
															 foreignColumnsIfAutogen,
															 explicitForeignColumnNames,
															 foreignKey.onUpdate(),
															 foreignKey.onDelete(),
															 foreignKey.deferred());
	}

	private static Supplier<List<@Nullable TableColumnModel>> buildColumnForeignColumnsIfAutogen(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			ColumnForeignKey foreignKey,
			Supplier<TableColumnModel> localColumnModel,
			Supplier<CreateTableModel> foreignTableIfAutogen)
	{
		return Suppliers.memoize(() -> {
			CreateTableModel foreignTable = foreignTableIfAutogen.get();
			TableColumnModel localColumn = localColumnModel.get();
			TableColumnModel foreignColumn = foreignTable.columnSqlMap.get(foreignKey.column());
			if (foreignColumn == null) {
				if (messager != null) {
					messager.error(tableElement,
												 "Column %s foreign key references remote column %s which does not seem to exist",
												 localColumn.sqlName,
												 foreignKey.column());
				}
				return new ArrayList<>();
			}
			if (messager != null && localColumn.affinity != foreignColumn.affinity) {
				messager.error(tableElement,
											 "Column %s is %s, but foreign key references remote column %s which is %s",
											 localColumn.sqlName,
											 localColumn.affinity,
											 foreignColumn.sqlName,
											 foreignColumn.affinity);
			}
			ImmutableSet<String> foreignColumnSet = ImmutableSet.of(foreignColumn.sqlName);
			if (messager != null && !foreignTable.allUniques.contains(foreignColumnSet)) {
				messager.error(tableElement,
											 "Column %s foreign key refers to %s.%s, but that column is not unique or primary",
											 localColumn.sqlName,
											 foreignTable.sqlName,
											 foreignColumn.sqlName);
			}
			List<@Nullable TableColumnModel> result = new ArrayList<>();
			result.add(foreignColumn);
			return result;
		});
	}
}
