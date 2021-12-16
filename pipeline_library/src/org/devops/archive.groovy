package org.devops


// 入口函数, 执行发布包, 获取包的打包路径
def ArchivePackage(String PackagePath="${workspace}/${MoudleName}/code" ){
    env.PackagePath = PackagePath
    // PackageName 为后续deploy阶段取包使用
    env.PackageName = sh(script: "cd  ${PackagePath}; ls | egrep -i 'MDS6800|MCS6800' ", returnStdout: true).trim()

    environment.DownloadTFSCI()

    GetProjectInfos()

    sh """
        cd ${workspace}/CICD
        bash ./Gitlab_Archive.sh
    """
}

// 通用方法, 获取 包的名称、对自测库的相对版本库发布路径和全路径
def GeneralGetProjectArchivePath(String TemProductName = ProductName){

    """
        发布包路径规则约定为: 
            ProjectArchivePath: BasePath + "MDS6800/MCS6800" + "MCS6800_V5.5.G/MDS6800_V5.5.3"
            ARCHIVE_FULL_PATH:  ProjectArchivePath / CompileMachine / MoudleName / PACKAGE_NAME
    """
    env.ProjectArchivePath = BasePath + "/" + TemProductName.toUpperCase() + "/" + TemProductName.toUpperCase() + "_" + ProjectVersion.toUpperCase()
}

// 根据不同的项目, 去获取对应的发布包版本库路径
def GetProjectInfos(){

    // 研发自测版本路径: "//192.168.2.77/研发自测版本发布库"
    env.BasePath = environment.ArchiveBasePath() 

    switch( ProjectVersion.toUpperCase() ) {
        case "V5.5.G":
            env.ProjectArchivePath = projectInfos.Project_V5_5_G()
            break;
        default:
            GeneralGetProjectArchivePath()
            break;
    }   
}


def ArchiveAllPackages(String PackagePath="${workspace}/${MoudleName}/code"){
    def PackageName = sh(script: "cd  ${PackagePath}; ls | egrep -i 'MDS6800|MCS6800' ", returnStdout: true).trim()

    sh """
        cd ${workspace}/CICD
        bash ./Gitlab_Archive.sh ${PackagePath} ${PackageName}
    """
}