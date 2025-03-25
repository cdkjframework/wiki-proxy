import type {RouteRecordRaw} from 'vue-router'
import {createRouter, createWebHashHistory} from 'vue-router'

const LoginView = () => import('@/views/LoginView.vue')
const NotFound = () => import('@/views/error/404.vue')
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'defaultLogin',
    meta: {
      title: '登录',
      local: 'login.login'
    },
    component: LoginView
  },
  {
    path: '/login',
    name: 'Login',
    meta: {
      title: '登录',
      local: 'login.login'
    },
    component: LoginView
  },
  {
    path: '/404',
    name: 'NotFound',
    meta: {
      title: '404',
      local: 'login.login'
    },
    component: NotFound
  }
]
const router = createRouter({
  history: createWebHashHistory(),
  // mode: 'hash',
  routes
})

export default router
