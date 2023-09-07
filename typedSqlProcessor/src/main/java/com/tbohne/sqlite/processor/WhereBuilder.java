package com.tbohne.sqlite.processor;

import com.squareup.javapoet.TypeSpec;
import com.tbohne.sqlite.processor.models.CreateTableModel;
import com.tbohne.sqlite.processor.models.CreateTableNames;
import com.tbohne.sqlite.processor.util.Output;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class WhereBuilder {
	private static final String CONTENT_VALUES_NAME = "values";
	private static final String BITSET_NAME = "fieldsSet";
	private static final String[] RAW_GETTER_SUFFIXES = {"AsLong", "AsDouble", "AsString", "AsByteArray"};
	private final ProcessingEnvironment environment;
	private final Elements elements;
	private final Types types;
	private final Output messager;
	private final TypeMirror stringMirror;
	private final TypeMirror blobMirror;
	private final TypeMirror rawBinderMirror;

	WhereBuilder(ProcessingEnvironment environment, Output messager) {
		this.environment = environment;
		this.types = environment.getTypeUtils();
		this.elements = environment.getElementUtils();
		this.messager = messager;

		stringMirror = elements.getTypeElement("java.lang.String").asType();
		blobMirror = types.getArrayType(types.getPrimitiveType(TypeKind.BYTE));
		rawBinderMirror = elements.getTypeElement("com.tbohne.sqlite.binders.RawBinder").asType();
	}

	public void addWhereClass(
			TypeSpec.Builder parentClassBuilder,
			TypeElement tableElement,
			CreateTableModel createTable,
			CreateTableNames names)
	{

	}
}
