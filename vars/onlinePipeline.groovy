import com.lguplus.sre.online.TestAutomation

def call(Map baseArgs) {
    def SKIPTESTS_DEFAULT_VALUE = TestAutomation.getDefaultYN(baseArgs.project)
    def TEST_DESCRIPTION = TestAutomation.getSkipTestDescription(baseArgs.project)

    def PARAMETER_LIST = []

    // parameters 정의
    def skipTest = booleanParam(name: 'SKIPTESTS', defaultValue: SKIPTESTS_DEFAULT_VALUE, description: TEST_DESCRIPTION)

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
    }
}
