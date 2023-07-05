//该页面用于创建整个应用的路由管理者router
import VueRouter from "vue-router";

//引入对应的路由组件
import Login from '../pages/login/index'
import Home from '../pages/home/index'
import Vector from '../pages/vector/index'
import material from '../pages/material/index'
import VectorTable from '../pages/vector/vector-table'
import VectorInfo from '../pages/vector/vector-info'

//创建并暴露一个router
export default new VueRouter({
	routes: [
		{
			path: '/login',
			component: Login,
			meta: {
				title: "登录"
			}
		},
		{
			path: '/home',
			component: Home,
			meta: {
				title: "首页"
			},children:[

			]
		},
		{
			path: '/vector',
			component: Vector,
			meta: {
				title: "客户分层"
			},
			children:[
				{
					path: '/vector/table',
					component: VectorTable,
					meta: {
						title: "客户分层"
					}
				},{
					path: '/vector/info',
					component: VectorInfo,
					meta: {
						title: "客户分层"
					}
				}
			]
		},
		{
			path: '/material',
			component: material,
			meta: {
				title: "素材库"
			}
		},

		{
			path: '*',
			redirect: '/login'
		}
	]
})
