package com.tbohne.sqlite.processor.models;

import static com.tbohne.sqlite.processor.util.ImmutableMethods.listToMap;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.createTable.TableUnique;
import com.tbohne.sqlite.annotations.enums.ConflictAction;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * {@link com.tbohne.sqlite.annotations.createTable.TableUnique}
 */
public class TableUniqueModel {
	public final @Nullable String constraintName;
	public final ImmutableList<IndexedColumnModel> columnList;
	public final ImmutableMap<String, IndexedColumnModel> columnMap;
	public final ConflictAction onConflict;

	private TableUniqueModel(
			@Nullable String constraintName,
			ImmutableList<IndexedColumnModel> columnList,
			ImmutableMap<String, IndexedColumnModel> columnMap,
			ConflictAction onConflict)
	{
		this.constraintName = constraintName;
		this.columnList = columnList;
		this.columnMap = columnMap;
		this.onConflict = onConflict;
	}

	public static ImmutableList<TableUniqueModel> buildList(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			CreateTable createTable,
			ImmutableMap<String, TableColumnModel> sqlNameColumnMap)
	{
		TableUnique[] uniques = createTable.unique();
		ImmutableList.Builder<TableUniqueModel> list = ImmutableList.builder();
		for (int i = 0; i < uniques.length; i++) {
			TableUnique unique = uniques[i];
			TableUniqueModel model = build(environment, messager, tableElement, createTable, unique, sqlNameColumnMap);
			if (model != null) {
				list.add(model);
			}
		}
		return list.build();
	}

	public static @Nullable TableUniqueModel build(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			CreateTable createTable,
			TableUnique unique,
			ImmutableMap<String, TableColumnModel> sqlNameColumnMap)
	{
		if (unique.constraintName().isEmpty()
				&& unique.value().length == 0
				&& unique.onConflict() == ConflictAction.UNSPECIFIED)
		{
			return null;
		}
		if (unique.value().length == 0) {
			if (messager != null) {
				messager.error(tableElement,
											 "Table unique constraint %s has a name or onConflict but no unique columns",
											 unique.constraintName());
			}
			return null;
		}
		if (messager != null && !StringHelpers.validOrEmptySqlId(unique.constraintName())) {
			messager.error(tableElement, "Table unique constraint %s has invalid name", unique.constraintName());
		}
		ImmutableList<IndexedColumnModel>
				columnList =
				IndexedColumnModel.buildList(environment,
																		 messager,
																		 tableElement,
																		 sqlNameColumnMap,
																		 unique.constraintName(),
																		 unique.value());
		ImmutableMap<String, IndexedColumnModel> columnMap = listToMap(columnList, i -> i.sqlColumnName);
		return new TableUniqueModel(
				Strings.emptyToNull(unique.constraintName()),
				columnList,
				columnMap,
				unique.onConflict());
	}
}
