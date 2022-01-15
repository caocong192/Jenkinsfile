package org.devops


// ������ GBK ��ʽ
// Linux�����ϴ��ǩ
def TagLinux(String TmpBranchName = BranchName){
    def tools = new org.devops.tools()
    try{
        def BuildVersion = sh(script: "cd ${workspace}/CICD && cat ./Branch_Version.txt | grep -i ${TmpBranchName}.${MoudleName} | awk '{printf(\$2)}' ", returnStdout: true).trim()
        if ( BuildVersion == "" ){
            tools.PrintMes("�汾�Ż�ȡʧ��, ��tagʧ��!", "red")
        } else {
            // �ж� �Ƿ��� testing ��֧
            String TestBranchPrefix = "testing"
            if ( BranchName.indexOf(TestBranchPrefix) == 0 ) {
                BuildVersionList = BuildVersion.split("\\.")
                BuildVersionList[-1] = BuildVersionList[-1].toInteger() - 1
                BuildVersion = BuildVersionList.join(".")
            } 

            def TagName = "${BranchName}/${BuildVersion}_#${BUILD_NUMBER}"
            tools.PrintMes("�˴ι������ ���TAG����: ${TagName}", "green")
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
        tools.PrintMes("�˴ι������ ���TAG����: ${TagName}", "green")
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