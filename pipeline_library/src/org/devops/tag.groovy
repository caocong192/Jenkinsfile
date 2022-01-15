package org.devops


// 需设置 GBK 格式
// Linux机器上打标签
def TagLinux(String TmpBranchName = BranchName){
    def tools = new org.devops.tools()
    try{
        def BuildVersion = sh(script: "cd ${workspace}/CICD && cat ./Branch_Version.txt | grep -i ${TmpBranchName}.${MoudleName} | awk '{printf(\$2)}' ", returnStdout: true).trim()
        if ( BuildVersion == "" ){
            tools.PrintMes("版本号获取失败, 打tag失败!", "red")
        } else {
            // 判断 是否是 testing 分支
            String TestBranchPrefix = "testing"
            if ( BranchName.indexOf(TestBranchPrefix) == 0 ) {
                BuildVersionList = BuildVersion.split("\\.")
                BuildVersionList[-1] = BuildVersionList[-1].toInteger() - 1
                BuildVersion = BuildVersionList.join(".")
            } 

            def TagName = "${BranchName}/${BuildVersion}_#${BUILD_NUMBER}"
            tools.PrintMes("此次构建完成 打的TAG名是: ${TagName}", "green")
            sh "cd ${workspace}/${MoudleName} && git tag ${TagName} && git push origin ${TagName}"
        }
    }
    catch(Exception e){
        println "${e} \n${MoudleName} Set Tag failed. please contact CM to deal with it"
    }
}


def TagWindows() {
    def tools = new org.devops.tools()
    try {
        def TagName = "${BranchName}/${BranchVersion}_#${BUILD_NUMBER}"
        tools.PrintMes("此次构建完成 打的TAG名是: ${TagName}", "green")
        bat """
            cd %WORKSPACE%\\%MoudleName%
            git tag ${TagName}
            git push origin ${TagName}
        """
    }
    catch(Exception e){
        println "${e} \n${MoudleName} Set Tag failed. please contact CM to deal with it"
    }
}