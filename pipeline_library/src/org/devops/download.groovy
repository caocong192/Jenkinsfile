package org.devops


def DownloadMoudleCode(){

    // 开始从GitServer下载代码
    def SrcCode = new org.devops.checkout() 
    SrcCode.CheckoutFromGit("${RepoURL}", BranchName, MoudleName)
}


def DownloadInterfaceCode(String os = "linux"){
    // 开始从SVN 下载Interface库
    def SrcCode = new org.devops.checkout()
    def SvnServer = environment.SvnServer()
    def SvnVersion = environment.SvnVersion()
    def SvnRepoURL = "${SvnServer}/mds6800/lib/${BranchName}/${CompileMachine}/interfaces"
    SrcCode.CheckoutFromSvn("${SvnRepoURL}", "${SvnVersion}", "lib/${BranchName}/${CompileMachine}/interfaces")

    // 区分windows和linux系统
    if ( os == "windows"){
        SrcCode.CheckoutFromSvn("${SvnServer}/mds6800/trunk/tools", "${SvnVersion}", "tools")
        bat """
            cd %WORKSPACE%
            rmdir interfaces
            echo %CompileMachine%
            mklink /j interfaces lib\\%BranchName:/=\\%\\%CompileMachine%\\interfaces
        """
    }
    
    if ( os == "linux" ){
        sh "rm interfaces -rf; ln -snf lib/${BranchName}/${CompileMachine}/interfaces interfaces"
    }
}