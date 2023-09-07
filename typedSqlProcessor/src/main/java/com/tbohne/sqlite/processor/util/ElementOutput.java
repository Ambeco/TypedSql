package com.tbohne.sqlite.processor.util;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class ElementOutput {
	private final Messager messager;
	private final Element element;

	public ElementOutput(Messager messager, Element element) {
		this.messager = messager;
		this.element = element;
	}

	public AnnotationOutput forAnnotation(AnnotationMirror annotation) {
		return new AnnotationOutput(messager, element, annotation);
	}

	public void info(String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), element);
	}

	public void info(AnnotationMirror a, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), element, a);
	}

	public void info(AnnotationMirror a, AnnotationValue v, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), element, a, v);
	}

	public void warning(String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), element);
	}

	public void warning(AnnotationMirror a, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), element, a);
	}

	public void warning(AnnotationMirror a, AnnotationValue v, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), element, a, v);
	}

	public void error(String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
	}

	public void error(AnnotationMirror a, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element, a);
	}

	public void error(AnnotationMirror a, AnnotationValue v, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element, a, v);
	}
}
