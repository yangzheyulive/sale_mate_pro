import http from '../../utils/request.js'
export function getMaterialByGroup (labelType){
return http({
        url:'/material/getMaterialByGroup?labelType='+labelType,
        method:'GET'
    })

}
export function getMaterialByGroupImage (groupId){
return http({
        url:'/material/getMaterialByGroupImage?groupId='+groupId,
        method:'GET'
    })

}
export function getMaterialByGroupText (groupId){
return http({
        url:'/material/getMaterialByGroupText?groupId='+groupId,
        method:'GET'
})

}

/**
 * 随机图片接口
 */
export function randomImage(){
    return http({
        url:'/material/randomImage',
        method:'GET'
    })
}


export function aiImage (groupId){
return http({
        url:'/material/aiImage',
        method:'GET'
    })
}

export function getList (params){
return http({
        url:'/material/getList',
        method:'POST',
        data:params
    })

}