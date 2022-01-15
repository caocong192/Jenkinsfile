#!groovy

@Library("Gitlab_Pipeline_Library") _

//func from shareibrary
def tools = new org.devops.tools()
def download = new org.devops.download()
def pclint = new org.devops.pclint()
def projectEnv = new org.devops.projectEnv()
def build = new org.devops.build()
def controlVersion = new org.devops.controlVersion()
def archive = new org.devops.archive()
def tag = new org.devops.tag()
def toEmail = new org.devops.toEmail()
def deploy = new org.devops.deploy()

// env from shareibrary
env.ProjectVersion = environment.GetProjectVersion() // 项目版本名
env.ProductName = environment.GetProductName() // 产品名
env.MoudleName = environment.GetMoudleName() // 模块名

projectInfos.BackendSericeEnv() // 获取项目配置信息
env.RepoURL = environment.TFSGitServer() + "/" + MoudleName // TFS仓库地址
env.DeployFaildModulesStr = "--"


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
        booleanParam(name: "ExecutePclint", defaultValue: false, description: "请选择是否执行Pclint")

        extendedChoice description: '请勾选要构建的环境', 
        multiSelectDelimiter: ',', 
        name: 'CompileMachine', 
        quoteValue: false, 
        saveJSONParameterToFile: false, 
        type: 'PT_CHECKBOX',
        value: ProjectBuildEnv,
        visibleItemCount: 6

        extendedChoice description: '请勾选要构建的环境,为空跳过部署', 
        multiSelectDelimiter: ',', 
        name: 'CompileMachine', 
        quoteValue: false, 
        saveJSONParameterToFile: false, 
        type: 'PT_CHECKBOX',
        value: DeployIps,
        visibleItemCount: 6
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
                    tools.PrintMes("开始下载代码","green")
                    if ( CompileMachine != "All" ){
                        download.DownloadMoudleCode()
                        download.DownloadInterfaceCode("windows")
                    }else{
                        withEnv(["CompileMachine=Centos_X86"]) {
                            download.DownloadMoudleCode()
                            download.DownloadInterfaceCode("windows")   
                        }
                    }
                    
                    tools.PrintMes("开始执行pclint","green")
                    pclint.Pclint()
                }    
            }
        }

        stage("Build") {
            agent { 
                node {
                    label "${CompileMachine}"
                    customWorkspace "/home/Jenkins_Workspace/${ProductName}"
                }
            }
            when {
                beforeAgent true
                expression {
                    environment.StageExecute()
                }
            }
            steps {
                script {
                    tools.PrintMes("开始下载代码","green")
                    download.DownloadMoudleCode()
                    download.DownloadInterfaceCode()

                    tools.PrintMes("设置版本号", "green")
                    environment.DownloadTFSCI() 
                    controlVersion.SetVersion()

                    tools.PrintMes("开始构建","green")
                    build.Build()
                }
            }
        }

        stage("Archive") {
            agent {
                node {
                    label "${CompileMachine}"
                    customWorkspace "/home/Jenkins_Workspace/${ProductName}"
                }
            }
            when {
                beforeAgent true
                expression {
                   environment.StageExecute()
                }         
            }
            steps{
                script{
                    tools.PrintMes("开始发布包", "green")
                    archive.ArchivePackage()
                }
            }
        }

        stage("Tag and Add_Version") {
            agent { 
                node {
                    label "${CompileMachine}"
                    customWorkspace "/home/Jenkins_Workspace/${ProductName}"
                }
            }
            when {
                beforeAgent true
                expression {
                    environment.StageExecute()
                }        
            }
            steps{
                script{
                    tools.PrintMes("开始打标签", "green")
                    // tag.TagLinux()

                    tools.PrintMes("开始增加版本号", "green")
                    environment.DownloadTFSCI()
                    // controlVersion.AddVersion()
                    // controlVersion.SubmitVersion()
                }
            }
        }

        stage("Deploy") {
            agent { 
                node {
                    label "ansible"
                    customWorkspace "/home/Jenkins_Workspace/${ProductName}"
                }
            }
            when {
                beforeAgent true
                expression {
                    environment.StageExecute()
                }
                not {
                    environment name: "DeployIP", value: ""
                }         
                           
            }
            steps{
                script{
                    tools.PrintMes("开始部署阶段", "green")
                    environment.DownloadTFSCI()
                    deploy.Deploy()
                }
            }
        }

        stage('Parallel_Build') {
            failFast true
            parallel {
                stage('Centos_X86') {
                    agent { 
                        node {
                            label "Centos_X86"
                            customWorkspace "/home/Jenkins_Workspace/${ProductName}"
                        }
                    }
                    when {
                        beforeAgent true
                        expression {
                            environment.StageExecute("Centos_X86")
                        }        
                    }
                    steps {
                        script {
                            withEnv(["CompileMachine=Centos_X86"]) {

                                tools.PrintMes("开始下载代码","green")
                                download.DownloadMoudleCode()
                                download.DownloadInterfaceCode() 

                                tools.PrintMes("设置版本号", "green")
                                environment.DownloadTFSCI() 
                                controlVersion.SetVersion()

                                tools.PrintMes("开始构建","green")
                                build.Build()

                                tools.PrintMes("开始发布包", "green")
                                archive.ArchivePackage()
                            }
                        }
                    }
                }

                
                stage('kylin_ft') {
                    agent { 
                        node {
                            label "kylin_ft"
                            customWorkspace "/home/Jenkins_Workspace/${ProductName}"
                        }
                    }
                    when {
                        beforeAgent true
                        expression {
                            environment.StageExecute("kylin_ft")
                        }          
                    }
                    steps {
                        script {
                            withEnv(["CompileMachine=kylin_ft"]) {

                                tools.PrintMes("开始下载代码","green")
                                download.DownloadMoudleCode()
                                download.DownloadInterfaceCode() 

                                tools.PrintMes("设置版本号", "green")
                                environment.DownloadTFSCI() 
                                controlVersion.SetVersion()

                                tools.PrintMes("开始构建","green")
                                build.Build()

                                tools.PrintMes("开始发布包", "green")
                                archive.ArchivePackage()
                            }
                        }
                    }
                }

                stage('kylin_x86') {
                    agent { 
                        node {
                            label "kylin_x86"
                            customWorkspace "/home/Jenkins_Workspace/${ProductName}"
                        }
                    }
                    when {
                        beforeAgent true
                        expression {
                            environment.StageExecute("kylin_x86")
                        }          
                    }
                    steps {
                        script {
                            withEnv(["CompileMachine=kylin_x86"]) {

                                tools.PrintMes("开始下载代码","green")
                                download.DownloadMoudleCode()
                                download.DownloadInterfaceCode()

                                tools.PrintMes("设置版本号", "green")
                                environment.DownloadTFSCI() 
                                controlVersion.SetVersion()

                                tools.PrintMes("开始构建","green")
                                build.Build()

                                tools.PrintMes("开始发布包", "green")
                                archive.ArchivePackage()
                            }
                        }
                    }
                }

                // stage('neokylin_kunpeng') {
                //     agent { 
                //         node {
                //             label "neokylin_kunpeng"
                //             customWorkspace "/home/Jenkins_Workspace/${ProductName}"
                //         }
                //     }
                    // when {
                    //     beforeAgent true
                    //     expression {
                    //         environment.StageExecute("neokylin_kunpeng")
                    //     }        
                    // }
                //     steps {
                //         script {
                //             withEnv(["CompileMachine=neokylin_kunpeng"]) {

                //                 tools.PrintMes("开始下载代码","green")
                //                 download.DownloadMoudleCode()
                //                 download.DownloadInterfaceCode()

                //                 tools.PrintMes("设置版本号", "green")
                //                 environment.DownloadTFSCI() 
                //                 controlVersion.SetVersion()

                //                 tools.PrintMes("开始构建","green")
                //                 build.Build()

                //                 tools.PrintMes("开始发布包", "green")
                //                 archive.ArchivePackage()
                //             }
                //         }
                //     }
                // }

                stage('RH7_X86') {
                    agent { 
                        node {
                            label "RH7_X86"
                            customWorkspace "/home/Jenkins_Workspace/${ProductName}"
                        }
                    }
                    when {
                        beforeAgent true
                        expression {
                            environment.StageExecute("RH7_X86")
                        }           
                    }
                    steps {
                        script {
                            withEnv(["CompileMachine=RH7_X86"]) {

                                tools.PrintMes("开始下载代码","green")
                                download.DownloadMoudleCode()
                                download.DownloadInterfaceCode() 
    
                                tools.PrintMes("设置版本号", "green")
                                environment.DownloadTFSCI() 
                                controlVersion.SetVersion()

                                tools.PrintMes("开始构建","green")
                                build.Build()

                                tools.PrintMes("开始发布包", "green")
                                archive.ArchivePackage()
                            }
                        }
                    }
                }
            }
        }

        stage("Parallel_Build Tag and Add_Version") {
            agent { 
                node {
                    label "${MulEnvTagMachine}"
                    customWorkspace "/home/Jenkins_Workspace/${ProductName}"
                }
            }
            when {
                beforeAgent true
                expression {
                    environment.StageExecute(MulEnvTagMachine)
                }  
            }
            steps{
                script{
                    tools.PrintMes("开始打标签", "green")
                    // tag.TagLinux()

                    tools.PrintMes("开始增加版本号", "green")
                    environment.DownloadTFSCI()
                    // controlVersion.AddVersion()
                    // controlVersion.SubmitVersion()
                }
            }
        }

        stage("Deploy") {
            agent { 
                node {
                    label "ansible"
                    customWorkspace "/home/Jenkins_Workspace/${ProductName}"
                }
            }
            when {
                beforeAgent true
                expression {
                    environment.StageExecute(MulEnvTagMachine)
                }
                not {
                    environment name: "DeployIP", value: ""
                }         
                           
            }
            steps{
                script{
                    tools.PrintMes("开始部署阶段", "green")
                    environment.DownloadTFSCI()
                    deploy.Deploy()
                }
            }
        }
    }

    post {
        always{
            script {
                println("always")
            }
        }
        
        success{
            wrap([$class: 'BuildUser']) {
                script{
                    println("success")
                    toEmail.Email("成功!")
                }
            }
        }
        failure{
            wrap([$class: 'BuildUser']) {
                script{
                    println("failure")
                    toEmail.Email("失败!", "#FF0000")
                }
            }
        }
    }    
}
