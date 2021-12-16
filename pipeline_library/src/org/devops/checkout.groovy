package org.devops

def CheckoutFromSvn(repo,Branch,Localdir) {
	checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[cancelProcessOnExternalsFail: false, credentialsId: '405842bd-f409-47b3-bbfe-c7cdb835b1fd', depthOption: 'infinity', ignoreExternalsOption: false, local: "${Localdir}", remote: "${repo}@${Branch}"]], quietOperation: true, workspaceUpdater: [$class: 'UpdateWithCleanUpdater']])
}

def CheckoutFromGit(repo,Branch,Localdir) {
	checkout([$class: 'GitSCM', branches: [[name: "${Branch}"]], doGenerateSubmoduleConfigurations: false, extensions:[[$class: 'CleanBeforeCheckout'],[$class: 'RelativeTargetDirectory', relativeTargetDir: "${Localdir}"]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '4976a3d9-5998-4fb1-b925-46dae821363d', url: "${repo}"]]])
}

def CheckoutFromGitlabGit(repo,Branch,Localdir) {
	checkout([$class: 'GitSCM', branches: [[name: "${Branch}"]], doGenerateSubmoduleConfigurations: false, extensions:[[$class: 'CleanBeforeCheckout'],[$class: 'RelativeTargetDirectory', relativeTargetDir: "${Localdir}"]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '7bf36790-7d9d-4d2a-a4e1-1b143e7e5d68', url: "${repo}"]]])
}

