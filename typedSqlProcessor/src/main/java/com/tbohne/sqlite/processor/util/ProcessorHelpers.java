package com.tbohne.sqlite.processor.util;

import com.google.common.collect.ImmutableList;
import com.tbohne.sqlite.processor.models.AbstractColumnModel;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class ProcessorHelpers {

	private ProcessorHelpers() {
	}

	public static @Nullable ExecutableElement getMethodByName(TypeElement binderElement, String simpleName) {
		for (int i = 0; i < binderElement.getEnclosedElements().size(); i++) {
			Element rawElement = binderElement.getEnclosedElements().get(i);
			if (rawElement instanceof ExecutableElement) {
				ExecutableElement element = (ExecutableElement) rawElement;
				if (element.getSimpleName().toString().equals(simpleName)) {
					return element;
				}
			}
		}
		return null;
	}

	public static DeclaredType getTypeMirror(Supplier<Class<?>> supplier) {
		try {
			supplier.get();
			throw new IllegalStateException("supplier did not return a class");
		} catch (MirroredTypeException mte) {
			return (DeclaredType) mte.getTypeMirror();
		}
	}

	public static ImmutableList<DeclaredType> getTypeMirrors(Supplier<Class<?>[]> supplier) {
		try {
			supplier.get();
			throw new IllegalStateException("supplier did not return a class");
		} catch (MirroredTypesException mte) {
			ImmutableList.Builder<DeclaredType> list = ImmutableList.builder();
			for (int i = 0; i < mte.getTypeMirrors().size(); i++) {
				list.add((DeclaredType) mte.getTypeMirrors().get(i));
			}
			return list.build();
		}
	}

	//    public static @Nullable AnnotationMirror getAnnotationParameterMirror(ProcessingEnvironment environment, AnnotationMirror typeMirror, String parameter) {
	//        Types types = environment.getTypeUtils();
	//        TypeMirror wantMirror = environment.getElementUtils().getTypeElement(annotation.getCanonicalName()).asType();
	//        List<? extends AnnotationMirror> annotations = typeMirror.getAnnotationMirrors();
	//        for (int i = 0; i < annotations.size(); i++) {
	//            if (types.isSameType(annotations.get(i).getAnnotationType(), wantMirror))
	//                return annotations.get(i);
	//        }
	//        return null;
	//    }

	public static @Nullable AnnotationMirror getAnnotationMirror(
			ProcessingEnvironment environment, TypeMirror typeMirror, Class<?> annotation)
	{
		Types types = environment.getTypeUtils();
		TypeMirror wantMirror = environment.getElementUtils().getTypeElement(annotation.getCanonicalName()).asType();
		List<? extends AnnotationMirror> annotations = typeMirror.getAnnotationMirrors();
		for (int i = 0; i < annotations.size(); i++) {
			if (types.isSameType(annotations.get(i).getAnnotationType(), wantMirror)) {
				return annotations.get(i);
			}
		}
		return null;
	}

	public static void appendAnnotatedMirror(
			StringBuilder sb, TypeMirror mirror)
	{
		List<? extends AnnotationMirror> annotations = mirror.getAnnotationMirrors();
		for (int i = 0; i < annotations.size(); i++) {
			AnnotationMirror annotation = annotations.get(i);
			sb.append('@').append(annotation).append(' ');
		}
		//TODO: Confirm that this emits type-arguments correctly
		sb.append(mirror);
	}

	public static void appendAnnotatedElement(StringBuilder sb, Element element) {
		List<? extends AnnotationMirror> annotations = element.getAnnotationMirrors();
		for (int i = 0; i < annotations.size(); i++) {
			AnnotationMirror annotation = annotations.get(i);
			sb.append('@').append(annotation).append(' ');
		}
		sb.append(element.asType());
	}

	public static boolean hasNullableAnnotation(TypeMirror input) {
		List<? extends AnnotationMirror> mirrors = input.getAnnotationMirrors();
		for (int i = 0; i < mirrors.size(); i++) {
			AnnotationMirror mirror = mirrors.get(i);
			if (mirror.getAnnotationType().asElement().getSimpleName().toString().equals("Nullable")) {
				return true;
			}
		}
		return false;
	}

	public static @Nullable MethodElementTypePair findMethod(
			Types typeUtils,
			TypeElement classDefinitionWithoutGenerics,
			DeclaredType classInstanceWithGenerics,
			String methodName,
			@Nullable Predicate<TypeMirror> desiredReturnType,
			List<@Nullable Predicate<TypeMirror>> desiredParameters,
			List<ExecutableElement> outputWrongSignatureList) //THIS IS AN OUTPUT PARAMETER
	{
		List<? extends Element> members = classDefinitionWithoutGenerics.getEnclosedElements();
		ExecutableElement result = null;
		for (int i = 0; i < members.size(); i++) {
			Element rawMember = members.get(i);
			//skip non-methods
			if (rawMember.getKind() != ElementKind.METHOD) {
				continue;
			}
			ExecutableElement member = (ExecutableElement) rawMember;
			// skip wrong names
			if (!member.getSimpleName().toString().equals(methodName)) {
				continue;
			}
			outputWrongSignatureList.add(member);
			// failed match for wrong number of parameters
			if (desiredParameters.size() != member.getParameters().size()) {
				continue;
			}
			// failed match for wrong return type
			ExecutableType genericizedMember = (ExecutableType) typeUtils.asMemberOf(classInstanceWithGenerics, member);
			if (desiredReturnType != null && !desiredReturnType.test(genericizedMember.getReturnType())) {
				continue;
			}
			// failed match for wrong parameter types
			boolean matchedAll = true;
			for (int j = 0; j < desiredParameters.size(); j++) {
				@Nullable Predicate<TypeMirror> parameterPredicate = desiredParameters.get(j);
				if (parameterPredicate != null && parameterPredicate.test(genericizedMember.getParameterTypes().get(j))) {
					matchedAll = false;
					break;
				}
			}
			// otherwise, return correct method
			if (matchedAll) {
				return new MethodElementTypePair(member, genericizedMember);
			}
		}
		return null; // correct method not found. Partial matches listed in outputWrongSignatureList
	}

	public static <ColumnType extends AbstractColumnModel> String methodNotFoundErrorText(
			String binderName,
			DeclaredType binderInstanceWithGenerics,
			String methodName,
			String returnType,
			List<String> columns,
			List<ExecutableElement> foundToSqlMethodsList)
	{
		StringBuilder
				sb =
				new StringBuilder("binder \"").append(binderName)
																			.append("\" `")
																			.append(binderInstanceWithGenerics)
																			.append("` does not seem to have a #")
																			.append(methodName)
																			.append(" method with the required signature. Wanted: `")
																			.append(returnType)
																			.append(" #")
																			.append(methodName)
																			.append("(");
		for (int i = 0; i < columns.size(); i++) {
			sb.append(i == 0 ? "" : ", ").append(columns.get(i));
		}
		if (foundToSqlMethodsList.isEmpty()) {
			sb.append(")`, but didn't find any #toSql methods");
		} else {
			sb.append(")`, but instead found: \n");
			for (int i = 0; i < foundToSqlMethodsList.size(); i++) {
				sb.append(foundToSqlMethodsList.get(i)).append('\n');
			}
		}
		return sb.toString();
	}

	public static class MethodElementTypePair {
		public final ExecutableElement methodBodyNoGenerics;
		public final ExecutableType methodCallWithGenerics;

		public MethodElementTypePair(
				ExecutableElement methodBodyNoGenerics, ExecutableType methodCallWithGenerics)
		{
			this.methodBodyNoGenerics = methodBodyNoGenerics;
			this.methodCallWithGenerics = methodCallWithGenerics;
		}
	}
}
