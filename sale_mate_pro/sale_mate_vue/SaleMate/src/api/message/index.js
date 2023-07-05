import http from '../../utils/request.js'
export function getMessageList(externalUserid){
   return  http({
        url:'/message/getList?externalUserid='+externalUserid,
        method:'GET'
    })
}
export function updateMessage(data){
   return  http({
        url:'/message/updateMessage',
        method:'POST',
        data:data
    })
}

