package com.tbohne.sqlite.processor.models;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.tbohne.sqlite.annotations.createTable.ColumnCheck;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * {@link com.tbohne.sqlite.annotations.createTable.ColumnCheck}
 */
public class ColumnCheckModel {
	public final @Nullable String constraintName;
	public final String check;

	private ColumnCheckModel(
			@Nullable String constraintName, String check)
	{
		this.constraintName = constraintName;
		this.check = check;
	}

	public static ImmutableList<ColumnCheckModel> buildList(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			String columnName,
			TableColumn column)
	{
		ImmutableList.Builder<ColumnCheckModel> list = ImmutableList.builder();
		for (int i = 0; i < column.checks().length; i++) {
			ColumnCheck check = column.checks()[i];
			String name = Strings.emptyToNull(check.constraintName());
			if (messager != null && !StringHelpers.validOrEmptySqlId(check.constraintName())) {
				messager.error(tableElement,
											 "Column %s Check #d has invalid constraint name \"%s\"",
											 columnName,
											 i,
											 check.constraintName());
			}
			if (messager != null && check.value().isEmpty()) {
				messager.error(tableElement, "Column %s check #d \"%s\" has no value", columnName, i, name);
			}
			list.add(new ColumnCheckModel(name, check.value()));
		}
		return list.build();
	}
}
