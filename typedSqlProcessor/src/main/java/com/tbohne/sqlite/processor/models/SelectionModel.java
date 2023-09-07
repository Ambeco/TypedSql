package com.tbohne.sqlite.processor.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class SelectionModel {

	public final String selectionName;
	public final String rowName;
	public final String javaCursorName;
	public final String sqlCursorName;
	public final Supplier<List<@Nullable CreateTableModel>> tables;
	public final ImmutableList<ProjectionExpressionModel> expressions;
	public final Supplier<ImmutableList<AbstractColumnModel>> allColumns;
	public final Supplier<ImmutableList<JavaBinderModel<AbstractColumnModel>>> allJavaBinders;
	public final Supplier<ImmutableMap<String, JavaBinderModel<AbstractColumnModel>>> javaColumnToBinderMap;

	private SelectionModel(
			String selectionName,
			String rowName,
			String javaCursorName,
			String sqlCursorName,
			Supplier<List<@Nullable CreateTableModel>> tables,
			ImmutableList<ProjectionExpressionModel> expressions,
			Supplier<ImmutableList<AbstractColumnModel>> allColumns,
			Supplier<ImmutableList<JavaBinderModel<AbstractColumnModel>>> allJavaBinders,
			Supplier<ImmutableMap<String, JavaBinderModel<AbstractColumnModel>>> javaColumnToBinderMap)
	{
		this.selectionName = selectionName;
		this.rowName = rowName;
		this.javaCursorName = javaCursorName;
		this.sqlCursorName = sqlCursorName;
		this.tables = tables;
		this.expressions = expressions;
		this.allColumns = allColumns;
		this.allJavaBinders = allJavaBinders;
		this.javaColumnToBinderMap = javaColumnToBinderMap;
	}

	//    public static SelectionModel build(
	//            ProcessingEnvironment environment,
	//            Output messager,
	//            TypeElement selectionElement,
	//            Selection selection,
	//            HashMap<String, CreateTableModel> pathToTableCache) {
	//        String selectionName = getProjectionName(messager, selection, selectionElement);
	//        String rowName = getRowName(messager, selection, selectionElement);
	//        String javaCursorName = getJavaCursorName(messager, selection, selectionElement);
	//        String sqlCursorName = getSqlCursorName(messager, selection, selectionElement);
	//        ImmutableList<ProjectionExpressionModel> expressions = ProjectionExpressionModel.buildList(environment, messager, selectionElement, selection);
	//        Supplier<List<@Nullable CreateTableModel>> tables = buildTableList(environment, messager, selectionElement, selection, pathToTableCache);
	//        Supplier<ImmutableList<AbstractColumnModel>> allColumns = Suppliers.memoize(() ->
	//                buildColumnList(environment, messager, selectionElement, selection, expressions, pathToTableCache));
	//        Supplier<ImmutableList<JavaBinderModel<AbstractColumnModel>>> allJavaBinders = Suppliers.memoize(() ->
	//                JavaBinderModel.buildJavaBinderList(environment, messager, selectionElement, Objects.requireNonNull(allColumns.get())));
	//        Supplier<ImmutableMap<String, JavaBinderModel<AbstractColumnModel>>> javaColumnToBinderMap = Suppliers.memoize(() ->
	//                ImmutableMethods.listToMap(Objects.requireNonNull(allJavaBinders.get()), c -> c.columnJavaName));
	//
	//        return new SelectionModel(
	//                selectionName,
	//                rowName,
	//                javaCursorName,
	//                sqlCursorName,
	//                tables,
	//                expressions,
	//                allColumns,
	//                allJavaBinders,
	//                javaColumnToBinderMap);
	//    }
	//
	//    private static Supplier<List<@Nullable CreateTableModel>> buildTableList(
	//            ProcessingEnvironment environment,
	//            Output messager,
	//            TypeElement selectionElement,
	//            Selection selection,
	//            HashMap<String, CreateTableModel> pathToTableCache) {
	//        return () -> {
	//            List<@Nullable CreateTableModel> tableList = new ArrayList<>(selection.tables().length);
	//            for (int i = 0; i < selection.tables().length; i++) {
	//                int tableIndex = i;
	//                TypeMirror tableMirror = ProcessorHelpers.getTypeMirror(() -> selection.tables()[tableIndex].value());
	//                tableList.set(i, pathToTableCache.get(tableMirror.toString()));
	//            }
	//            return tableList;
	//        };
	//    }
	//
	//    private static ImmutableList<AbstractColumnModel> buildColumnList(
	//            ProcessingEnvironment environment,
	//            Output messager,
	//            TypeElement selectionElement,
	//            Selection selection,
	//            ImmutableList<ProjectionExpressionModel> expressions,
	//            HashMap<String, CreateTableModel> pathToTableCache) {
	//        ImmutableList.Builder<AbstractColumnModel> list = ImmutableList.builder();
	//        for(int i = 0; i < selection.tables().length; i++) {
	//            list.addAll(ProjectionTableColumnModel.build(
	//                    environment,
	//                    messager,
	//                    selectionElement,
	//                    selection,
	//                    i,
	//                    selection.tables()[i],
	//                    pathToTableCache));
	//        }
	//        list.addAll(expressions);
	//        return list.build();
	//    }
	//
	//    static String getProjectionName(@Nullable Output messager, Selection selection, TypeElement selectionElement) {
	//        if (selection.selectionName().isEmpty()) {
	//            Element enclosing = selectionElement.getEnclosingElement();
	//            return selectionElement.getSimpleName().toString() +
	//                    (enclosing.getKind() == ElementKind.CLASS ? "" : "Selection");
	//        } else {
	//            if (messager != null && !StringHelpers.validJavaId(selection.selectionName()))
	//                messager.error(selectionElement, "Selection name \"%s\" is invalid", selection.selectionName());
	//            return selection.selectionName();
	//        }
	//    }
	//
	//    static String getRowName(@Nullable Output messager, Selection selection, TypeElement selectionElement) {
	//        if (selection.rowName().isEmpty()) {
	//            Element enclosing = selectionElement.getEnclosingElement();
	//            return selectionElement.getSimpleName().toString() + "Row";
	//        } else {
	//            if (messager != null && !StringHelpers.validJavaId(selection.rowName()))
	//                messager.error(selectionElement, "Row name \"%s\" is invalid", selection.rowName());
	//            return selection.rowName();
	//        }
	//    }
	//
	//    static String getJavaCursorName(@Nullable Output messager, Selection selection, TypeElement selectionElement) {
	//        if (selection.cursorName().isEmpty()) {
	//            Element enclosing = selectionElement.getEnclosingElement();
	//            return selectionElement.getSimpleName().toString() + "Cursor";
	//        } else {
	//            if (messager != null && !StringHelpers.validJavaId(selection.cursorName()))
	//                messager.error(selectionElement, "Cursor name \"%s\" is invalid", selection.cursorName());
	//            return selection.cursorName();
	//        }
	//    }
	//
	//    static String getSqlCursorName(@Nullable Output messager, Selection selection, TypeElement selectionElement) {
	//        if (selection.cursorName().isEmpty()) {
	//            Element enclosing = selectionElement.getEnclosingElement();
	//            return selectionElement.getSimpleName().toString() + "RawSqlCursor";
	//        } else {
	//            if (messager != null && !StringHelpers.validJavaId(selection.rawCursorName()))
	//                messager.error(selectionElement, "RawSqlCursor name \"%s\" is invalid", selection.cursorName());
	//            return selection.cursorName();
	//        }
	//    }
}
