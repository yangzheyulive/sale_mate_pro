import http from '../../utils/request.js'
export function getTagList(){
    return http({
        url:'/tag/list',
        method:'GET'
    })
}