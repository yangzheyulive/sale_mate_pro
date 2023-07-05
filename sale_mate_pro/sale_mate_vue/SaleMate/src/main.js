import Vue from 'vue'
import App from './App.vue'
import VueAxios from 'vue-axios';
import axios from 'axios';
import ElementUI from 'element-ui';
import VueTour from 'vue-tour'
import 'element-ui/lib/theme-chalk/index.css';
import 'vue-tour/dist/vue-tour.css';
import { Button, Select, Table, Input, Popover, Pagination } from 'element-ui';
import request from './utils/request.js'
import VueCookie from 'vue-cookie'            // api: https://github.com/alfhen/vue-cookie


Vue.component(Button.name, Button);
Vue.component(Select.name, Select);
Vue.component(Table.name, Table);
Vue.component(Input.name, Input);
Vue.component(Popover.name, Popover);
Vue.component(Pagination.name, Pagination);

Vue.prototype.http = request;
//引入vue-router
import VueRouter from 'vue-router'
//引入路由配置
import router from './router'
//路由前置守卫；用来设置元信息
router.beforeEach((to,from,next)=>{
  if(to.meta.title){
    document.title=to.meta.title
  }
  next()
})
//应用VueRouter
Vue.use(VueRouter)
Vue.use(ElementUI)
Vue.use(VueTour)
Vue.use(VueCookie)

Vue.config.productionTip = false
Vue.prototype.$axios = axios;
Vue.prototype.http = request;
new Vue({
  render: h => h(App),
  router:router
}).$mount('#app')
