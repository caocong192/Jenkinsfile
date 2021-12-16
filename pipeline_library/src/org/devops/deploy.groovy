package org.devops

// MDS6800 Í¨ÓÃ²¿Êð
def MDS6800GeneralDeploy(){

    sh """
        cd ${workspace}/CICD
        bash ./Gitlab_Deploy.sh
    """
}

def Deploy(){
    
    environment.DownloadTFSCI()
	MDS6800GeneralDeploy()
}