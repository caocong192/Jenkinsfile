#!/bin/bash

# 支持动态传参 MoudleName
if [ -n "$1" ]; then
    MoudleName=$1
fi

# 构建成功，递增版本号
VER_MAJOR=`cat Branch_Version.txt | grep "${BranchName}.${MoudleName}" | awk '{printf($2)}' | awk 'BEGIN {FS="."} {printf($1)}'`
VER_MINOR=`cat Branch_Version.txt | grep "${BranchName}.${MoudleName}" | awk '{printf($2)}' | awk 'BEGIN {FS="."} {printf($2"."$3)}'`
VER_REV=`cat Branch_Version.txt | grep "${BranchName}.${MoudleName}" | awk '{printf($2)}' | awk 'BEGIN {FS="."} {printf($4)}'`
VERSION="${VER_MAJOR}"."${VER_MINOR}"."${VER_REV}"

let VER_REV++

NEXT_VERSION="${VER_MAJOR}"."${VER_MINOR}"."${VER_REV}"

BRANCH_MOUDLE=${BranchName/\//\\/}.${MoudleName}
echo "修改的版本分支模块是: ${BRANCH_MOUDLE}"
echo "当前的版本号是: ${VERSION}"
echo "下一个版本号是: ${NEXT_VERSION}"

sed -i '/'''${BRANCH_MOUDLE}'''/c\'''${BRANCH_MOUDLE}''' '''$NEXT_VERSION'''' Branch_Version.txt