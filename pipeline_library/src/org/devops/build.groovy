package org.devops

// 模块构建通用命令
def MoudleBuild() {
    sh "cd ${workspace}/${MoudleName}/code;rm -rf MDS6800_*;cd build;make clean && make && make dist"
}


def Build() {
    MoudleBuild()
}