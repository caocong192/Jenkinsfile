package org.devops

//�����ʼ�����
def Email(status, statusColor="#0B610B", emailUser="" ){
    emailext body: """
            <!DOCTYPE html> 
            <html> 
            <head> 
            <meta charset="UTF-8"> 
            </head> 
            <body leftmargin="8" marginwidth="0" topmargin="8" marginheight="4" offset="0"> 
                <table width="95%" cellpadding="0" cellspacing="0" style="font-size: 11pt; font-family: Tahoma, Arial, Helvetica, sans-serif">   
                    <tr> 
                        <td><br /> 
                            <b><font color="${statusColor}"> ������Ϣ</font></b> 
                        </td> 
                    </tr> 
                    <tr> 
                        <td> 
                            <ul> 
                                <li>��Ŀ���ƣ�${JOB_NAME}</li>         
                                <li>������ţ�${BUILD_ID}</li> 
                                <li>����״̬: <font color="${statusColor}">${status}</font></li>                         
                                <li>��Ŀ��ַ��<a href="${BUILD_URL}">${BUILD_URL}</a></li>    
                                <li>������־��<a href="${BUILD_URL}console">${BUILD_URL}console</a></li>
                            </ul> 
                        </td> 
                    </tr> 
                    <tr>  
                </table> 
            </body> 
            </html>  """,
            subject: "Jenkins-${JOB_NAME}��Ŀ������Ϣ ",
            to: "${env.BUILD_USER_EMAIL}, ${emailUser}",
            from: "Jenkins@jiaxun.com"
}
