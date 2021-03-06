def TFSGitServer() {
    return "http://tfs.jiaxun.com/JIAXUN/6800-%E7%B3%BB%E7%BB%9F%E8%BD%AF%E4%BB%B6/_git"
}

def GitlabServer() {
    return "http://gitlab.jiaxun.com"
}

def SvnServer() {
    return "http://192.168.2.100/svn"
}

def SvnVersion() {
    return "HEAD"
}

def GetProjectVersion() {
    return JOB_NAME.split("_")[0].toLowerCase()
}

def GetProductName() {
    return JOB_NAME.split("_")[1].toLowerCase()
}

def GetMoudleName() {
    return JOB_NAME.split("_")[-1].toLowerCase()
}

def MdsModules(){
    AllModulesStr = 'aasc6800,crss6800,csc6800,dbs6800,dns6800,dps6800,fss6800,gis6800,mrsc6800,mrsp6800,pms6800,pns6800,prs6800,rssc6800,rssp6800,sac6800,sag6800,sbc6800,sbs6800,scs6800,syd6800,uag6800,vmsc6800,asbc6800,stun6800'
    return  AllModulesStr
}

// 判断当前步骤是否执行
def StageExecute( String BuildMachineLabel = "," ){
    // BuildMachineLabel 为 "," 字符进行单环境判断 否则是多环境判断
    // 不传参数, 默认单环境判断
    if ( BuildMachineLabel == "," ) {
        if (CompileMachine.indexOf(BuildMachineLabel) == -1 ) {
            return true
        } else {
            return false
        }
    } 
    // 多环境判断
    else if (CompileMachine.indexOf(BuildMachineLabel) != -1 ) {
        env.MulEnvTagMachine = CompileMachine.split(",")[0]    
        return true
    } else {
        return false
    }
}

// 需 gbk 编码
def ArchiveBasePath(){
    if (BranchName =~ "testing") {
        return "//192.168.2.77/产品版本测试发布库"
    }
    return "//192.168.2.77/研发自测版本发布库"
}

def DownloadTFSCI(){
    def SrcCode = new org.devops.checkout()
    SrcCode.CheckoutFromGit("http://tfs.jiaxun.com/JIAXUN/CM/_git/MDS6800_CI", "master", "CICD")
    return this
}

def DownloadGtilabCICD(){
    def SrcCode = new org.devops.checkout()
    SrcCode.CheckoutFromGit("http://gitlab.jiaxun.com/cm/ci.git", "master", "CICD")
    return this
}

