package com.rxjavaimpl.annotations;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
MyProcessor.jar
	- com
		- example
			- MyProcessor.class

	- META-INF
		- services
			- javax.annotation.processing.Processor
 */
/* content javax.annotation.processing.Processor:
com.example.MyProcessor
com.foo.OtherProcessor
net.blabla.SpecialProcessor
 */
public class CreateJarForAnnotation {
    public static void main(String[] args) {
        try {
            new CreateJarForAnnotationLogic().createJar();
            System.exit(0);
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

class CreateJarForAnnotationLogic {
    void createJar() {
        final String projectDir = System.getProperty("user.dir").split("/src")[0];
        final String annotationFileName = "javax.annotation.processing.Processor";
        final String annotationFileSourceRootDir = "META-INF/services/";
        final String libraryLocation = projectDir + "/lib";
        try {
            List<Path> processorPaths = getProcessorPaths(projectDir);
            List<String> processorSourceRootPaths = getSourceRootPaths(processorPaths);
            createAnnotationMetaFile(processorSourceRootPaths, libraryLocation + "/" + annotationFileSourceRootDir, annotationFileName);
            produceJarFile(libraryLocation, processorSourceRootPaths, processorPaths, annotationFileSourceRootDir + annotationFileName);
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAnnotationMetaFile(List<String> processorSourceRootPaths, String outputPath, String outputFileName) throws IOException {
        String fullOutputPath = outputPath + outputFileName;

        List<String> fullQualifiedProcessorsNames = processorSourceRootPaths.stream()
                .map(p -> p.replace("/", ".")
                        .replace(".class", ""))
                .collect(Collectors.toList());
        Files.deleteIfExists(Paths.get(fullOutputPath));
        Files.createDirectories(Paths.get(outputPath));
        Files.createFile(Paths.get(fullOutputPath));
        Files.write(Paths.get(fullOutputPath), fullQualifiedProcessorsNames);
    }

    private List<String> getSourceRootPaths(List<Path> processorPaths) {
        return processorPaths.stream()
                .map(this::getSubPath)
                .collect(Collectors.toList());
    }

    private String getSubPath(Path p) {
        final int sizeOfPath = p.getNameCount();
        int startFromCom = 5;
        return p.subpath(sizeOfPath - startFromCom,
                sizeOfPath).toString();
    }

    private List<Path> getProcessorPaths(String projectDir) {
        final String processorsDir = projectDir + "/target/classes/com/rxjavaimpl/annotations/processors";
        try {
            return Files.walk(Paths.get(processorsDir), FileVisitOption.FOLLOW_LINKS)
                    .filter(p -> p.getFileName().toString().contains(".class"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void produceJarFile(String libraryLocation, List<String> processorSourceRootPaths, List<Path> processorPaths, String annotationFileSourceRootDir) throws IOException {
        String jarName = "AnnotationProcessors.jar";
        Files.deleteIfExists(Paths.get(libraryLocation + "/" + jarName));
        Files.createDirectories(Paths.get(libraryLocation));
        JarOutputStream jar = new JarOutputStream(Files.newOutputStream(Paths.get(libraryLocation + "/" + jarName)));
        for (int i = 0; i < processorPaths.size(); i++) {
            provideContentToJar(jar, processorSourceRootPaths.get(i), processorPaths.get(i));
        }
        provideContentToJar(jar, annotationFileSourceRootDir, Paths.get(libraryLocation + "/" + annotationFileSourceRootDir));
        jar.close();
    }

    private void provideContentToJar(JarOutputStream jar, String pathInJar, Path pathOfOriginalFile) throws IOException {//TODO
        JarEntry file = new JarEntry(pathInJar);
        jar.putNextEntry(file);
        jar.write(Files.readAllBytes(pathOfOriginalFile));
    }
}
