// 存放项目信息
// 需 GBK 编码
// V5.5.G


// MDS6800&MCS6800多媒体调度系统&无线宽带集群系统融合统一V5.5.5/V5.5.G
def Project_V5_5_G(){
	env.ProjectArchivePath="//192.168.2.77/Jenkins构建结果归档/MCS6800/" + BranchName.replace("/","_")

	switch(BranchName) {
        default:
     		env.InterfaceBranch="platform_railway_develop/Release1"
            break;
    }
	

	AllModulesStr = 'cag6800,aasc6800,crss6800,csc6800,dbs6800,dns6800,dps6800,fss6800,gis6800,mrsc6800,mrsp6800,pms6800,pns6800,prs6800,rssc6800,rssp6800,sac6800,sag6800,sbc6800,sbs6800,scs6800,syd6800,uag6800'
	return AllModulesStr
}


def BackendSericeEnv() {
    environment.DownloadTFSCI()
    env.ProjectBuildEnv = sh(script: "cd CICD; python Gitlab_GetProjectEnv.py 'ProjectBuildEnv' ${ProjectVersion}", returnStdout: true).trim()
    env.AllModulesStr = sh(script: "cd CICD; python Gitlab_GetProjectEnv.py 'AllModulesStr' ${ProjectVersion}", returnStdout: true).trim()
    env.DeployIps = sh(script: "cd CICD; python Gitlab_GetProjectEnv.py 'DeployIps' ${ProjectVersion}", returnStdout: true).trim()
}



// QT environment.
def QtArchivePath(Version, ProjectVersion){
    def ProjectName =  JOB_NAME.split("_")[2]
    switch(ProjectName) {
        case "CZ":
            return "\\\\192.168.2.77\\研发自测版本发布库\\MDS6800_C\\C6500_Trunk\\C6500_V5.5.H"
        default:
        	return "\\\\192.168.2.77\\研发自测版本发布库\\MDS6800_C\\%Version%\\%Version%_%ProjectVersion%"
            break;
        break
    }
}