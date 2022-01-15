package org.devops


// 执行模块pclint通用命令
def MoudlePclint() {
    bat "%workspace%/${MoudleName}/code/pclint/pc-lint_all.bat"
}


def Pclint() {
    MoudlePclint()
}




//执行lib模块的pclint
def Libdiagcount() {
    bat "%workspace%/common/${MoudleName}/pclint/pc-lint_all.bat"
}

def RmelPclint() {
    bat "%workspace%/common/rme/${MoudleName}/code/pclint/pc-lint_all.bat"
}

def MediaconPclint() {
    bat "%workspace%/common/rme/rmel/${MoudleName}/pclint/pc-lint_all.bat"
}

def GeneralLibPclint() {
    bat "%workspace%/common/${MoudleName}/code/pclint/pc-lint_all.bat"
}


def LibPclint() {
    switch(MoudleName) {
        case "libdiagcount":
            Libdiagcount()
            break;
        case "libdiagtime":
            Libdiagcount()
            break;
        case "rmel":
            RmelPclint()
            break;
        case "mediacon":
            MediaconPclint()
            break;
        default:
            GeneralLibPclint()
            break;
    }   
}

