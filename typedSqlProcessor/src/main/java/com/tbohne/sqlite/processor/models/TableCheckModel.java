package com.tbohne.sqlite.processor.models;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.createTable.TableCheck;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * {@link com.tbohne.sqlite.annotations.createTable.TableCheck}
 */
public class TableCheckModel {
	public final @Nullable String constraintName;
	public final String check;

	private TableCheckModel(
			@Nullable String constraintName, String check)
	{
		this.constraintName = constraintName;
		this.check = check;
	}

	public static ImmutableList<TableCheckModel> buildList(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			CreateTable createTable,
			ImmutableMap<String, TableColumnModel> sqlNameColumnMap)
	{
		TableCheck[] checks = createTable.checks();
		ImmutableList.Builder<TableCheckModel> list = ImmutableList.builder();
		for (int i = 0; i < checks.length; i++) {
			TableCheck check = checks[i];
			TableCheckModel model = build(environment, messager, tableElement, createTable, check, sqlNameColumnMap);
			if (model != null) {
				list.add(model);
			}
		}
		return list.build();
	}

	public static @Nullable TableCheckModel build(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			CreateTable createTable,
			TableCheck check,
			ImmutableMap<String, TableColumnModel> sqlNameColumnMap)
	{
		if (check.value().isEmpty() && check.constraintName().isEmpty()) {
			return null;
		}
		if (check.value().isEmpty()) {
			if (messager != null) {
				messager.error(tableElement, "Table check %s has a name but no check", check.constraintName());
			}
			return null;
		}
		if (messager != null && StringHelpers.validOrEmptySqlId(check.constraintName())) {
			messager.error(tableElement, "Table check %s has invalid name", check.constraintName());
		}
		return new TableCheckModel(Strings.emptyToNull(check.constraintName()), check.value());
	}
}
