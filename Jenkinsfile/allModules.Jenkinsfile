#!groovy

@Library("Gitlab_Pipeline_Library") _

//func from shareibrary
def tools = new org.devops.tools()
def download = new org.devops.download()
def pclint = new org.devops.pclint()
def build = new org.devops.build()
def controlVersion = new org.devops.controlVersion()
def archive = new org.devops.archive()
def tag = new org.devops.tag()
def toEmail = new org.devops.toEmail()
def deploy = new org.devops.deploy()

// env from shareibrary
env.ProductName = environment.GetProductName() // 产品名
env.ProjectVersion = environment.GetProjectVersion() // 项目版本名
env.GitPath = environment.TFSGitServer()

//define store result string
SuccessModulesStr = "--"
PclintFaildModulesStr = "--"
BuildFailedModulesStr = "--"
ArchiveFaildModulesStr = "--"

pipeline {
    
    agent {
        node {
            label "master_166"
            customWorkspace "D:/Jenkins_Workspace"
        }
    }

    options {
        ansiColor('xterm')
    }

    parameters {
        choice(name: "BranchName", choices: ["platform_railway_develop/Release1"], description: "请选择所需要构建的分支")
        choice(name: "CompileMachine", choices: ["Centos_X86", "kylin_ft", "kylin_x86", "neokylin_kunpeng", "RH7_X86"], description: "请选择编译机类型")
        booleanParam(name: "ExecutePclint", defaultValue: false, description: "请选择是否执行Pclint")

        extendedChoice description: '请勾选要构建的模块, 勾选All 为构建所有模块', 
        multiSelectDelimiter: ',', 
        name: 'ChoiseModules', 
        quoteValue: false, 
        saveJSONParameterToFile: false, 
        type: 'PT_CHECKBOX', 
        value: "All,"+ environment.MdsModules(),
        visibleItemCount: 10
    }
    
    stages {
        stage("Pclint") {
            agent { 
                node {
                    label "pclint"
                    customWorkspace "D:/Jenkins_Workspace/${ProductName}"
                }
            }
            when {
                beforeAgent true
                environment name: "ExecutePclint", value: "true"           
            }
            steps {
                
                script {
                    // 先单独拉 interface
                    tools.PrintMes("开始下载Interface","green")
                    download.DownloadInterfaceCode("windows")

                    // 获取所有 mds服务的模块列表
                    if ( ChoiseModules.equalsIgnoreCase("All") ){
                        MdsModules = environment.MdsModules().split(",")
                    }else{
                        MdsModules = ChoiseModules.split(",")
                    }

                    def PclintMdsModules = [:]
                    for (x in MdsModules){
                        def Moudle = x
                        PclintMdsModules["${Moudle}"] = {
                            try {
                                withEnv(["MoudleName=${Moudle}", "RepoURL=${GitPath}/${Moudle}"]) {
                                    tools.PrintMes("开始下载代码","green")
                                    download.DownloadMoudleCode()

                                    println("开始执行pclint")
                                    pclint.Pclint()
                                }
                            }
                            catch (Exception err){
                                println("${err}")
                                tools.PrintMes("${Moudle} pclint failed. please check it .","red")
                                PclintFaildModulesStr += "${Moudle} "
                            }
                        }
                    }
                    // 开始并发Module pclint
                    parallel PclintMdsModules
                }    
            }
        }

        stage("Build and Archive") {
            agent { 
                node {
                    label "${CompileMachine}"
                    customWorkspace "/home/Jenkins_Workspace2/${ProductName}"
                }
            }
            when {
             beforeAgent true
                not {
                 environment name: "CompileMachine", value: "All"
                }        
            }
            steps {
                script {
                    // 先单独拉 interface
                    tools.PrintMes("开始下载Interface","green")
                    download.DownloadInterfaceCode()

                    // 获取所有 mds服务的模块列表
                    if ( ChoiseModules.equalsIgnoreCase('All') ){
                        MdsModules = environment.MdsModules().split(",")
                    }else{
                        MdsModules = ChoiseModules.split(',')
                    }
                    
                    tools.PrintMes("拉取最新的CI配置", "green")
                    environment.DownloadTFSCI()

                    BuildFailedModules = []
                    def BuildMdsModules = [:]
                    for (x in MdsModules){
                        def Moudle = x
                        BuildMdsModules["${Moudle}"] = {
                            try {
                                withEnv(["MoudleName=${Moudle}", "RepoURL=${GitPath}/${Moudle}"]) {
                                    tools.PrintMes("开始下载代码","green")
                                    download.DownloadMoudleCode()

                                    tools.PrintMes("设置版本号", "green")
                                    controlVersion.SetVersion()

                                    tools.PrintMes("开始构建","green")
                                    build.Build()
                                }
                            }
                            catch (Exception err){
                                println("${err}")
                                tools.PrintMes("${Moudle} build failed. please build it by itself.","red")
                                BuildFailedModules.add("${Moudle}")
                                BuildFailedModulesStr += "${Moudle} "
                            }                
                        } 
                    }      
                    // 开始并发构建
                    parallel BuildMdsModules

                    // 开始发布包
                    tools.PrintMes("拉取最新的CI配置", "green")
                    environment.DownloadTFSCI()

                    archive.GetProjectInfos()
                    for (x in MdsModules){
                        def Moudle = x
                        def ArchiveFlag = true
                        if ( ! BuildFailedModules.contains("${Moudle}") ){
                            try {
                                withEnv(["MoudleName=${Moudle}"]) {
                                    tools.PrintMes("开始发布包", "green")
                                    archive.ArchiveAllPackages()
                                }
                            }
                            catch (Exception err){
                                println "${err}"
                                tools.PrintMes("${Moudle} archive failed. please contact CM to deal with it","red")

                                ArchiveFaildModulesStr += "${Moudle} "
                                ArchiveFlag = false
                            }
                            finally {
                                if ( ArchiveFlag ){
                                    withEnv(["MoudleName=${Moudle}"]) {
                                        tools.PrintMes("开始打标签", "green")
                                        tag.TagLinux()

                                        tools.PrintMes("开始增加版本号", "green")
                                        controlVersion.AddVersion()
                                        SuccessModulesStr += "${Moudle} "
                                    }
                                }
                                else {
                                    tools.PrintMes("{Moudle} archive failed, skip Branch_Version step.", "red") 
                                }
                            }
                        }
                    }

                    tools.PrintMes("开始提交Branch_Version.txt", "green")
                    controlVersion.SubmitVersion()
                }
            }
        }   
    }

    post {
        always {
            wrap([$class: 'BuildUser']) {
                emailext (
                    subject: "(info) Build Completed: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                    body: """
                        <body leftmargin="8" marginwidth="0" topmargin="8" marginheight="4"offset="0">
                            <table width="95%" cellpadding="0" cellspacing="0"  style="font-size: 11pt; font-family: Tahoma, Arial, Helvetica, sans-serif">
                                <tr>
                                    <td>以下为${env.JOB_NAME }项目构建信息</td>
                                </tr>
                                <tr>
                                    <td><br />
                                        <b><font color="#0B610B">构建情况: 完成 </font></b>
                                        <hr size="2" width="100%" align="center" />
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <ul>
                                            <li>选择构建的模块有:  ${ChoiseModules}</li>
                                            <li>成功构建模块:    ${SuccessModulesStr}</li>
                                            <li>PcLint失败模块:  ${PclintFaildModulesStr}</li>
                                            <li>Compile失败模块: ${BuildFailedModulesStr}</li>
                                            <li>Archive失败模块: ${ArchiveFaildModulesStr}</li>
                                            <li>构建人：${BUILD_USER}</li>
                                            <li>构建日志：<a href="${BUILD_URL}console">${BUILD_URL}console</a></li>
                                        </ul>
                                    </td>
                                </tr>
                            </table>
                        </body> """,
                    to: "${env.BUILD_USER_EMAIL}",
                    from: "Jenkins@jiaxun.com"
                )
            }
        }
    }
}
        