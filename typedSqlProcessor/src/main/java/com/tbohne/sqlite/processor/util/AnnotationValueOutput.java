package com.tbohne.sqlite.processor.util;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class AnnotationValueOutput {
	private final Messager messager;
	private final Element element;
	private final AnnotationMirror annotation;
	private final AnnotationValue value;

	public AnnotationValueOutput(Messager messager, Element element, AnnotationMirror annotation, AnnotationValue value) {
		this.messager = messager;
		this.element = element;
		this.annotation = annotation;
		this.value = value;
	}

	public void info(AnnotationValue v, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), element, annotation, value);
	}

	public void warning(String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), element, annotation, value);
	}

	public void error(String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element, annotation, value);
	}
}
