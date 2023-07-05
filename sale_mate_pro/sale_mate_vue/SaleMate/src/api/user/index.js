import http from '../../utils/request.js'

export function  verify(data){
    return  http({
        url:'/user/verify',
        data:data,
        method:'POST'
    })
}

export function install(authCode){
    return http({
        url:'/user/install',
        method:'POST',
        data:authCode
    });
}

export function systemLogin(data){
    return http({
        url:'/user/systemLogin',
        method:'POST',
        data:data
    });
}

export function env (){
    return http({
        url:'/user/env',
        method:'GET'
    })
}