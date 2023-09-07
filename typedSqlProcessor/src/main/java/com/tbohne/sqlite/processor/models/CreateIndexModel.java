package com.tbohne.sqlite.processor.models;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tbohne.sqlite.annotations.CreateIndex;
import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * {@link com.tbohne.sqlite.annotations.CreateIndex}
 */
public class CreateIndexModel {
	public final boolean unique;
	public final String javaName;
	public final String sqlName;
	public final ImmutableList<IndexedColumnModel> columns;
	public final @Nullable String where;

	private CreateIndexModel(
			boolean unique,
			String javaName,
			String sqlName,
			ImmutableList<IndexedColumnModel> columns,
			@Nullable String where)
	{
		this.unique = unique;
		this.javaName = javaName;
		this.sqlName = sqlName;
		this.columns = columns;
		this.where = where;
	}

	public static ImmutableList<CreateIndexModel> buildList(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			CreateTable createTable,
			ImmutableMap<String, TableColumnModel> sqlNameColumnMap,
			HashMap<String, CreateIndexModel> nameIndexMap)
	{
		List<? extends Element> members = tableElement.getEnclosedElements();
		ImmutableList.Builder<CreateIndexModel> list = ImmutableList.builder();
		for (int i = 0; i < members.size(); i++) {
			Element rawElement = members.get(i);
			CreateIndex index = rawElement.getAnnotation(CreateIndex.class);
			if (index == null) {
				continue;
			}
			CreateIndexModel model = build(environment, messager, rawElement, index, sqlNameColumnMap);
			if (model == null) {
				continue;
			}
			if (messager != null && nameIndexMap.containsKey(model.sqlName)) {
				messager.error(
						tableElement,
						"table %s has multiple indecies named \"%s\"",
						createTable.sqlName(),
						index.name());
			}
			list.add(model);
			nameIndexMap.put(model.sqlName, model);
		}
		return list.build();
	}

	public static @Nullable CreateIndexModel build(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			Element indexElement,
			CreateIndex index,
			ImmutableMap<String, TableColumnModel> sqlNameColumnMap)
	{
		if (index.name().isEmpty() && !index.unique() && index.value().length == 0 && index.where().isEmpty()) {
			return null;
		}
		String javaName = getJavaName(messager, index, indexElement);
		String sqlName = getSqlName(messager, index, indexElement, javaName);
		ImmutableList<IndexedColumnModel>
				columns =
				IndexedColumnModel.buildList(environment,
																		 messager,
																		 indexElement,
																		 sqlNameColumnMap,
																		 index.name(),
																		 index.value());
		if (columns.size() == 0) {
			if (messager != null) {
				messager.error(indexElement, "index %s (%s) does not seem to contain columns", javaName);
			}
			return null;
		}
		return new CreateIndexModel(index.unique(), javaName, sqlName, columns, Strings.emptyToNull(index.where()));
	}

	static String getJavaName(@Nullable Output messager, CreateIndex index, Element indexElement) {
		if (index.name().isEmpty()) {
			return indexElement.getSimpleName().toString();
		}
		if (messager != null && !StringHelpers.validJavaId(index.name())) {
			messager.error(indexElement, "Index java name \"%s\" is invalid", index.name());
		}
		return index.name();
	}

	static String getSqlName(@Nullable Output messager, CreateIndex index, Element indexElement, String javaName) {
		if (index.sqlName().isEmpty()) {
			return javaName;
		}
		String sqlName = StringHelpers.javaToSql(javaName);
		if (messager != null && !StringHelpers.validJavaId(sqlName)) {
			messager.error(indexElement, "Index sql name \"%s\" is invalid", sqlName);
		}
		return sqlName;
	}
}
