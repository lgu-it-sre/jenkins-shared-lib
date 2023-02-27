def call(String project, String option='') {
    def buildUserId
    node {
        wrap([$class: 'BuildUser']) {
            buildUserId = env.BUILD_USER_ID
        }
    }
    def baseArgs = [
        project: project,
        buildUserId: buildUserId,
        option: option,
    ]
}
