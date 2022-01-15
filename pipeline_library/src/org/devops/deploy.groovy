package org.devops


// MDS6800 ͨ�ò���
def MDS6800GeneralDeploy(PackageName, MoudleName, IP){
    try {
        sh """
            cd ${workspace}/CICD
            bash ./Gitlab_Deploy.sh '${MoudleName}' '${PackageName}' '${IP}'
        """
    }catch (Exception err){
        env.DeployFaildModulesStr += "${MoudleName}_${IP} "
    }
}


// ����׶����
def Deploy(){
    def tools = new org.devops.tools()

    GlobalData.PackageMap.each {
        IPs = DeployIP.replaceAll(" ", ",").split(",")
        IPs.each { IPstr ->
            IP, OS = IPstr.split(" ")
            if ( it.key.indexOf(OS) != -1 ) {
                tools.PrintMes("��ʼ����# PackageName: ${it.key} -- MoudleName: ${it.value} -- IP: ${IP}", "green")
                MDS6800GeneralDeploy(it.key, it.value, IP)
            }
        }
    }
}