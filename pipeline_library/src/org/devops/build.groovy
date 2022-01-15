package org.devops


// 模块构建通用命令
def MoudleBuild() {
    sh "cd ${workspace}/${MoudleName}/code; rm -rf MDS6800_* MCS6800_*; cd build;make clean && make && make dist"
}

def DeployToolsBuild(){
    sh "cd ${workspace}/${MoudleName}/; chmod a+x *.sh; bash package.sh"
}

def Build() {
    switch(MoudleName) {
        case "deploytools":
        case "developtools":
            DeployToolsBuild()
            break;
        default:
            MoudleBuild()
            break;
    }
}



// 执行lib某块的编译功能
def LibcliembBuild() {
    sh "cd ${workspace}/common/${MoudleName}/code/build/linux;make clean && make && make dist"
}

def LibdbmsBuild() {
    sh "cd ${workspace}/common/${MoudleName}/build;make clean && make && make dist"
}

def LibhttpBuild() {
    sh "cd ${workspace}/common/${MoudleName};chmod a+x ./*;./mk.sh;./commit.sh"
}

def LibldapBuild() {
    sh "cd ${workspace}/common/${MoudleName}/code/build;chmod a+x mk.sh;./mk.sh"
}

def LibzkclientBuild() {
    sh label: '', script: '''cd ${workspace}/common/${MoudleName}/code/app/zookeeper/zookeeper-3.4.13/src/c
    chmod a+x configure
    ./configure
    make
    rm -rf ../../../lib/*.so.*
    cp .libs/*.so.* ../../../lib


    cd ${workspace}/common/${MoudleName}
    BASE_PATH=$(pwd)

    cd code/app/muduo/base
    make
    make install

    cd ../net
    make
    make install

    cd ../../../build
    #cd build
    make clean
    make
    make dist'''
}

def LicenseBuild() {
    sh "cd ${workspace}/${MoudleName}/libmdslicense/code/build/;make clean && make && make commit"
}

def RmelBuild() {
    sh "cd ${workspace}/common/rme/${MoudleName}/code/build;make clean && make && make commit"
}

def Mediaconbuid() {
    if (NODE_LABELS.contains("RH7_X86")) {
        sh "cd ${workspace}/common/rme/rmel/${MoudleName}/build;make clean && make && make MEDIACON_HIK=1 && make commit MEDIACON_HIK=1"
    } else {
        sh "cd ${workspace}/common/rme/rmel/${MoudleName}/build;make clean && make && make commit"
    }
}

def GeneralLibBuild() {
    sh "cd ${workspace}/common/${MoudleName}/code/build;make clean && make && make commit"
}

def LibBuild() {
    def archive = new org.devops.archive()
    switch(MoudleName) {
        case "libcliemb":
        case "libmdsini":
        case "libmdslog":
        case "libmdsutils":
        case "libosal":
        case "libwdkeepalive":
        case "libdal":
            LibcliembBuild()
            archive.LibArchive()
            break;
        case "libdbms":
        case "libdiagcount":
        case "libdiagtime":
        case "libmds":
        //添加libdba相关库的编译工程 2021/7/17
        case "libdba_config":
        case "libdba_contact":
        case "libdba_dynamic":
        case "libdba_record":
            LibdbmsBuild()
            archive.LibArchive()
            break;
        case "libhttp":
            LibhttpBuild()
            break;
        case "libldap":
            LibldapBuild()
            break;
        case "libzkclient":
            LibzkclientBuild()
            archive.LibArchive()
            break;
        case "license":
            LicenseBuild()
            break;
        case "rmel":
            RmelBuild()
            break;
        case "mediacon":
            Mediaconbuid()
            break;
        default:
            GeneralLibBuild()
            break;
    }
}


// QT构建命令
def QtBuild(Version){
    bat """
        rd /q /s %workspace%\\%MoudleName%\\%Version%Setup\\Setup
        md  %workspace%\\%MoudleName%\\%Version%Setup\\Setup 
        cd %workspace%\\%MoudleName%\\%Version%\\build
        call huderson_build.bat
    """
}