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
env.ProductName = environment.GetProductName() // ��Ʒ��
env.ProjectVersion = environment.GetProjectVersion() // ��Ŀ�汾��
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
        choice(name: "BranchName", choices: ["platform_railway_develop/Release1"], description: "��ѡ������Ҫ�����ķ�֧")
        choice(name: "CompileMachine", choices: ["Centos_X86", "kylin_ft", "kylin_x86", "neokylin_kunpeng", "RH7_X86"], description: "��ѡ����������")
        booleanParam(name: "ExecutePclint", defaultValue: false, description: "��ѡ���Ƿ�ִ��Pclint")

        extendedChoice description: '�빴ѡҪ������ģ��, ��ѡAll Ϊ��������ģ��', 
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
                    // �ȵ����� interface
                    tools.PrintMes("��ʼ����Interface","green")
                    download.DownloadInterfaceCode("windows")

                    // ��ȡ���� mds�����ģ���б�
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
                                    tools.PrintMes("��ʼ���ش���","green")
                                    download.DownloadMoudleCode()

                                    println("��ʼִ��pclint")
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
                    // ��ʼ����Module pclint
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
                    // �ȵ����� interface
                    tools.PrintMes("��ʼ����Interface","green")
                    download.DownloadInterfaceCode()

                    // ��ȡ���� mds�����ģ���б�
                    if ( ChoiseModules.equalsIgnoreCase('All') ){
                        MdsModules = environment.MdsModules().split(",")
                    }else{
                        MdsModules = ChoiseModules.split(',')
                    }
                    
                    tools.PrintMes("��ȡ���µ�CI����", "green")
                    environment.DownloadTFSCI()

                    BuildFailedModules = []
                    def BuildMdsModules = [:]
                    for (x in MdsModules){
                        def Moudle = x
                        BuildMdsModules["${Moudle}"] = {
                            try {
                                withEnv(["MoudleName=${Moudle}", "RepoURL=${GitPath}/${Moudle}"]) {
                                    tools.PrintMes("��ʼ���ش���","green")
                                    download.DownloadMoudleCode()

                                    tools.PrintMes("���ð汾��", "green")
                                    controlVersion.SetVersion()

                                    tools.PrintMes("��ʼ����","green")
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
                    // ��ʼ��������
                    parallel BuildMdsModules

                    // ��ʼ������
                    tools.PrintMes("��ȡ���µ�CI����", "green")
                    environment.DownloadTFSCI()

                    archive.GetProjectInfos()
                    for (x in MdsModules){
                        def Moudle = x
                        def ArchiveFlag = true
                        if ( ! BuildFailedModules.contains("${Moudle}") ){
                            try {
                                withEnv(["MoudleName=${Moudle}"]) {
                                    tools.PrintMes("��ʼ������", "green")
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
                                        tools.PrintMes("��ʼ���ǩ", "green")
                                        tag.TagLinux()

                                        tools.PrintMes("��ʼ���Ӱ汾��", "green")
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

                    tools.PrintMes("��ʼ�ύBranch_Version.txt", "green")
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
                                    <td>����Ϊ${env.JOB_NAME }��Ŀ������Ϣ</td>
                                </tr>
                                <tr>
                                    <td><br />
                                        <b><font color="#0B610B">�������: ��� </font></b>
                                        <hr size="2" width="100%" align="center" />
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <ul>
                                            <li>ѡ�񹹽���ģ����:  ${ChoiseModules}</li>
                                            <li>�ɹ�����ģ��:    ${SuccessModulesStr}</li>
                                            <li>PcLintʧ��ģ��:  ${PclintFaildModulesStr}</li>
                                            <li>Compileʧ��ģ��: ${BuildFailedModulesStr}</li>
                                            <li>Archiveʧ��ģ��: ${ArchiveFaildModulesStr}</li>
                                            <li>�����ˣ�${BUILD_USER}</li>
                                            <li>������־��<a href="${BUILD_URL}console">${BUILD_URL}console</a></li>
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
        