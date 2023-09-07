package com.tbohne.sqlite.processor.models;

import com.squareup.javapoet.ClassName;
import com.tbohne.sqlite.processor.util.Output;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

public class CreateTableNames {
	public final ClassName generatedClassType;
	public final ClassName sqlColumnNumberType;
	public final ClassName sqlColumnNamesType;
	public final ClassName javaColumnNumberType;
	public final ClassName projectionInterfaceType;
	public final ClassName generatedProjectionType;
	public final ClassName selectionInterfaceType;
	public final ClassName generatedSelectionType;
	public final ClassName binderSetType;
	public final String binderSetName;
	public final ClassName writeValuesType;
	public final ClassName writeValuesFactoryType;

	private CreateTableNames(
			ClassName generatedClassType,
			ClassName sqlColumnNumberType,
			ClassName sqlColumnNamesType,
			ClassName javaColumnNumberType,
			ClassName projectionInterfaceType,
			ClassName generatedProjectionType,
			ClassName selectionInterfaceType,
			ClassName generatedSelectionType,
			ClassName binderSetType,
			String binderSetName,
			ClassName writeValuesType,
			ClassName writeValuesFactoryType)
	{
		this.generatedClassType = generatedClassType;
		this.sqlColumnNumberType = sqlColumnNumberType;
		this.sqlColumnNamesType = sqlColumnNamesType;
		this.javaColumnNumberType = javaColumnNumberType;
		this.projectionInterfaceType = projectionInterfaceType;
		this.generatedProjectionType = generatedProjectionType;
		this.selectionInterfaceType = selectionInterfaceType;
		this.generatedSelectionType = generatedSelectionType;
		this.binderSetType = binderSetType;
		this.binderSetName = binderSetName;
		this.writeValuesType = writeValuesType;
		this.writeValuesFactoryType = writeValuesFactoryType;
	}

	public static CreateTableNames build(
			ProcessingEnvironment environment, @Nullable Output messager, TypeElement tableElement)
	{
		String packageName = environment.getElementUtils().getPackageOf(tableElement).getQualifiedName().toString();
		String inputName = tableElement.getSimpleName().toString();
		return new CreateTableNames(
				ClassName.get(packageName, inputName + "Sql"),
				ClassName.get(packageName, inputName + "Sql", "SqlColumnNumbers"),
				ClassName.get(packageName, inputName + "Sql", "SqlColumnNames"),
				ClassName.get(packageName, inputName + "Sql", "JavaColumnNumbers"),
				ClassName.get(packageName, inputName + "Sql", inputName + "Projection"),
				ClassName.get(packageName, inputName + "Projection"),
				ClassName.get(packageName, inputName + "Sql", inputName + "Selection"),
				ClassName.get(packageName, inputName + "Selection"),
				ClassName.get(packageName, inputName + "Sql", "Binders"),
				"binders",
				ClassName.get(packageName, inputName + "Sql", inputName + "WriteValues"),
				ClassName.get(packageName, inputName + "Sql", inputName + "WriteValuesFactory"));
	}
}
