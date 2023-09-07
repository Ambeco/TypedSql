package com.tbohne.sqlite.processor.models;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.createTable.TablePrimaryKey;
import com.tbohne.sqlite.annotations.enums.ConflictAction;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * {@link com.tbohne.sqlite.annotations.createTable.TablePrimaryKey}
 */
public class TablePrimaryKeyModel {
	public final @Nullable String constraintName;
	public final ImmutableList<IndexedColumnModel> columns;
	public final ConflictAction onConflict;

	private TablePrimaryKeyModel(
			@Nullable String constraintName, ImmutableList<IndexedColumnModel> columns, ConflictAction onConflict)
	{
		this.constraintName = constraintName;
		this.columns = columns;
		this.onConflict = onConflict;
	}

	public static @Nullable TablePrimaryKeyModel build(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			CreateTable createTable,
			ImmutableMap<String, TableColumnModel> sqlNameColumnMap)
	{
		TablePrimaryKey primaryKey = createTable.primaryKey();
		if (primaryKey.constraintName().isEmpty()
				&& primaryKey.value().length == 0
				&& primaryKey.onConflict() == ConflictAction.UNSPECIFIED)
		{
			return null;
		}
		if (primaryKey.value().length == 0) {
			if (messager != null && tableElement != null && !primaryKey.constraintName().isEmpty()) {
				messager.error(tableElement,
											 "table has no PrimaryKey columns, but named the primaryKey constraint \"%s\"",
											 primaryKey.constraintName());
			}
		}
		if (messager != null && tableElement != null && !StringHelpers.validOrEmptySqlId(primaryKey.constraintName())) {
			messager.error(tableElement, "table has invalid PrimaryKey constraint name \"%s\"", primaryKey.constraintName());
		}
		return new TablePrimaryKeyModel(Strings.emptyToNull(primaryKey.constraintName()),
																		IndexedColumnModel.buildList(environment,
																																 messager,
																																 tableElement,
																																 sqlNameColumnMap,
																																 primaryKey.constraintName(),
																																 primaryKey.value()),
																		primaryKey.onConflict());
	}
}
