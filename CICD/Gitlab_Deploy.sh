#!/bin/bash
#Date: 2021年11月23日
#Author: caocong
#Mail: caocong@jiaxun.com
#Function: MCS6800/MDS6800 deploy script.
#Version: V1.0
#Update
#1.0 初始版本


function mount_dir(){
    mount -t cifs -o username=version -o password=xxx,vers=3.0 $1 ./${MOUNT_PATH}
}

function deploy_package(){
	# 创建 packages 目录
	cd ${WORKSPACE}/CICD; mkdir ${MOUNT_PATH}

	# 开始挂载
	mount_dir ${DEPLOY_PACKAGE_PATH}

	ls ./packages/*

	ansible-playbook Gitlab_Genernal_Deploy.yml -e "env_host_ip=${DeployIP} env_package_name=${PackageName}"

	if [ $? -eq 0 ]; then
		echo -e "\033[47;34m **********${PackageName} 部署成功********************* \033[0m"
	else
		echo -e "\033[40;31m **********${PackageName} 部署失败********************* \033[0m"
	fi

	# 结束挂载
	umount -l ${MOUNT_PATH}
}

function main(){
	MOUNT_PATH="packages"
	echo "PackageName: ${PackageName}"

	# 模块名转大写
	MoudleName=$(echo $MoudleName | tr "a-z" "A-Z")
	DEPLOY_PACKAGE_PATH="${ProjectArchivePath}/${CompileMachine}/${MoudleName}/${PackageName}"
	echo "DEPLOY_PACKAGE_PATH: ${DEPLOY_PACKAGE_PATH}"
	echo "DeployIP: ${DeployIP}"

	deploy_package
}

main "$@"