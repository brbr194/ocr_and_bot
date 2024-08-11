import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'NewManage',
      component: () => import('@/views/NewManage.vue'),
      redirect: '/home',
      children: [
        { path: 'home', name: 'Home', component: () => import('@/views/manager/Home.vue')},
        { path: 'recognize', name: 'recognize', component: () => import('@/views/manager/recognize.vue')},
        { path: 'pay', name: 'pay', component: () => import('@/views/manager/pay.vue')},

      ]
    }
  ]
})

export default router
