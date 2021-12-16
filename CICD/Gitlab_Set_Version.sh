#!/bin/bash

if [ -n "$1" ]; then
	MoudleName=$1
fi

if [ -n "$2" ]; then
	BuildPath=$2
else
	BuildPath="code/build"
fi

cd ${WORKSPACE}/CICD/
echo "***********${WORKSPACE}*********"
echo "***********${BranchName}*********"
echo "***********${MoudleName}*********"

# 5.5.G.100
# VER_MAJOR: 5
# VER_MINOR: 5.G
# VER_REV: 100
VER_MAJOR=`cat Branch_Version.txt | grep "${BranchName}.${MoudleName}" | awk '{printf($2)}' | awk 'BEGIN {FS="."} {printf($1)}'`
VER_MINOR=`cat Branch_Version.txt | grep "${BranchName}.${MoudleName}" | awk '{printf($2)}' | awk 'BEGIN {FS="."} {printf($2"."$3)}'`
VER_REV=`cat Branch_Version.txt | grep "${BranchName}.${MoudleName}" | awk '{printf($2)}' | awk 'BEGIN {FS="."} {printf($4)}'`
echo "${VER_MAJOR}"."${VER_MINOR}"."${VER_REV}"

if [ ! -n "${VER_REV}" ];then
	echo "获取不到版本信息, 请检查."
	exit 1
fi

cd ${WORKSPACE}/${MoudleName}/${BuildPath}
sed -i '/VER_MAJOR =/c\\VER_MAJOR = '''$VER_MAJOR'''' Makefile
sed -i '/VER_MINOR =/c\\VER_MINOR = '''$VER_MINOR'''' Makefile
sed -i '/VER_REV =/c\\VER_REV = '''$VER_REV'''' Makefile