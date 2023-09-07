package com.tbohne.sqlite.processor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;

@RunWith(Parameterized.class)
public class AnnotationProcessorTests {
	private final String filename;

	public AnnotationProcessorTests(String filename) {
		this.filename = filename;
	}

	@Parameterized.Parameters
	public static ArrayList<String> primeNumbers() {
		//TODO find all java files in test/resources/...
		//return them as a list
		ArrayList<String> result = new ArrayList<>(1);
		result.add("src/test/resources/java/com/tbohne/sqlite/processor/CreateTableAllDefaults.java");
		return result;
	}

	@Before
	public void initialize() {
	}

	@Test
	public void runTest() {
		try {
			//            CompilationResult result = compile(configuration);
			//            return interpretResults(configuration, result);
		} catch (OutOfMemoryError e) {
			String message = String.format("Max memory = %d, total memory = %d, free memory = %d.",
																		 Runtime.getRuntime().maxMemory(),
																		 Runtime.getRuntime().totalMemory(),
																		 Runtime.getRuntime().freeMemory());
			System.out.println(message);
			System.err.println(message);
			throw new Error(message, e);
		}
	}
	//
	//    //mimic https://github.com/typetools/checker-framework/blob/master/checker/src/test/java/org/checkerframework/checker/test/junit/NullnessTest.java
	//    public CompilationResult compile(TestConfiguration configuration) {
	//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	//        final StringWriter javacOutput = new StringWriter();
	//        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
	//        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
	//            Iterable<? extends JavaFileObject> javaFiles =
	//                    fileManager.getJavaFileObjects(configuration.getTestSourceFiles().toArray(new File[]{}));
	//
	//            ArrayList<String> nonJvmOptions = new ArrayList<>();
	//            nonJvmOptions.add("-Xmaxerrs");
	//            nonJvmOptions.add("100000");
	//            nonJvmOptions.add("-Xmaxwarns");
	//            nonJvmOptions.add("100000");
	//            nonJvmOptions.add("-Xlint:deprecation");
	//            nonJvmOptions.add("-ApermitMissingJdk");
	//            nonJvmOptions.add("-Anocheckjdk");
	//            nonJvmOptions.add("-Anomsgtext");
	//
	//            final ArrayList<String> options = new ArrayList<>();
	//            options.add("-processor");
	//            options.add("-d");
	//            options.addAll(nonJvmOptions);
	//
	//            System.out.println("Running test using the following invocation:");
	//            System.out.println(
	//                    "javac "
	//                            + String.join(" ", options)
	//                            + " "
	//                            + StringsPlume.join(" ", configuration.getTestSourceFiles()));
	//            JavaCompiler.CompilationTask task =
	//                    compiler.getTask(
	//                            javacOutput, fileManager, diagnostics, options, new ArrayList<>(), javaFiles);
	//            final Boolean compiledWithoutError = task.call();
	//            javacOutput.flush();
	//            return new CompilationResult(
	//                    compiledWithoutError, javacOutput.toString(), javaFiles, diagnostics.getDiagnostics());
	//        } catch (IOException e) {
	//            throw new Error(e);
	//        }
	//    }
	//
	//    public TypecheckResult interpretResults(
	//            TestConfiguration config, CompilationResult compilationResult) {
	//        List<TestDiagnostic> expectedDiagnostics = readDiagnostics(config, compilationResult);
	//        return TypecheckResult.fromCompilationResults(config, compilationResult, expectedDiagnostics);
	//    }
	//
	//    //https://github.com/typetools/checker-framework/blob/0d3a885f2963342cac1a1ce8f36ef27bb82b0ac1/framework-test/src/main/java/org/checkerframework/framework/test/diagnostics/JavaDiagnosticReader.java#L41
	//    protected List<TestDiagnostic> readDiagnostics(
	//            TestConfiguration config, CompilationResult compilationResult) {
	//        List<TestDiagnostic> expectedDiagnostics;
	//        if (config.getDiagnosticFiles() == null || config.getDiagnosticFiles().isEmpty()) {
	//            expectedDiagnostics =
	//                    JavaDiagnosticReader.readJavaSourceFiles(compilationResult.getJavaFileObjects());
	//        } else {
	//            expectedDiagnostics = JavaDiagnosticReader.readDiagnosticFiles(config.getDiagnosticFiles());
	//        }
	//
	//        return expectedDiagnostics;
	//    }
}
