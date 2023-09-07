package com.tbohne.sqlite.processor.util;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class Output {
	private final Messager messager;

	public Output(Messager messager) {
		this.messager = messager;
	}

	public ElementOutput forElement(Element e) {
		return new ElementOutput(messager, e);
	}

	public void info(Element e, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), e);
	}

	public void info(Element e, AnnotationMirror a, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), e, a);
	}

	public void info(Element e, AnnotationMirror a, AnnotationValue v, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), e, a, v);
	}

	public void warning(Element e, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), e);
	}

	public void warning(Element e, AnnotationMirror a, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), e, a);
	}

	public void warning(Element e, AnnotationMirror a, AnnotationValue v, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), e, a, v);
	}

	public void error(Element e, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
	}

	public void error(Element e, AnnotationMirror a, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e, a);
	}

	public void error(Element e, AnnotationMirror a, AnnotationValue v, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e, a, v);
	}
}
