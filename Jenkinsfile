pipeline {
    agent any

    stages {
        stage('Configure git') {
            steps {
                script {
                    sh 'git config --global user.name "luminolmc"'
                    sh 'git config --global user.email "luminolmc@noreply.github.com"'
                }
            }
        }

        stage('Apply patches') {
            steps {
                script {
                    sh 'chmod +x gradlew'
                    sh './gradlew applyAllPatches'
                }
            }
        }

        stage('Build paperclip jar') {
            steps {
                script {
                    sh './gradlew createMojmapPaperclipJar'
                }
            }
        }

        stage('Upload artifacts') {
            steps {
               script {
                   archiveArtifacts(
                       artifacts: "luminol-server/build/libs/*.jar",
                       allowEmptyArchive: false
                   )
               }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}