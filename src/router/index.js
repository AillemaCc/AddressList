import { createRouter, createWebHistory } from 'vue-router'
import select from '@/views/select/index.vue'
import layout from '@/views/stu/layout/index.vue'
import home from '@/views/stu/home/index.vue'
import query from '@/views/stu/query/index.vue'
import login from '@/views/stu/login/index.vue'
import register from '@/views/stu/register/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/select',
    },
    {
      path: '/select',
      component: select,
    },
    {
      path: '/stu',
      redirect: '/stu/home',
      children: [
        {
          path: '/stu/',
          component: layout,
          redirect: '/stu/home',
          children: [
            { path: 'home', component: home },
            { path: 'query', component: query },
          ],
        },
        {
          path: 'login',
          component: login,
        },
        {
          path: 'register',
          component: register,
        },
      ],
    },
    {
      path: '/admin',
      children: [],
    },
  ],
})

const cantAccessPath = ['/stu/home', '/stu/query']

router.beforeEach((to, from) => {
  if (cantAccessPath.includes(to.fullPath)) {
    //判断是否存在token
    //存在
    //不存在
  }
  console.log(to)
})

export default router
