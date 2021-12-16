package org.devops


// 模块pclint通用命令
def MoudlePclint() {
    bat "%workspace%/${MoudleName}/code/pclint/pc-lint_all.bat"
}


def Pclint(){
    MoudlePclint()
}