package com.technokratos.eateasy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class JacocoConventionPlugin implements Plugin<Project> {

    private static final String JACOCO_VERSION = '0.8.13'

    private static final String[] EXCLUDE = [
            '**/dto/**',
            '**/exception/**',
            '**/*Exception.class'
    ]

    private static final String[] INCLUDE = [
            "**/*.class"
    ]

    private static final Float MINIMUM_COVERAGE = 0.5

    @Override
    void apply(Project rootProject) {
        rootProject.subprojects { Project subproject ->
            if (hasJavaSources(subproject)) {
                configureForJavaProject(subproject)
            } else {
                configureForNonJavaProject(subproject)
            }
        }

        registerAggregatedCheckTask(rootProject)
    }

    private void configureForJavaProject(Project project) {
        project.pluginManager.apply('jacoco')

        project.jacoco {
            toolVersion = JACOCO_VERSION
        }

        project.afterEvaluate {
            configureJacocoTestReport(project)
            configureJacocoTestCoverageVerification(project)
        }
    }

    private void configureJacocoTestReport(Project project) {
        Task jacocoTestReport = project.tasks.named('jacocoTestReport').get()
        jacocoTestReport.reports {
            xml.required = true
            html.required = true
        }
        jacocoTestReport.classDirectories.setFrom(createFilteredClassDirectories(jacocoTestReport, project))
        jacocoTestReport.dependsOn(project.tasks.named('test'))
        jacocoTestReport.mustRunAfter(project.tasks.named('test'))
    }

    private Object createFilteredClassDirectories(Task jacocoTestReport, Project project) {
        def include = project.hasProperty("jacocoInclude") ? project.property("jacocoInclude").split(",") : INCLUDE
        def exclude = project.hasProperty("jacocoExclude") ? project.property("jacocoExclude").split(",") : EXCLUDE

        jacocoTestReport.classDirectories.files.collect {
            project.fileTree(dir: it, include: include, exclude: exclude)
        }
    }

    private void configureJacocoTestCoverageVerification(Project project) {
        Task jacocoTestCoverageVerification = project.tasks.named('jacocoTestCoverageVerification').get()
        jacocoTestCoverageVerification.classDirectories.setFrom(createFilteredClassDirectories(jacocoTestCoverageVerification, project))

        jacocoTestCoverageVerification.violationRules {
            rule {
                limit {
                    counter = 'LINE'
                    minimum = MINIMUM_COVERAGE
                }
            }
            rule {
                limit {
                    counter = 'BRANCH'
                    minimum = MINIMUM_COVERAGE
                }
            }
        }

        project.tasks.named('check').configure {
            dependsOn jacocoTestCoverageVerification
        }
    }

    private void configureForNonJavaProject(Project project) {
        registerAggregatedCheckTask(project)
    }

    private void registerAggregatedCheckTask(Project project) {
        project.tasks.register('check') {
            dependsOn project.subprojects*.tasks*.named('check')
            description = "Aggregates check tasks of all subprojects"
            group = "verification"
        }
    }

    private static boolean hasJavaSources(Project project) {
        return project.file("${project.projectDir}/src/main/java").exists()
    }
}