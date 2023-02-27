package com.lguplus.sre.common

class JobBuildUser {

    static void checkPermission(script, String project, String buildUserId, String branchName) {
        if (branchName == 'master') {
            if (!buildUserId) {
                script.echo 'Build user is not set. This is webhook build.'
            }
            script.echo 'master branch build is not allowed.'
        }
    }

}
