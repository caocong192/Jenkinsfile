#!/bin/bash
#Date: 2021年11月12日
#Author: caocong
#Mail: caocong@jiaxun.com
#Function: MCS6800/MDS6800 archive script.
#Version: V1.0
#Update
#1.0 初始版本


function mount_dir(){
    mount -t cifs -o username=version -o password=123qwe..,vers=3.0 $1 ${MOUNT_PATH}
}


function archive_packages(){
	cd ${PackagePath}
	if [ ! -f ./${PACKAGE_NAME} ]; then
		echo -e "\033[40;31m **********在${PackagePath}下 没有找到 ${PACKAGE_NAME} ********************* \033[0m"
    	exit 2
	fi

	# 开始挂载
	for((i=1;i<=3;i++));
	do
	    echo "**开始第 $i 次挂载尝试 **"
	    if [ ! -d ${MOUNT_PATH} ]; then
	    	mkdir -p ${MOUNT_PATH}
	    fi

	    umount -l ${MOUNT_PATH} &
	    mount_dir "${ProjectArchivePath}"

		if [ $? -eq 0 ]; then
		    echo -e "\033[47;34m **********${MOUNT_PATH} 挂载成功********************* \033[0m"
		    break
		fi
		sleep 30s

		if [ $i -eq 3 ];then
			echo -e "\033[40;31m **********${MOUNT_PATH} 挂载失败********************* \033[0m"
			exit 3
		fi
	done

	echo "*****************开始发布 ${PACKAGE_NAME}***************"
	if [ ! -d ${MOUNT_PATH}/${CompileMachine}/${MoudleName}/${PACKAGE_NAME} ]; then
		mkdir -p ${MOUNT_PATH}/${CompileMachine}/${MoudleName}/${PACKAGE_NAME}
	fi

	cp -rp ${PACKAGE_NAME} ${MOUNT_PATH}/${CompileMachine}/${MoudleName}/${PACKAGE_NAME}

	if [ $? -eq 0 ]; then
		echo "**********${PACKAGE_NAME} 发布包完成********************"
		echo -en "\033[47;34m 发布包路径: \033[0m"
		echo "${ARCHIVE_FULL_PATH//\//\\}"
	else
		echo "**********${PACKAGE_NAME} 发布包失败********************"
		exit 4
	fi

	# 结束挂载
	umount -l ${MOUNT_PATH}
}


function get_package_name(){
	PACKAGE_NAME=`cd  ${PackagePath}; ls | egrep -i "MDS6800|MCS6800"`
	if [ ! -n "${PACKAGE_NAME}" ]; then
		echo "没有获取到包名."
		exit 1
	fi
}

function main(){
	# 支持动态传参 PackagePath, PackageName
	if [ -n "$1" ]; then
	    PackagePath=$1
	    PackageName=$2
	fi

	get_package_name
	
	# 发布包路径规则约定为 ProjectArchivePath / CompileMachine / MoudleName / PACKAGE_NAME

	echo "打印参数信息:"
	#
	#	MoudleName: 构建的模块名
	#	ProductName: 构建的产品名 MDS6800 / MCS6800
	#	ProjectArchivePath: 按规则生成的项目发布包路径
	#	ARCHIVE_FULL_PATH: 发布包的全路径
	#	MOUNT_PATH: 挂载路径
	#	PackagePath: 构建包生成的目录
	#	PACKAGE_NAME: 构建包的名称
	
	# 模块名转大写
	MoudleName=$(echo $MoudleName | tr "a-z" "A-Z")
	echo "MoudleName: ${MoudleName}"
	echo "ProductName: ${ProductName}"
	echo "ProjectArchivePath: ${ProjectArchivePath}"
	ARCHIVE_FULL_PATH="${ProjectArchivePath}/${CompileMachine}/${MoudleName}/${PACKAGE_NAME}"
	echo "ARCHIVE_FULL_PATH: ${ARCHIVE_FULL_PATH}"

	MOUNT_PATH="/mnt/share/${ProductName}/${MoudleName}"
	echo "MOUNT_PATH: ${MOUNT_PATH}"
	echo "PackagePath: ${PackagePath}"
	echo "PACKAGE_NAME: ${PACKAGE_NAME}"

	archive_packages
}

main "$@"
