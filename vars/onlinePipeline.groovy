import com.lguplus.sre.common.JobBuildUser

import com.lguplus.sre.online.TestAutomation

def call(Map baseArgs) {
    def SKIPTESTS_DEFAULT_VALUE = TestAutomation.getDefaultYN(baseArgs.project)
    def TEST_DESCRIPTION = TestAutomation.getSkipTestDescription(baseArgs.project)

    def PARAMETER_LIST = []

    // parameters 정의
    def skipTest = booleanParam(name: 'SKIPTESTS', defaultValue: SKIPTESTS_DEFAULT_VALUE, description: TEST_DESCRIPTION)
    def rollback = booleanParam(name: 'ROLLBACK', defaultValue: false, description: '체크 시 입력한 commit hash 값으로 checkout 합니다.')

    PARAMETER_LIST.add(rollback)
    if (env.BRANCH_NAME == 'develop' || [/feature.*/, /hotfix.*/].any { env.BRANCH_NAME =~ it }) {
        PARAMETER_LIST.add(skipTest)
}

    def newParams = parameters(PARAMETER_LIST)
    properties([
        newParams
    ])

    def pipelineArgs = [:]

    pipeline {
        agent any

        options {
            disableConcurrentBuilds()
            skipDefaultCheckout(true)
            buildDiscarder(logRotator(numToKeepStr: '10'))
        }

        post {
            always {
                cleanWs(cleanWhenNotBuilt: true, deleteDirs: true, disableDeferredWipeout: false, notFailBuild: true)
            }
        }

        stages {
            stage('Check build permission') {
                steps {
                    script {
                        JobBuildUser.checkPermission(this, baseArgs.project, baseArgs.buildUserId, env.BRANCH_NAME)
                    }
                }
            }
            stage('Checkout') {
                steps {
                    cleanWs()
                    checkout scm

                    script {
                        def git_hash = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                        if (params.ROLLBACK) {
                            timeout(time: 10, unit: 'MINUTES') {
                                git_hash = input(
                                    message: 'Rollback 할 commit hash 를 입력해주세요.',
                                    parameters: [[
                                        $class: 'TextParameterDefinition',
                                        defaultValue: git_hash,
                                        description: 'Enter commit hash',
                                        name: 'commit_hash'
                                    ]]
                                ).trim()
                                echo "Project Name: ${baseArgs.project}"
                                echo "Choice: ${git_hash}"

                                sh "git checkout ${git_hash}"
                            }
                        }
                    }
                }
            }
        }
    }
}
