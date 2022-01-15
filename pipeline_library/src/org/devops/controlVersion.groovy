package org.devops

// 模块构建前设置版本号
def SetVersion(){
    sh "cd ${workspace}/CICD;chmod a+x Gitlab_Set_Version.sh; ./Gitlab_Set_Version.sh"
}

def AddVersion(){
    sh "cd ${workspace}/CICD; chmod a+x ./Gitlab_Add_Version.sh; ./Gitlab_Add_Version.sh ${MoudleName}"
}


def SubmitVersion(){
    sh "cd ${workspace}/CICD; /usr/bin/git add Branch_Version.txt; /usr/bin/git commit -m 'Build Success! ${BranchName} Version add'; /usr/bin/git push origin HEAD:master"
}