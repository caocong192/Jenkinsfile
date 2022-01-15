package org.devops

class GlobalData {
    static def PackageMap = [:]
}

// 入口函数, 执行发布包, 获取包的打包路径
// 此处通过传参 自定义包路径
def ArchivePackage(String PackagePath="${workspace}/${MoudleName}/code"){

    // 获取此次构建的包名
    def PackageName = sh(script: "cd  ${PackagePath}; ls | egrep -i 'MDS6800|MCS6800' ", returnStdout: true).trim()

    sh """
        cd ${workspace}/CICD
        bash ./Gitlab_Archive.sh ${PackagePath} ${PackageName}
    """

    // 包名与模块名构造成map, 追加到 PackageMap
    GlobalData.PackageMap[PackageName] = MoudleName
}



// lib 发布到SVN 脚本
def LibArchive() {
    sh """ 
        cp ${workspace}/interfaces/latest/${MoudleName} ~/ -rf
        cd ~/${MoudleName}
        find . -type d -name '.svn' | xargs rm -rf
        cd ${workspace}/interfaces/latest/
        svn rm --force ${workspace}/interfaces/latest/${MoudleName}
        svn ci -m 'remove old ${MoudleName} lib'
        cp ~/${MoudleName} . -rf
        svn add --force --no-ignore ./${MoudleName}
        svn ci -m 'add new ${MoudleName} lib'
        rm ~/${MoudleName} -rf
    """
}


// QT Archive 脚本
def QtArchive(){
    bat """
        cd %workspace%\\%MoudleName%\\%Version%Setup
        if exist "Setup\\*.exe" (
            for /f "delims=" %%a in ('dir /b Setup\\*.exe') do (
                set TAGNAME=%%~na
                echo *********************%TAGNAME%**************************
                echo *********************Archive**************************
                net use /delete \\\\192.168.2.77\\
                ping 0.0.0.0>nul
                                
                net use \\\\192.168.2.77 /user:%Artifatory_NAME% %Artifatory_PASSWD%

                md %ProjectArchivePath%\\%%~na"
                copy Setup\\*.exe %ProjectArchivePath%\\%%~na
                break
            )
        ) 
    """
}