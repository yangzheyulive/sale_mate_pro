import http from '../../utils/request.js'
export function getList (data){
return http({
        url:'/customer/getList',
        method:'POST',
        data:data
    })
}

export function getCustomer (id){
return http({
        url:'/customer/getCustomer?id='+id,
        method:'GET'
    })
}


export function syncCustomer (){
return http({
        url:'/customer/syncCustomer',
        method:'POST',
        timeout: 1000 * 60 * 60
    })
}

export function getSyncCustomerStatus (data){
return http({
        url:'/customer/getSyncCustomerStatus',
        method:'GET',
        data:data
    })
}