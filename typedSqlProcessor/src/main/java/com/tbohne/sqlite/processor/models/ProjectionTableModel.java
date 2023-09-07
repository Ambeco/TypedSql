package com.tbohne.sqlite.processor.models;

import com.google.common.collect.ImmutableList;
import com.tbohne.sqlite.annotations.CreateIndex;
import com.tbohne.sqlite.annotations.CreateProjection;
import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.CreateView;
import com.tbohne.sqlite.annotations.query.ProjectionTable;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.ProcessorHelpers;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * {@link ProjectionTable}
 */
public class ProjectionTableModel {
	public final DeclaredType tableViewIndex;
	public final @Nullable String sqlAlias;
	public final ImmutableList<String> columnNames;
	public final List<@Nullable TableColumnModel> columnModels;

	private ProjectionTableModel(
			@Nullable DeclaredType tableViewIndex,
			@Nullable String sqlAlias,
			ImmutableList<String> columnNames,
			List<@Nullable TableColumnModel> columnModels)
	{
		this.tableViewIndex = tableViewIndex;
		this.sqlAlias = sqlAlias;
		this.columnNames = columnNames;
		this.columnModels = columnModels;
	}

	public static ImmutableList<ProjectionTableModel> buildList(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement projectionElement,
			CreateProjection createProjection,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		ProjectionTable[] tables = createProjection.tables();
		ImmutableList.Builder<ProjectionTableModel> list = ImmutableList.builder();
		for (int i = 0; i < tables.length; i++) {
			ProjectionTable table = tables[i];
			ProjectionTableModel
					model =
					build(environment, messager, projectionElement, createProjection, i, table, pathToTableCache);
			if (model != null) {
				list.add(model);
			}
		}
		return list.build();
	}

	public static ProjectionTableModel build(
			ProcessingEnvironment environment,
			Output messager,
			TypeElement projectionElement,
			CreateProjection createProjection,
			int i,
			ProjectionTable projectionTable,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		if (messager != null && !StringHelpers.validOrEmptySqlId(projectionTable.tableAlias())) {
			messager.error(projectionElement, "Projection table #%d name \"%s\" is invalid", i, projectionTable.tableAlias());
		}
		DeclaredType tableViewIndex = (DeclaredType) ProcessorHelpers.getTypeMirror(projectionTable::value);
		CreateTable remoteTable = tableViewIndex.getAnnotation(CreateTable.class);
		if (remoteTable != null) {
			return buildTable(environment,
												messager,
												projectionElement,
												createProjection,
												i,
												projectionTable,
												tableViewIndex,
												remoteTable,
												pathToTableCache);
		}
		CreateView remoteView = tableViewIndex.getAnnotation(CreateView.class);
		if (remoteView != null) {
			return buildView(environment,
											 messager,
											 projectionElement,
											 createProjection,
											 i,
											 projectionTable,
											 tableViewIndex,
											 remoteView,
											 pathToTableCache);
		}
		CreateIndex remoteIndex = tableViewIndex.getAnnotation(CreateIndex.class);
		if (remoteIndex != null) {
			return buildIndex(environment,
												messager,
												projectionElement,
												createProjection,
												i,
												projectionTable,
												tableViewIndex,
												remoteIndex,
												pathToTableCache);
		}
		if (messager != null) {
			messager.error(projectionElement,
										 "Projection table #%d %s references remote table %s which does not appear to have the @CreateTable, @CreateView, or @CreateIndex annotation",
										 i,
										 createProjection.projectionName(),
										 tableViewIndex);
		}
		return null;
	}

