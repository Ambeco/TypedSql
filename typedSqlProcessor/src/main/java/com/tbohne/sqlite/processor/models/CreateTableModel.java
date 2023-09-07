package com.tbohne.sqlite.processor.models;

import static com.tbohne.sqlite.processor.util.ImmutableMethods.listTransform;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.enums.Collation;
import com.tbohne.sqlite.processor.util.ImmutableMethods;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * {@link com.tbohne.sqlite.annotations.CreateTable}
 */
public class CreateTableModel {
	public final boolean temporary;
	public final @Nullable String schemaName;
	public final String javaName;
	public final String sqlName;
	public final String rowName;
	public final ImmutableList<TableColumnModel> columnList;
	public final ImmutableMap<String, TableColumnModel> columnSqlMap;
	public final @Nullable TablePrimaryKeyModel primaryKeyConstraint;
	public final ImmutableList<TableUniqueModel> explicitUniques;
	public final ImmutableList<TableCheckModel> checks;
	public final ImmutableList<ForeignKeyModel> foreignKeys;
	public final boolean withoutRowId;

	public final ImmutableList<CreateIndexModel> explicitIndecies;
	public final ImmutableMap<String, CreateIndexModel> sqlIndexMap;

	public final ImmutableList<IndexedColumnModel> primaryColumns;
	public final ImmutableSet<ImmutableSet<String>> allUniques;
	public final ImmutableMap<String, List<ImmutableList<String>>> sqlColumnToIndexMap;
	public final ImmutableList<JavaBinderModel<TableColumnModel>> javaColumns;
	public final ImmutableMap<String, JavaBinderModel<TableColumnModel>> javaColumnToBinderMap;
	public final ImmutableList<JavaBinderModel<TableColumnModel>> binders;
	public final ImmutableMap<String, JavaBinderModel<TableColumnModel>> binderNameToBinderMap;

	private CreateTableModel(
			boolean temporary,
			@Nullable String schemaName,
			String javaName,
			String sqlName,
			String rowName,
			ImmutableList<TableColumnModel> columnSqlMap,
			ImmutableMap<String, TableColumnModel> columnMap,
			@Nullable TablePrimaryKeyModel primaryKeyConstraint,
			ImmutableList<TableUniqueModel> explicitUniques,
			ImmutableList<TableCheckModel> checks,
			ImmutableList<ForeignKeyModel> foreignKeys,
			boolean withoutRowId,
			ImmutableList<CreateIndexModel> explicitIndecies,
			ImmutableMap<String, CreateIndexModel> sqlIndexMap,
			ImmutableList<IndexedColumnModel> primaryColumns,
			ImmutableSet<ImmutableSet<String>> allUniques,
			ImmutableMap<String, List<ImmutableList<String>>> sqlColumnToIndexMap,
			ImmutableList<JavaBinderModel<TableColumnModel>> javaColumns,
			ImmutableMap<String, JavaBinderModel<TableColumnModel>> javaColumnToBinderMap,
			ImmutableList<JavaBinderModel<TableColumnModel>> binders,
			ImmutableMap<String, JavaBinderModel<TableColumnModel>> binderNameToBinderMap)
	{
		this.temporary = temporary;
		this.schemaName = schemaName;
		this.javaName = javaName;
		this.sqlName = sqlName;
		this.rowName = rowName;
		this.columnList = columnSqlMap;
		this.columnSqlMap = columnMap;
		this.primaryKeyConstraint = primaryKeyConstraint;
		this.explicitUniques = explicitUniques;
		this.checks = checks;
		this.foreignKeys = foreignKeys;
		this.withoutRowId = withoutRowId;
		this.explicitIndecies = explicitIndecies;
		this.sqlIndexMap = sqlIndexMap;
		this.primaryColumns = primaryColumns;
		this.allUniques = allUniques;
		this.sqlColumnToIndexMap = sqlColumnToIndexMap;
		this.javaColumns = javaColumns;
		this.javaColumnToBinderMap = javaColumnToBinderMap;
		this.binders = binders;
		this.binderNameToBinderMap = binderNameToBinderMap;
	}

