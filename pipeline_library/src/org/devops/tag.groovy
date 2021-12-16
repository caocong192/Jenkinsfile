package org.devops


// ������ GBK ��ʽ
// Linux�����ϴ��ǩ
def TagLinux(){
    def tools = new org.devops.tools()
    try{
        def BuildVersion = sh(script: "cd ${workspace}/CICD && cat ./Branch_Version.txt | grep -i ${BranchName}.${MoudleName} | awk '{printf(\$2)}' ", returnStdout: true).trim()
        if ( BuildVersion == "" ){
            tools.PrintMes("�汾�Ż�ȡʧ��.", "red")
            continue
        }
        def TagName = "${BranchName}/${BuildVersion}_#${BUILD_NUMBER}"
        tools.PrintMes("�˴ι������ ���TAG����: ${TagName}", "green")
        sh "cd ${workspace}/${MoudleName} && git tag ${TagName} && git push origin ${TagName}"
    }
    catch(Exception e){
        println "${e} \n${MoudleName} Set Tag failed. please contact CM to deal with it"
    }
}