	private static ProjectionTableModel buildTable(
			ProcessingEnvironment environment,
			Output messager,
			TypeElement projectionElement,
			CreateProjection createProjection,
			int i,
			ProjectionTable projectionTable,
			DeclaredType remoteMirror,
			CreateTable remoteTable,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		TypeElement remoteElement = ((TypeElement) remoteMirror.asElement());
		CreateTableModel cachedRemoteTableModel = pathToTableCache.get(remoteElement.getQualifiedName().toString());
		CreateTableModel remoteTableModel;
		if (cachedRemoteTableModel != null) {
			remoteTableModel = cachedRemoteTableModel;
		} else {
			remoteTableModel = CreateTableModel.build(environment, messager, remoteElement, remoteTable, pathToTableCache);
			pathToTableCache.put(remoteElement.getQualifiedName().toString(), remoteTableModel);
		}
		List<@Nullable TableColumnModel> columnModels = new ArrayList<>(projectionTable.columns().length);
		for (int j = 0; j < projectionTable.columns().length; j++) {
			TableColumnModel columnMode = remoteTableModel.columnSqlMap.get(projectionTable.columns()[j]);
			columnModels.add(columnMode);
			if (messager != null && !remoteTableModel.columnSqlMap.containsKey(projectionTable.columns()[j])) {
				messager.error(projectionElement,
											 "Projection table #%d %s references remote table %s which does not appear to a %s column",
											 i,
											 createProjection.projectionName(),
											 remoteElement,
											 projectionTable.columns()[j]);
			}
		}
		return new ProjectionTableModel(remoteMirror,
																		projectionTable.tableAlias(),
																		ImmutableList.copyOf(projectionTable.columns()),
																		columnModels);
	}

	private static @Nullable ProjectionTableModel buildIndex(
			ProcessingEnvironment environment,
			Output messager,
			TypeElement projectionElement,
			CreateProjection createProjection,
			int i,
			ProjectionTable projectionTable,
			DeclaredType remoteIndexMirror,
			CreateIndex remoteIndex,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		TypeMirror remoteTableMirror = remoteIndexMirror.getEnclosingType();
		if (remoteTableMirror == null) {
			messager.error(projectionElement,
										 "Projection table #%d %s references remote index %s which is not a member of a @CreateTable",
										 i,
										 createProjection.projectionName(),
										 remoteIndexMirror);
			return null;
		}
		CreateTable remoteTable = remoteTableMirror.getAnnotation(CreateTable.class);
		if (remoteTable == null) {
			messager.error(projectionElement,
										 "Projection table #%d %s references remote index %s which is a member of %s, which does not appear to be a @CreateTable",
										 i,
										 createProjection.projectionName(),
										 remoteIndexMirror);
			return null;
		}
		TypeElement remoteTableElement = (TypeElement) ((DeclaredType) remoteTable).asElement();
		CreateTableModel cachedRemoteTableModel = pathToTableCache.get(remoteTableElement.getQualifiedName().toString());
		CreateTableModel remoteTableModel;
		if (cachedRemoteTableModel != null) {
			remoteTableModel = cachedRemoteTableModel;
		} else {
			remoteTableModel =
					CreateTableModel.build(environment, messager, remoteTableElement, remoteTable, pathToTableCache);
			pathToTableCache.put(remoteTableElement.getQualifiedName().toString(), remoteTableModel);
		}
		CreateIndexModel remoteIndexModel = remoteTableModel.sqlIndexMap.get(remoteIndex.sqlName());
		List<@Nullable TableColumnModel> columnModels = new ArrayList<>(projectionTable.columns().length);
		for (int j = 0; j < projectionTable.columns().length; j++) {
			TableColumnModel columnMode = remoteTableModel.columnSqlMap.get(projectionTable.columns()[j]);
			columnModels.add(columnMode);
			if (messager != null && !remoteTableModel.columnSqlMap.containsKey(projectionTable.columns()[j])) {
				messager.error(projectionElement,
											 "Projection table #%d %s references remote index %s which does not appear to have a %s column",
											 i,
											 createProjection.projectionName(),
											 remoteIndex.sqlName(),
											 projectionTable.columns()[j]);
			}
		}
		return new ProjectionTableModel(remoteIndexMirror,
																		projectionTable.tableAlias(),
																		ImmutableList.copyOf(projectionTable.columns()),
																		columnModels);
	}

	private static @Nullable ProjectionTableModel buildView(
			ProcessingEnvironment environment,
			Output messager,
			TypeElement projectionElement,
			CreateProjection createProjection,
			int i,
			ProjectionTable projectionTable,
			DeclaredType remoteViewMirror,
			CreateView remoteView,
			HashMap<String, CreateTableModel> pathToTableCache)
	{
		//TODO: implement
		throw new NotImplementedException();
	}
}

