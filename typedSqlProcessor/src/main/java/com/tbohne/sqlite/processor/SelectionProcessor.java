package com.tbohne.sqlite.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.tbohne.sqlite.processor.models.CreateTableModel;
import com.tbohne.sqlite.processor.util.Output;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class SelectionProcessor {
	private static final String CONTENT_VALUES_NAME = "values";
	private static final String BITSET_NAME = "fieldsSet";

	private final ProcessingEnvironment processingEnv;
	private final Elements elements;
	private final Types types;
	private final Output messager;
	private Filer filer;

	SelectionProcessor(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
		this.types = processingEnv.getTypeUtils();
		this.elements = processingEnv.getElementUtils();
		filer = processingEnv.getFiler();
		messager = new Output(processingEnv.getMessager());
	}

	public void process(TypeElement selectionElement) {
		String packageName = processingEnv.getElementUtils().getPackageOf(selectionElement).getQualifiedName().toString();

		AnnotationSpec
				generated =
				AnnotationSpec.builder(Generated.class)
											.addMember("value", "\"com.tbohne.sqlite.processor.typedSqlProcessor\"")
											.addMember("comments", "\"from " + selectionElement.getQualifiedName() + "\"")
											//.addMember("date",  "\"" + Instant.now().toString() + "\"")
											.build();
		TypeSpec.Builder
				classBuilder =
				TypeSpec.classBuilder(selectionElement.getSimpleName() + "Sql")
								.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
								.addAnnotation(generated)
								.addJavadoc(CodeBlock.builder().add("Generated from {@link $T}", selectionElement.asType()).build());
		classBuilder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

		HashMap<String, CreateTableModel> pathToTableCache = new HashMap<>();
		// processIntoClass(selectionElement, classBuilder, pathToTableCache);

		try {
			JavaFile.builder(packageName, classBuilder.build()).build().writeTo(filer);
		} catch (IOException e) {
			messager.error(selectionElement, e.toString());
		}
	}
	//
	//    public void processIntoClass(TypeElement selectionElement, TypeSpec.Builder classBuilder, HashMap<String, CreateTableModel> pathToTableCache) {
	//        Selection selectionAnnotation = selectionElement.getAnnotation(Selection.class);
	//        SelectionModel model = SelectionModel.build(processingEnv, messager, selectionElement, selectionAnnotation, pathToTableCache);
	//        addRowClass(classBuilder, selectionElement, model);
	//        addJavaCursorClass(classBuilder, selectionElement, model);
	//        addSqlCursorClass(classBuilder, selectionElement, model);
	//    }
	//
	//    void addRowClass(TypeSpec.Builder classBuilder, TypeElement selectionElement, CreateProjectionModel model) {
	//        TypeSpec.Builder cursorClassBuilder = TypeSpec.classBuilder(model.rowName)
	//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
	//                .addJavadoc(CodeBlock
	//                        .builder()
	//                        .add("A query row for selection {@link $T}", selectionElement.asType())
	//                        .build());
	//        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
	//                .addModifiers(Modifier.PUBLIC);
	//
	//        addRowColumns(cursorClassBuilder, constructorBuilder, model);
	//        cursorClassBuilder.addMethod(constructorBuilder.build());
	//        classBuilder.addType(cursorClassBuilder.build());
	//    }
	//
	//    void addRowColumns(TypeSpec.Builder classBuilder, MethodSpec.Builder constructor, SelectionModel model) {
	//        ImmutableList<JavaBinderModel<AbstractColumnModel>> binders = model.allJavaBinders.get();
	//        TypeMirror voidMirror = types.getNoType(TypeKind.VOID);
	//        ClassName stringClassName = ClassName.get(String.class);
	//        TypeName byteArrayName = ArrayTypeName.of(ClassName.BYTE);
	//        TypeName objectTypeName = TypeName.OBJECT;
	//        for (int i = 0; i < binders.size(); i++) {
	//            JavaBinderModel<AbstractColumnModel> binder = binders.get(i);
	//            if (types.isSameType(voidMirror, binder.fromSqlJavaMirror)) {
	//                // skip
	//            } else if (!binder.isRawBinder) {
	//                //normal java field
	//                TypeMirror javaTypeMirror = binder.fromSqlJavaMirror;
	//                TypeName javaTypeName = TypeName.get(javaTypeMirror);
	//                //TODO NULLABLE
	//                classBuilder.addField(FieldSpec.builder(javaTypeName, binder.columnJavaName, Modifier.FINAL, Modifier.PRIVATE).build());
	//                constructor.addParameter(javaTypeName, binder.columnJavaName);
	//                constructor.addStatement("this.$N = $N", binder.columnJavaName, binder.columnJavaName);
	//                classBuilder.addMethod(
	//                        MethodSpec
	//                            .methodBuilder("get" + StringHelpers.sqlToJavaSuffix(binder.columnJavaName))
	//                                .returns(TypeName.get(binder.fromSqlJavaMirror))
	//                            .addStatement("return $N", binder.columnJavaName)
	//                        .build());
	//            }else { //unknown java type :(
	//                //TODO NULLABLE
	//                classBuilder.addField(FieldSpec.builder(objectTypeName, binder.columnJavaName, Modifier.FINAL, Modifier.PRIVATE).build());
	//                constructor.addParameter(objectTypeName, binder.columnJavaName);
	//                constructor.addStatement("this.$N = $N", binder.columnJavaName, binder.columnJavaName);
	//                classBuilder.addMethod(
	//                        MethodSpec
	//                                .methodBuilder(StringHelpers.sqlToJava("get" + StringHelpers.sqlToJavaSuffix(binder.columnJavaName) + "AsLong"))
	//                                .returns(TypeName.LONG)
	//                                .addStatement("return (Long) $N", binder.columnJavaName)
	//                                .build());
	//                classBuilder.addMethod(
	//                        MethodSpec
	//                                .methodBuilder(StringHelpers.sqlToJava("get" + StringHelpers.sqlToJavaSuffix(binder.columnJavaName) + "AsDouble"))
	//                                .returns(TypeName.DOUBLE)
	//                                .addStatement("return (Double) $N", binder.columnJavaName)
	//                                .build());
	//                classBuilder.addMethod(
	//                        MethodSpec
	//                                .methodBuilder(StringHelpers.sqlToJava("get" + StringHelpers.sqlToJavaSuffix(binder.columnJavaName) + "AsString"))
	//                                .returns(stringClassName)
	//                                .addStatement("return (String) $N", binder.columnJavaName)
	//                                .build());
	//                classBuilder.addMethod(
	//                        MethodSpec
	//                                .methodBuilder(StringHelpers.sqlToJava("get" + StringHelpers.sqlToJavaSuffix(binder.columnJavaName) + "AsByte[]"))
	//                                .returns(byteArrayName)
	//                                .addStatement("return (Byte[]) $N", binder.columnJavaName)
	//                                .build());
	//                classBuilder.addMethod(
	//                        MethodSpec
	//                                .methodBuilder(StringHelpers.sqlToJava("get" + StringHelpers.sqlToJavaSuffix(binder.columnJavaName) + "AsObject"))
	//                                .returns(TypeName.OBJECT)
	//                                .addStatement("return $N", binder.columnJavaName)
	//                                .build());
	//            }
	//        }
	//    }
	//
	//    void addSqlCursorClass(TypeSpec.Builder classBuilder, TypeElement selectionElement, SelectionModel model) {
	//        TypeSpec.Builder cursorClassBuilder = TypeSpec.classBuilder(model.sqlCursorName)
	//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
	//                .addJavadoc(CodeBlock
	//                        .builder()
	//                        .add("A cursor exposing the raw sql columns from selection {@link $T}", selectionElement.asType())
	//                        .build());
	//        addCursorWrapperMethods(cursorClassBuilder);
	//        addSqlCursorColumns(cursorClassBuilder, model);
	//        classBuilder.addType(cursorClassBuilder.build());
	//    }
	//
	//    private void addCursorWrapperMethods(TypeSpec.Builder cursorClassBuilder) {
	//        ClassName autoClosableTypeName = ClassName.get("java.lang", "AutoCloseable");
	//        ClassName rawCursorTypeName = ClassName.get("android.database.sqlite", "SQLiteCursor");
	//        ClassName bundleTypeName = ClassName.get("android.os", "Bundle");
	//        ClassName uriTypeName = ClassName.get("android.net", "Uri");
	//        ClassName contentObserverTypeName = ClassName.get("android.database", "ContentObserver");
	//        ClassName contentResolverTypeName = ClassName.get("android.content", "ContentResolver");
	//        ParameterizedTypeName listOfUriTypeName = ParameterizedTypeName.get(ClassName.get(List.class), uriTypeName);
	//        cursorClassBuilder.addSuperinterface(autoClosableTypeName);
	//        cursorClassBuilder.addField(rawCursorTypeName, "rawCursor");
	//        cursorClassBuilder.addMethod(MethodSpec.constructorBuilder()
	//                .addModifiers(Modifier.PUBLIC)
	//                .addParameter(rawCursorTypeName, "rawCursor")
	//                .addStatement("this.rawCursor = rawCursor")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("close")
	//                .addAnnotation(Override.class)
	//                .addModifiers(Modifier.PUBLIC)
	//                .addStatement("rawCursor.close()")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("getCount")
	//                .addModifiers(Modifier.PUBLIC)
	//                        .returns(TypeName.LONG)
	//                .addStatement("return rawCursor.getCount()")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("getExtras")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(bundleTypeName)
	//                .addStatement("return rawCursor.getExtras()")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("getNotificationUri")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(uriTypeName)
	//                .addStatement("return rawCursor.getNotificationUri()")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("getNotificationUris")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(listOfUriTypeName)
	//                .addStatement("return rawCursor.getNotificationUris()")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("getPosition")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(TypeName.INT)
	//                .addStatement("return rawCursor.getPosition()")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("move")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(TypeName.BOOLEAN)
	//                .addParameter(TypeName.INT, "offset")
	//                .addStatement("return rawCursor.move(offset)")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("moveToFirst")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(TypeName.BOOLEAN)
	//                .addStatement("return rawCursor.moveToFirst()")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("moveToLast")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(TypeName.BOOLEAN)
	//                .addStatement("return rawCursor.moveToLast()")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("moveToNext")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(TypeName.BOOLEAN)
	//                .addStatement("return rawCursor.moveToNext()")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("moveToPosition")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(TypeName.BOOLEAN)
	//                .addParameter(TypeName.INT, "offset")
	//                .addStatement("return rawCursor.moveToPosition(offset)")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("moveToPrevious")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(TypeName.BOOLEAN)
	//                .addStatement("return rawCursor.moveToPrevious()")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("registerContentObserver")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(TypeName.VOID)
	//                .addParameter(contentObserverTypeName, "observer")
	//                .addStatement("rawCursor.registerContentObserver(observer)")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("respond")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(bundleTypeName)
	//                .addParameter(bundleTypeName, "extras")
	//                .addStatement("return rawCursor.respond(extras)")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("setExtras")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(TypeName.VOID)
	//                .addParameter(bundleTypeName, "extras")
	//                .addStatement("rawCursor.setExtras(extras)")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("setNotificationUri")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(TypeName.VOID)
	//                .addParameter(contentResolverTypeName, "resolver")
	//                .addParameter(uriTypeName, "uri")
	//                .addStatement("rawCursor.setNotificationUri(resolver, uri)")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("setNotificationUris")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(TypeName.VOID)
	//                .addParameter(contentResolverTypeName, "resolver")
	//                .addParameter(listOfUriTypeName, "uris")
	//                .addStatement("rawCursor.setNotificationUris(resolver, uris)")
	//                .build());
	//        cursorClassBuilder.addMethod(MethodSpec.methodBuilder("unregisterContentObserver")
	//                .addModifiers(Modifier.PUBLIC)
	//                .returns(TypeName.VOID)
	//                .addParameter(contentObserverTypeName, "observer")
	//                .addStatement("rawCursor.unregisterContentObserver(observer)")
	//                .build());
	//    }
	//
	//    void addSqlCursorColumns(TypeSpec.Builder classBuilder, SelectionModel model) {
	//        ImmutableList<AbstractColumnModel> columns = model.allColumns.get();
	//        AnnotationSpec nullableAnnotation = AnnotationSpec.builder(Nullable.class).build();
	//        TypeName longTypeName = ClassName.get(Long.class).annotated(nullableAnnotation);
	//        TypeName doubleTypeName = ClassName.get(Double.class).annotated(nullableAnnotation);
	//        TypeName stringTypeName = ClassName.get(String.class).annotated(nullableAnnotation);
	//        TypeName byteArrayTypeName = ArrayTypeName.of(ClassName.BYTE).annotated(nullableAnnotation);
	//        TypeName objectName = TypeName.OBJECT;
	//        for (int i = 0; i < columns.size(); i++) {
	//            AbstractColumnModel column = columns.get(i);
	//            String longMethodName = column.affinity == Affinity.INTEGER
	//                    ? ("get" + StringHelpers.sqlToJavaSuffix(column.sqlName))
	//                    : ("get" + StringHelpers.sqlToJavaSuffix(column.sqlName) + "UnsafeAsLong");
	//            classBuilder.addMethod(
	//                    MethodSpec
	//                            .methodBuilder(longMethodName)
	//                            .returns(longTypeName)
	//                            .addStatement("return rawCursor.getLong($L)", i)
	//                            .build());
	//            String doubleMethodName = column.affinity == Affinity.REAL
	//                    ? ("get" + StringHelpers.sqlToJavaSuffix(column.sqlName))
	//                    : ("get" + StringHelpers.sqlToJavaSuffix(column.sqlName) + "UnsafeAsDouble");
	//            classBuilder.addMethod(
	//                    MethodSpec
	//                            .methodBuilder(doubleMethodName)
	//                            .returns(doubleTypeName)
	//                            .addStatement("return rawCursor.getDouble($L)", i)
	//                            .build());
	//            String stringMethodName = column.affinity == Affinity.TEXT
	//                    ? ("get" + StringHelpers.sqlToJavaSuffix(column.sqlName))
	//                    : ("get" + StringHelpers.sqlToJavaSuffix(column.sqlName) + "UnsafeAsString");
	//            classBuilder.addMethod(
	//                    MethodSpec
	//                            .methodBuilder(stringMethodName)
	//                            .returns(stringTypeName)
	//                            .addStatement("return rawCursor.getString($L)", i)
	//                            .build());
	//            String blobMethodName = column.affinity == Affinity.BLOB
	//                    ? ("get" + StringHelpers.sqlToJavaSuffix(column.sqlName))
	//                    : ("get" + StringHelpers.sqlToJavaSuffix(column.sqlName) + "UnsafeAsBlob");
	//            classBuilder.addMethod(
	//                    MethodSpec
	//                            .methodBuilder(blobMethodName)
	//                            .returns(byteArrayTypeName)
	//                            .addStatement("return rawCursor.getBlob($L)", i)
	//                            .build());
	//        }
	//    }
	//
	//    void addJavaCursorClass(TypeSpec.Builder classBuilder, TypeElement selectionElement, SelectionModel model) {
	//        TypeSpec.Builder cursorClassBuilder = TypeSpec.classBuilder(model.javaCursorName)
	//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
	//                .addJavadoc(CodeBlock
	//                        .builder()
	//                        .add("A cursor exposing Java type-safe columns from selection {@link $T}", selectionElement.asType())
	//                        .build());
	//        cursorClassBuilder.addMethod(MethodSpec.constructorBuilder()
	//                .addModifiers(Modifier.PUBLIC)
	//                .build());
	//        addCursorWrapperMethods(cursorClassBuilder);
	//
	//        addBinderSet(cursorClassBuilder, model);
	//        //TODO Java cursor methods
	//        classBuilder.addType(cursorClassBuilder.build());
	//    }
	//
	//    void addBinderSet(TypeSpec.Builder classBuilder, SelectionModel model) {
	//        //TODO
	//    }
}
