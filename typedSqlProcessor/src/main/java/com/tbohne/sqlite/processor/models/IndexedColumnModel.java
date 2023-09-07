package com.tbohne.sqlite.processor.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tbohne.sqlite.annotations.createTable.IndexedColumn;
import com.tbohne.sqlite.annotations.enums.Collation;
import com.tbohne.sqlite.annotations.enums.KeyOrder;
import com.tbohne.sqlite.processor.util.Output;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.function.Supplier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * {@link com.tbohne.sqlite.annotations.createTable.IndexedColumn}
 */
public class IndexedColumnModel {
	public final String sqlColumnName;
	public final Supplier<@Nullable TableColumnModel> column;
	public final Collation collation;
	public final KeyOrder keyOrder;

	public IndexedColumnModel(
			String sqlColumnName, Supplier<@Nullable TableColumnModel> column, Collation collation, KeyOrder keyOrder)
	{
		this.sqlColumnName = sqlColumnName;
		this.column = column;
		this.collation = collation;
		this.keyOrder = keyOrder;
	}

	public static ImmutableList<IndexedColumnModel> buildList(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			Element tableElement,
			ImmutableMap<String, TableColumnModel> sqlNameColumnMapBuilder,
			String constraintName,
			IndexedColumn[] indexedColumns)
	{
		ImmutableList.Builder<IndexedColumnModel> list = ImmutableList.builder();
		for (int i = 0; i < indexedColumns.length; i++) {
			IndexedColumn column = indexedColumns[i];
			TableColumnModel foundColumn = sqlNameColumnMapBuilder.get(column.value());
			if (messager != null && tableElement != null && foundColumn == null) {
				messager.error(tableElement, "table %s does not seem to contain column \"%s\"", constraintName, column.value());
			}
			list.add(new IndexedColumnModel(column.value(), () -> foundColumn, column.collation(), column.order()));
		}
		return list.build();
	}

	public static ImmutableMap<String, IndexedColumnModel> buildMap(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			ImmutableMap<String, TableColumnModel> sqlNameColumnMapBuilder,
			String constraintName,
			IndexedColumn[] indexedColumns)
	{
		HashMap<String, IndexedColumnModel> map = new HashMap<>(indexedColumns.length);
		for (int i = 0; i < indexedColumns.length; i++) {
			IndexedColumn column = indexedColumns[i];
			TableColumnModel foundColumn = sqlNameColumnMapBuilder.get(column.value());
			if (messager != null && tableElement != null && foundColumn == null) {
				messager.error(tableElement,
											 "table does not seem to contain column \"%s\" from constraint %s",
											 column.value(),
											 constraintName);
			}
			if (messager != null && map.containsKey(column.value())) {
				messager.error(tableElement, "table %s contains column \"%s\" multiple times", constraintName, column.value());
			}
			map.put(column.value(),
							new IndexedColumnModel(column.value(), () -> foundColumn, column.collation(), column.order()));
		}
		return ImmutableMap.copyOf(map);
	}
}
