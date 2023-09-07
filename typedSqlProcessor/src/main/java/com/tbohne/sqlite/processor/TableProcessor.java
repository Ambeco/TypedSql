package com.tbohne.sqlite.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tbohne.sqlite.annotations.CreateProjection;
import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.Selection;
import com.tbohne.sqlite.annotations.query.ProjectionTable;
import com.tbohne.sqlite.annotations.query.SelectColumns;
import com.tbohne.sqlite.annotations.query.SelectTable;
import com.tbohne.sqlite.processor.models.CreateTableModel;
import com.tbohne.sqlite.processor.models.CreateTableNames;
import com.tbohne.sqlite.processor.models.JavaBinderModel;
import com.tbohne.sqlite.processor.models.TableColumnModel;
import com.tbohne.sqlite.processor.util.Output;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class TableProcessor {
	private ProcessingEnvironment processingEnv;
	private Elements elements;
	private Filer filer;
	private Output messager;

	public TableProcessor(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
		elements = processingEnv.getElementUtils();
		filer = processingEnv.getFiler();
		messager = new Output(processingEnv.getMessager());
	}

	public boolean process(TypeElement tableElement) {
		CreateTable createTable = tableElement.getAnnotation(CreateTable.class);

		HashMap<String, CreateTableModel> pathToTableCache = new HashMap<>();
		CreateTableModel
				model =
				CreateTableModel.build(processingEnv, messager, tableElement, createTable, pathToTableCache);
		CreateTableNames names = CreateTableNames.build(processingEnv, messager, tableElement);

		AnnotationSpec
				generated =
				AnnotationSpec.builder(ClassName.get("javax.annotation", "Generated"))
											.addMember("value", "\"com.tbohne.sqlite.processor.typedSqlProcessor\"")
											.addMember("comments", "\"from " + tableElement.getQualifiedName() + "\"")
											//.addMember("date",  "\"" + Instant.now().toString() + "\"")
											.build();
		TypeSpec.Builder
				classBuilder =
				TypeSpec.classBuilder(names.generatedClassType)
								.addModifiers(Modifier.PUBLIC)
								.addAnnotation(generated)
								.addJavadoc(CodeBlock.builder().add("{@link $T}", tableElement.asType()).build());
		classBuilder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

		addColumnData(classBuilder, model, names);
		addBinderSet(classBuilder, model, names);
		addProjectionAndSelection(classBuilder, model, names, tableElement);
		new TableCreateMethodBuilder(processingEnv, messager).addCreateTableMethod(classBuilder, model);

		new TableWriteValuesBuilder(processingEnv, messager).addWriteValuesClass(classBuilder, tableElement, model, names);

		new WhereBuilder(processingEnv, messager).addWhereClass(classBuilder, tableElement, model, names);

		try {
			JavaFile.builder(names.generatedClassType.packageName(), classBuilder.build()).build().writeTo(filer);
		} catch (IOException e) {
			messager.error(tableElement, e.toString());
		}
		return true;
	}

	void addColumnData(TypeSpec.Builder classBuilder, CreateTableModel model, CreateTableNames names) {
		ClassName stringTypeName = ClassName.get(String.class);
		TypeSpec.Builder sqlNumbers = TypeSpec.interfaceBuilder(names.sqlColumnNumberType).addModifiers(Modifier.PUBLIC);
		TypeSpec.Builder sqlNames = TypeSpec.interfaceBuilder(names.sqlColumnNamesType).addModifiers(Modifier.PUBLIC);
		for (int i = 0; i < model.columnList.size(); i++) {
			TableColumnModel column = model.columnList.get(i);
			sqlNumbers.addField(FieldSpec.builder(TypeName.INT, column.javaName)
																	 .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
																	 .initializer("$L", i)
																	 .build());
			sqlNames.addField(FieldSpec.builder(stringTypeName, column.javaName)
																 .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
																 .initializer("$S", column.sqlName)
																 .build());
		}
		classBuilder.addType(sqlNumbers.build());
		classBuilder.addType(sqlNames.build());

		TypeSpec.Builder javaNumbers = TypeSpec.interfaceBuilder(names.javaColumnNumberType).addModifiers(Modifier.PUBLIC);
		for (int i = 0; i < model.javaColumns.size(); i++) {
			JavaBinderModel column = model.javaColumns.get(i);
			javaNumbers.addField(FieldSpec.builder(TypeName.INT, column.columnJavaName)
																		.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
																		.initializer("$L", i)
																		.build());
		}
		classBuilder.addType(javaNumbers.build());
	}

	void addBinderSet(TypeSpec.Builder classBuilder, CreateTableModel model, CreateTableNames names) {
		TypeSpec.Builder
				type =
				TypeSpec.classBuilder(names.binderSetType).addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
		MethodSpec.Builder
				constructor =
				MethodSpec.constructorBuilder()
									.addModifiers(Modifier.PUBLIC)
									.addAnnotation(ClassName.get("javax.inject", "Inject"));
		for (int i = 0; i < model.binders.size(); i++) {
			JavaBinderModel binder = model.binders.get(i);
			if (binder.bothStatic) {
				continue;
			}
			type.addField(TypeName.get(binder.binderMirror), binder.binderName);
			constructor.addParameter(TypeName.get(binder.binderMirror), binder.binderName);
			constructor.addStatement("this.$N = $N", binder.binderName, binder.binderName);
		}
		classBuilder.addType(type.addMethod(constructor.build()).build());
	}

	void addProjectionAndSelection(
			TypeSpec.Builder classBuilder, CreateTableModel model, CreateTableNames names, TypeElement tableElement)
	{
		AnnotationSpec
				projection =
				AnnotationSpec.builder(CreateProjection.class)
											.addMember("tables", "@$T($T.class)", ProjectionTable.class, tableElement)
											.build();
		TypeSpec
				projectionType =
				TypeSpec.classBuilder(names.projectionInterfaceType)
								.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
								.addAnnotation(projection)
								.addJavadoc("{@link $T}", tableElement.asType())
								.build();
		classBuilder.addType(projectionType);

		AnnotationSpec
				selectionColumns =
				AnnotationSpec.builder(SelectColumns.class)
											.addMember("projection", "@$T($T.class)", Selection.class, tableElement)
											.build();
		AnnotationSpec
				selectionTable =
				AnnotationSpec.builder(SelectTable.class).addMember("table", "$T", tableElement.asType()).build();
		AnnotationSpec selection = AnnotationSpec.builder(Selection.class)
																						 .addMember("select",
																												"@$T(projection = $T.class)",
																												SelectColumns.class,
																												names.projectionInterfaceType)
																						 .addMember("fromTable", "@$T($T.class)", SelectTable.class, tableElement)
																						 .build();
		TypeSpec
				selectionType =
				TypeSpec.classBuilder(names.selectionInterfaceType)
								.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
								.addAnnotation(selection)
								.addJavadoc("{@link $T}", tableElement.asType())
								.build();
		classBuilder.addType(selectionType);
	}

}
