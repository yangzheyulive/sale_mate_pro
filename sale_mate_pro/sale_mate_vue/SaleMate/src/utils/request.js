import axios from 'axios'
import { Notification, MessageBox, Message, Loading } from 'element-ui'
import router from '../router'
import Vue from "vue";
import AppConfig from "../config/index"

// axios.defaults.baseURL = 'http://localhost:9090/api'
axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
axios.defaults.timeout = 5000000;
const service = axios.create(
    {
        // baseURL: 'http://localhost:9090/api',
        baseURL: AppConfig.baseURL,
        timeout: 5000000,
    }
);
// request interceptor
service.interceptors.request.use(
    config => {
        //请求前配置token
        // console.log("token:",localStorage.token);
        config.headers['token'] = Vue.cookie.get('token')
        return config;
    }, error => {
        console.log(error)
        return Promise.reject(error)
    }
)
// response interceptor
service.interceptors.response.use(
    response => {
        console.log(response);
        if (response.data === 'fail') {
            Message.error('请求出错');
            return ;
        }
        return response;
    },
    error => {
        console.log(error)
        if (error.response.status === 401) {
            //未登录或者登录失效
            MessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
                confirmButtonText: '重新登录',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                //重新登录
                router.push({path:'/login'});
            })
        }
        else if (error.response.status === 500) {
            Message.error(error.response.data);
        }
    });

export default service