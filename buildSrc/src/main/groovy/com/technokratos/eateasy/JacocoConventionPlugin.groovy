package com.technokratos.eateasy

import org.gradle.api.Plugin
import org.gradle.api.Project

class JacocoConventionPlugin implements Plugin<Project> {

    private static final String[] EXCLUDE = [
            '**/dto/**',
            '**/exception/**',
            '**/*Exception.class'
    ]

    private static final String[] INCLUDE = [
            "**/*.class"
    ]

    private static final Float MINIMUM_COVERAGE = 0.8

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
        project.with {
            apply plugin: 'jacoco'
            jacoco {
                toolVersion = '0.8.13'
            }

            afterEvaluate {
                tasks.named('jacocoTestReport') {
                    reports {
                        xml.required = true
                        html.required = true
                    }

                    classDirectories.setFrom(files(classDirectories.files.collect {
                        fileTree(dir: it,
                                include: INCLUDE,
                                exclude: EXCLUDE
                        )
                    }))

                    dependsOn tasks.named('test')
                    mustRunAfter tasks.named('test')
                }

                tasks.named('jacocoTestCoverageVerification') {
                    violationRules {
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
                }

                tasks.named('check') {
                    dependsOn tasks.named('jacocoTestCoverageVerification')
                }

                tasks.named('test') {
                    finalizedBy tasks.named('jacocoTestReport')
                }
            }
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