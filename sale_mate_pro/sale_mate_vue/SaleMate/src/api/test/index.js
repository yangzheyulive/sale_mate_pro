import http from '../../utils/request.js'

export function test(){
    return  http({
        url:'/test',
        method:'GET'
    })
}