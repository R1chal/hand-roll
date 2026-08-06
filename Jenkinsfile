pipeline {
    agent any

    tools {
        jdk 'jdk23'
        maven 'maven3'
    }

    stages {
        stage('拉取代码') {
            steps {
                checkout scm
                sh 'git log --oneline -1'
            }
        }
        stage('编译打包') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('发布到 Nexus') {
            steps {
                // 容器内 localhost 指自己,必须用容器名 nexus 覆盖默认地址
                sh 'mvn deploy -DskipTests -Dnexus.url=http://nexus:8081'
            }
        }
    }

    post {
        success { echo '🎉 自动触发构建成功!' }
        failure { echo '❌ 流水线失败' }
    }
}
