package com.tbohne.sqlite.processor.models;


import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tbohne.sqlite.annotations.CreateProjection;
import com.tbohne.sqlite.processor.util.ImmutableMethods;
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
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * {@link CreateProjection}
 */
public class CreateProjectionModel {
	public final String projectionName;
	public final String rowName;
	public final String javaCursorName;
	public final String sqlCursorName;
	public final Supplier<List<@Nullable CreateTableModel>> tables;
	public final ImmutableList<ProjectionExpressionModel> expressions;
	public final Supplier<ImmutableList<AbstractColumnModel>> allColumns;
	public final Supplier<ImmutableList<JavaBinderModel<AbstractColumnModel>>> allJavaBinders;
	public final Supplier<ImmutableMap<String, JavaBinderModel<AbstractColumnModel>>> javaColumnToBinderMap;

	private CreateProjectionModel(
			String projectionName,
			String rowName,
			String javaCursorName,
			String sqlCursorName,
			Supplier<List<@Nullable CreateTableModel>> tables,
			ImmutableList<ProjectionExpressionModel> expressions,
			Supplier<ImmutableList<AbstractColumnModel>> allColumns,
			Supplier<ImmutableList<JavaBinderModel<AbstractColumnModel>>> allJavaBinders,
			Supplier<ImmutableMap<String, JavaBinderModel<AbstractColumnModel>>> javaColumnToBinderMap)
	{
		this.projectionName = projectionName;
		this.rowName = rowName;
		this.javaCursorName = javaCursorName;
		this.sqlCursorName = sqlCursorName;
		this.tables = tables;
		this.expressions = expressions;
		this.allColumns = allColumns;
		this.allJavaBinders = allJavaBinders;
		this.javaColumnToBinderMap = javaColumnToBinderMap;
	}

	public static CreateProjectionModel build(
			ProcessingEnvironment environment,
			Output messager,
			TypeElement projectionElement,
			CreateProjection createProjection,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		String projectionName = getProjectionName(messager, createProjection, projectionElement);
		String rowName = getRowName(messager, createProjection, projectionElement);
		String javaCursorName = getJavaCursorName(messager, createProjection, projectionElement);
		String sqlCursorName = getSqlCursorName(messager, createProjection, projectionElement);
		ImmutableList<ProjectionExpressionModel>
				expressions =
				ProjectionExpressionModel.buildList(environment, messager, projectionElement, createProjection);
		Supplier<List<@Nullable CreateTableModel>>
				tables =
				buildTableList(environment, messager, projectionElement, createProjection, pathToTableCache);
		Supplier<ImmutableList<AbstractColumnModel>>
				allColumns =
				Suppliers.memoize(() -> buildColumnList(environment,
																								messager,
																								projectionElement,
																								createProjection,
																								expressions,
																								pathToTableCache));
		Supplier<ImmutableList<JavaBinderModel<AbstractColumnModel>>>
				allJavaBinders =
				Suppliers.memoize(() -> JavaBinderModel.buildJavaBinderList(environment,
																																		messager,
																																		projectionElement,
																																		Objects.requireNonNull(allColumns.get())));
		Supplier<ImmutableMap<String, JavaBinderModel<AbstractColumnModel>>>
				javaColumnToBinderMap =
				Suppliers.memoize(() -> ImmutableMethods.listToMap(
						Objects.requireNonNull(allJavaBinders.get()),
						c -> c.columnJavaName));

		return new CreateProjectionModel(projectionName,
																		 rowName,
																		 javaCursorName,
																		 sqlCursorName,
																		 tables,
																		 expressions,
																		 allColumns,
																		 allJavaBinders,
																		 javaColumnToBinderMap);
	}

	private static Supplier<List<@Nullable CreateTableModel>> buildTableList(
			ProcessingEnvironment environment,
			Output messager,
			TypeElement projectionElement,
			CreateProjection createProjection,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		return () -> {
			List<@Nullable CreateTableModel> tableList = new ArrayList<>(createProjection.tables().length);
			for (int i = 0; i < createProjection.tables().length; i++) {
				int tableIndex = i;
				TypeMirror tableMirror = ProcessorHelpers.getTypeMirror(() -> createProjection.tables()[tableIndex].value());
				tableList.set(i, pathToTableCache.get(tableMirror.toString()));
			}
			return tableList;
		};
	}

	private static ImmutableList<AbstractColumnModel> buildColumnList(
			ProcessingEnvironment environment,
			Output messager,
			TypeElement projectionElement,
			CreateProjection createProjection,
			ImmutableList<ProjectionExpressionModel> expressions,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		ImmutableList.Builder<AbstractColumnModel> list = ImmutableList.builder();
		for (int i = 0; i < createProjection.tables().length; i++) {
			list.addAll(ProjectionTableColumnModel.build(environment,
																									 messager,
																									 projectionElement,
																									 createProjection,
																									 i,
																									 createProjection.tables()[i],
																									 pathToTableCache));
		}
		list.addAll(expressions);
		return list.build();
	}

	static String getProjectionName(
			@Nullable Output messager, CreateProjection createProjection, TypeElement projectionElement)
	{
		if (createProjection.projectionName().isEmpty()) {
			Element enclosing = projectionElement.getEnclosingElement();
			return projectionElement.getSimpleName().toString() + (enclosing.getKind() == ElementKind.CLASS
																														 ? ""
																														 : "Projection");
		} else {
			if (messager != null && !StringHelpers.validJavaId(createProjection.projectionName())) {
				messager.error(projectionElement, "Projection name \"%s\" is invalid", createProjection.projectionName());
			}
			return createProjection.projectionName();
		}
	}

	static String getRowName(
			@Nullable Output messager, CreateProjection createProjection, TypeElement projectionElement)
	{
		if (createProjection.rowName().isEmpty()) {
			Element enclosing = projectionElement.getEnclosingElement();
			return projectionElement.getSimpleName().toString() + "Row";
		} else {
			if (messager != null && !StringHelpers.validJavaId(createProjection.rowName())) {
				messager.error(projectionElement, "Row name \"%s\" is invalid", createProjection.rowName());
			}
			return createProjection.rowName();
		}
	}

	static String getJavaCursorName(
			@Nullable Output messager, CreateProjection createProjection, TypeElement projectionElement)
	{
		if (createProjection.cursorName().isEmpty()) {
			Element enclosing = projectionElement.getEnclosingElement();
			return projectionElement.getSimpleName().toString() + "Cursor";
		} else {
			if (messager != null && !StringHelpers.validJavaId(createProjection.cursorName())) {
				messager.error(projectionElement, "Cursor name \"%s\" is invalid", createProjection.cursorName());
			}
			return createProjection.cursorName();
		}
	}

	static String getSqlCursorName(
			@Nullable Output messager, CreateProjection createProjection, TypeElement projectionElement)
	{
		if (createProjection.cursorName().isEmpty()) {
			Element enclosing = projectionElement.getEnclosingElement();
			return projectionElement.getSimpleName().toString() + "RawSqlCursor";
		} else {
			if (messager != null && !StringHelpers.validJavaId(createProjection.rawCursorName())) {
				messager.error(projectionElement, "RawSqlCursor name \"%s\" is invalid", createProjection.cursorName());
			}
			return createProjection.cursorName();
		}
	}
}

