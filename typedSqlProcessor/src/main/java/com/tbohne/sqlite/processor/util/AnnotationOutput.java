package com.tbohne.sqlite.processor.util;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class AnnotationOutput {
	private final Messager messager;
	private final Element element;
	private final AnnotationMirror annotation;

	public AnnotationOutput(Messager messager, Element element, AnnotationMirror annotation) {
		this.messager = messager;
		this.element = element;
		this.annotation = annotation;
	}

	public AnnotationValueOutput forAnnotationValue(AnnotationValue value) {
		return new AnnotationValueOutput(messager, element, annotation, value);
	}

	public void info(String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), element, annotation);
	}

	public void info(AnnotationValue v, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), element, annotation, v);
	}

	public void warning(String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), element, annotation);
	}

	public void warning(AnnotationValue v, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), element, annotation, v);
	}

	public void error(String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element, annotation);
	}

	public void error(AnnotationValue v, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element, annotation, v);
	}
}
