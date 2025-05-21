package com.technokratos.eateasy

import org.gradle.api.Plugin
import org.gradle.api.Project

class KeyGenerationTask implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.tasks.register('generateRsaKeys') {
            description = 'Generates RSA key pairs for access and refresh tokens'
            def resourcesDir = project.layout.buildDirectory.dir('resources/test/security')
            def keySize = 2048
            def keyDir = resourcesDir.get().asFile
            doLast {
                keyDir.mkdirs()

                logger.lifecycle("Генерация ключей для ACCESS токена...")
                generateKeyPair('access', project, keyDir, keySize)
                logger.lifecycle("Генерация ключей для REFRESH токена...")
                generateKeyPair('refresh', project, keyDir, keySize)

                logger.lifecycle("Все ключи успешно созданы в ${keyDir}")
            }
        }

        project.tasks.named('processTestResources') {
            dependsOn project.tasks.named('generateRsaKeys')
        }
    }

    private void generateKeyPair(String name, Project project, File keyDir, int keySize) {
        generatePublicKey(name, project, keyDir, keySize)
        generatePrivateKey(name, project, keyDir)
    }


    private void generatePublicKey(String name, Project project, File keyDir, int keySize) {
        project.exec {
            commandLine 'openssl', 'genpkey',
                    '-algorithm', 'RSA',
                    '-out', "${keyDir}/${name}_private.pem",
                    '-pkeyopt', "rsa_keygen_bits:${keySize}"
        }
    }

    private void generatePrivateKey(String name, Project project, File keyDir) {
        project.exec {
            commandLine 'openssl', 'rsa',
                    '-pubout',
                    '-in', "${keyDir}/${name}_private.pem",
                    '-out', "${keyDir}/${name}_public.pem"
        }
    }
}
