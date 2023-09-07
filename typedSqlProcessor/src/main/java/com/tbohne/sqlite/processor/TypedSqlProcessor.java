package com.tbohne.sqlite.processor;

import com.tbohne.sqlite.annotations.CreateIndex;
import com.tbohne.sqlite.annotations.CreateProjection;
import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.Query;
import com.tbohne.sqlite.annotations.Selection;
import com.tbohne.sqlite.processor.util.Output;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@SupportedAnnotationTypes({
		"com.tbohne.annotations.sqlite.CreateTable",
		"com.tbohne.annotations.sqlite.CreateProjection",
		"com.tbohne.annotations.sqlite.CreateIndex",
		"com.tbohne.annotations.sqlite.Query",
		"com.tbohne.annotations.sqlite.Selection",})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TypedSqlProcessor
		extends AbstractProcessor
{
	private ProcessingEnvironment processingEnv;
	private Elements elements;
	private Filer filer;
	private Output messager;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.processingEnv = processingEnv;
		elements = processingEnv.getElementUtils();
		filer = processingEnv.getFiler();
		messager = new Output(processingEnv.getMessager());
	}

	@Override
	public HashSet<String> getSupportedAnnotationTypes() {
		HashSet<String> supportedAnnotationTypes = new HashSet<>();
		supportedAnnotationTypes.add(CreateTable.class.getName());
		supportedAnnotationTypes.add(CreateProjection.class.getName());
		supportedAnnotationTypes.add(CreateIndex.class.getName());
		supportedAnnotationTypes.add(Query.class.getName());
		supportedAnnotationTypes.add(Selection.class.getName());
		return supportedAnnotationTypes;
	}

	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (annotations.isEmpty()) {
			return true;
		}
		{
			Set<? extends Element>
					annotated =
					roundEnv.getElementsAnnotatedWith(CreateTable.class)
									.stream()
									.filter(e -> e instanceof TypeElement)
									.collect(Collectors.toSet());
			messager.info(annotations.iterator().next(), "Starting TableProcessor round on %d types", annotations.size());
			TableProcessor tableProcessor = new TableProcessor(processingEnv);
			for (Element annotatedElement : annotated) {
				tableProcessor.process((TypeElement) annotatedElement);
			}
		}
		{
			Set<? extends Element>
					annotated =
					roundEnv.getElementsAnnotatedWith(CreateProjection.class)
									.stream()
									.filter(e -> e instanceof TypeElement)
									.collect(Collectors.toSet());
			messager.info(
					annotations.iterator().next(),
					"Starting ProjectionProcessor round on %d types",
					annotations.size());
			ProjectionProcessor projectionProcessor = new ProjectionProcessor(processingEnv);
			for (Element annotatedElement : annotated) {
				projectionProcessor.process((TypeElement) annotatedElement);
			}
		}

		return true;
	}
}