	public static CreateTableModel build(
			ProcessingEnvironment environment,
			Output messager,
			TypeElement tableElement,
			CreateTable createTable,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		if (messager != null && !StringHelpers.validOrEmptySqlId(createTable.schemaName())) {
			messager.error(tableElement, "Schema name \"%s\" is invalid", createTable.schemaName());
		}
		String javaName = getJavaName(messager, createTable, tableElement);
		if (messager != null && !StringHelpers.validJavaId(createTable.singleRowName())) {
			messager.error(tableElement, "Table row name \"%s\" is invalid", createTable.singleRowName());
		}
		String sqlName = getSqlName(messager, createTable, tableElement, javaName);

		HashMap<String, TableColumnModel> sqlNameColumnMapBuilder = new HashMap<>();
		ImmutableList<TableColumnModel>
				columnList =
				TableColumnModel.buildList(environment,
																	 messager,
																	 tableElement,
																	 sqlName,
																	 javaName,
																	 createTable,
																	 sqlNameColumnMapBuilder,
																	 pathToTableCache);
		if (messager != null && columnList.isEmpty()) {
			messager.error(tableElement, "Sqlite Table must have at least one column");
		}
		for (int i = 0; i < columnList.size(); i++) {
			TableColumnModel column = columnList.get(i);
			if (messager != null && sqlNameColumnMapBuilder.get(column.sqlName) != null) {
				messager.error(tableElement, "Two columns have the same sqlName \"%s\"", column.sqlName);
			}
			sqlNameColumnMapBuilder.put(column.sqlName, column);
		}
		ImmutableMap<String, TableColumnModel> columnSqlMap = ImmutableMap.copyOf(sqlNameColumnMapBuilder);
		TablePrimaryKeyModel
				primaryKey =
				TablePrimaryKeyModel.build(environment, messager, tableElement, createTable, columnSqlMap);
		ImmutableList<TableUniqueModel>
				uniques =
				TableUniqueModel.buildList(environment, messager, tableElement, createTable, columnSqlMap);
		ImmutableList<TableCheckModel>
				checks =
				TableCheckModel.buildList(environment, messager, tableElement, createTable, columnSqlMap);
		ImmutableList<ForeignKeyModel>
				foreignKeys =
				ForeignKeyModel.buildList(environment, messager, tableElement, createTable, columnSqlMap, pathToTableCache);
		HashMap<String, CreateIndexModel> nameIndexMap = new HashMap<>();
		ImmutableList<CreateIndexModel>
				explicitIndecies =
				CreateIndexModel.buildList(environment, messager, tableElement, createTable, columnSqlMap, nameIndexMap);
		ImmutableList<IndexedColumnModel>
				primaryColumns =
				buildPrimaryList(environment, messager, tableElement, primaryKey, columnList);
		ImmutableSet<ImmutableSet<String>>
				allUnique =
				buildAllUniqueList(environment, messager, tableElement, columnList, primaryKey, uniques);
		ImmutableMap<String, List<ImmutableList<String>>>
				sqlColumnToIndexMap =
				buildAllIndexList(environment, messager, tableElement, columnList, primaryKey, uniques, explicitIndecies);
		ImmutableList<JavaBinderModel<TableColumnModel>>
				javaColumns =
				JavaBinderModel.buildJavaBinderList(environment, messager, tableElement, columnList);
		ImmutableMap<String, JavaBinderModel<TableColumnModel>>
				javaNameBinderMap =
				ImmutableMethods.listToMap(javaColumns, c -> c.tableJavaName + "#" + c.columnJavaName);
		ImmutableList.Builder<JavaBinderModel<TableColumnModel>> binders = ImmutableList.builder();
		HashMap<String, JavaBinderModel<TableColumnModel>> binderNameBinderMap = new HashMap<>();
		for (int i = 0; i < javaColumns.size(); i++) {
			JavaBinderModel<TableColumnModel> binder = javaColumns.get(i);
			if (!binderNameBinderMap.containsKey(binder.binderName)) {
				binders.add(binder);
				binderNameBinderMap.put(binder.binderName, binder);
			}
		}
		CreateTableModel model = new CreateTableModel(createTable.temporary(),
																									Strings.emptyToNull(createTable.schemaName()),
																									javaName,
																									sqlName,
																									!createTable.singleRowName().isEmpty()
																									? createTable.singleRowName()
																									: javaName + "Row",
																									columnList,
																									columnSqlMap,
																									primaryKey,
																									uniques,
																									checks,
																									foreignKeys,
																									createTable.withoutRowId(),
																									explicitIndecies,
																									ImmutableMap.copyOf(nameIndexMap),
																									primaryColumns,
																									allUnique,
																									sqlColumnToIndexMap,
																									javaColumns,
																									javaNameBinderMap,
																									binders.build(),
																									ImmutableMap.copyOf(binderNameBinderMap));
		pathToTableCache.put(tableElement.getQualifiedName().toString(), model);
		return model;
	}

	static String getJavaName(@Nullable Output messager, CreateTable createTable, TypeElement tableElement) {
		if (createTable.javaName().isEmpty()) {
			if (messager != null) {
				messager.error(tableElement, "SqliteTable must have a table name");
			}
			return tableElement.getSimpleName().toString();
		} else {
			if (messager != null && !StringHelpers.validJavaId(createTable.javaName())) {
				messager.error(tableElement, "Table java name \"%s\" is invalid", createTable.javaName());
			}
			return createTable.javaName();
		}
	}

	static String getSqlName(
			@Nullable Output messager, CreateTable createTable, TypeElement tableElement, String javaName)
	{
		if (createTable.sqlName().isEmpty()) {
			if (messager != null && !StringHelpers.validOrEmptySqlId(javaName)) {
				messager.error(tableElement, "Table sql name \"%s\" is invalid", javaName);
			}
			return javaName;
		} else {
			String sqlName = StringHelpers.javaToSql(javaName);
			if (messager != null && !StringHelpers.validJavaId(sqlName)) {
				messager.error(tableElement, "Table sql name \"%s\" is invalid", sqlName);
			}
			return sqlName;
		}
	}

	private static ImmutableList<IndexedColumnModel> buildPrimaryList(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			@Nullable TablePrimaryKeyModel tablePrimaryKey,
			ImmutableList<TableColumnModel> columnList)
	{
		if (tablePrimaryKey != null) {
			return tablePrimaryKey.columns;
		}
		ImmutableList<IndexedColumnModel> result = ImmutableList.of();
		for (int i = 0; i < columnList.size(); i++) {
			TableColumnModel column = columnList.get(i);
			if (column.primaryKey != null) {
				if (messager != null && !result.isEmpty()) {
					messager.error(tableElement,
												 "Column %s and column %s both marked as primary key. Use table constraint instead",
												 result.get(0).sqlColumnName,
												 column.sqlName);
				}
				Collation collation = column.collation != null ? column.collation.collation : Collation.UNSPECIFIED;
				result =
						ImmutableList.of(new IndexedColumnModel(column.sqlName, () -> column, collation, column.primaryKey.order));
			}
		}
		return result;
	}

	private static ImmutableSet<ImmutableSet<String>> buildAllUniqueList(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			ImmutableList<TableColumnModel> columnList,
			@Nullable TablePrimaryKeyModel primaryKey,
			ImmutableList<TableUniqueModel> tableUniques)
	{
		HashSet<ImmutableSet<String>> set = new HashSet<>();
		for (int i = 0; i < columnList.size(); i++) {
			TableColumnModel column = columnList.get(i);
			if (column.unique != null || column.primaryKey != null) {
				set.add(ImmutableSet.of(column.sqlName));
			}
		}
		if (primaryKey != null) {
			ImmutableSet<String>
					primaryColumns =
					ImmutableSet.copyOf(listTransform(primaryKey.columns, i -> i.sqlColumnName));
			if (messager != null && primaryKey.columns.size() == 1 && set.contains(primaryColumns)) {
				messager.error(tableElement,
											 "Table primary key %s redundant, column is already primary key",
											 primaryKey.constraintName);
			}
			set.add(primaryColumns);
		}
		for (int i = 0; i < tableUniques.size(); i++) {
			TableUniqueModel tableUnique = tableUniques.get(i);
			ImmutableSet<String> columnSet = tableUnique.columnMap.keySet();
			if (messager != null && set.contains(columnSet)) {
				messager.error(tableElement,
											 "Table Unique constraint %s is redundant, columns were already unique or primary",
											 tableUnique.constraintName);
			}
			set.add(columnSet);
		}
		return ImmutableSet.copyOf(set);
	}

	private static ImmutableMap<String, List<ImmutableList<String>>> buildAllIndexList(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			ImmutableList<TableColumnModel> columnList,
			@Nullable TablePrimaryKeyModel primaryKey,
			ImmutableList<TableUniqueModel> tableUniques,
			ImmutableList<CreateIndexModel> tableIndecies)
	{
		HashMap<String, List<ImmutableList<String>>> map = new HashMap<>();
		for (int i = 0; i < columnList.size(); i++) {
			TableColumnModel column = columnList.get(i);
			if (column.unique != null || column.primaryKey != null) {
				List<ImmutableList<String>> indeciesForFirstColumn = new ArrayList<>(1);
				indeciesForFirstColumn.add(ImmutableList.of(column.sqlName));
				map.put(column.sqlName, indeciesForFirstColumn);
			}
		}
		if (primaryKey != null) {
			ImmutableList<String> primaryColumns = listTransform(primaryKey.columns, c -> c.sqlColumnName);
			List<ImmutableList<String>> indeciesForFirstColumn = map.getOrDefault(primaryColumns.get(0), new ArrayList<>());
			indeciesForFirstColumn.add(primaryColumns);
		}
		for (int i = 0; i < tableUniques.size(); i++) {
			TableUniqueModel tableUnique = tableUniques.get(i);
			ImmutableList<String> uniqueColumns = listTransform(tableUnique.columnList, c -> c.sqlColumnName);
			List<ImmutableList<String>> indeciesForFirstColumn = map.getOrDefault(uniqueColumns.get(0), new ArrayList<>());
			indeciesForFirstColumn.add(uniqueColumns);
		}
		for (int i = 0; i < tableIndecies.size(); i++) {
			CreateIndexModel index = tableIndecies.get(i);
			ImmutableList<String> indecies = listTransform(index.columns, c -> c.sqlColumnName);
			List<ImmutableList<String>> indeciesForFirstColumn = map.getOrDefault(indecies.get(0), new ArrayList<>());
			indeciesForFirstColumn.add(indecies);
		}
		return ImmutableMap.copyOf(map);

	}
}

