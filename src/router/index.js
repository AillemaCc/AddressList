import { createRouter, createWebHistory } from 'vue-router'
import select from '@/views/select/index.vue'
import stu_layout from '@/views/stu/layout/index.vue'
import stu_home from '@/views/stu/home/index.vue'
import stu_friends from '@/views/stu/friends.vue'
import stu_enter from '@/views/stu/enter.vue'
import stu_fail from '@/views/stu/fail.vue'
import stu_success from '@/views/stu/success.vue'
import stu_reject from '@/views/stu/reject.vue'
import stu_deleted from '@/views/stu/deleted.vue'
import stu_query from '@/views/stu/query/index.vue'
import stu_login from '@/views/stu/login/index.vue'
import stu_register from '@/views/stu/register/index.vue'

import admin_layout from '@/views/admin/layout/index.vue'
import admin_home from '@/views/admin/home/index.vue'
import admin_request_fail from '@/views/admin/request/fail.vue'
import admin_request_success from '@/views/admin/request/success.vue'
import admin_request_reject from '@/views/admin/request/reject.vue'
import admin_query from '@/views/admin/query/index.vue'
import admin_login from '@/views/admin/login/index.vue'
import admin_bulletin_draft from '@/views/admin/bulletin/draft.vue'
import admin_bulletin_released from '@/views/admin/bulletin/released.vue'
import admin_bulletin_pulledOf from '@/views/admin/bulletin/pulledOf.vue'
import admin_bulletin_deleted from '@/views/admin/bulletin/deleted.vue'

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
          component: stu_layout,
          redirect: '/stu/home',
          children: [
            { path: 'home', component: stu_home },
            { path: 'friends', component: stu_friends },
            { path: 'enter', component: stu_enter },
            { path: 'fail', component: stu_fail },
            { path: 'success', component: stu_success },
            { path: 'reject', component: stu_reject },
            { path: 'deleted', component: stu_deleted },
            { path: 'query', component: stu_query },
          ],
        },
        {
          path: 'login',
          component: stu_login,
        },
        {
          path: 'register',
          component: stu_register,
        },
      ],
    },
    {
      path: '/admin',
      redirect: '/admin/home',
      children: [
        {
          path: '/admin/',
          component: admin_layout,
          redirect: '/admin/home',
          children: [
            { path: 'home', component: admin_home },
            { path: 'request_fail', component: admin_request_fail },
            { path: 'request_success', component: admin_request_success },
            { path: 'request_reject', component: admin_request_reject },
            { path: 'query', component: admin_query },
            {
              path: 'bulletin_draft',
              component: admin_bulletin_draft,
            },
            {
              path: 'bulletin_released',
              component: admin_bulletin_released,
            },
            {
              path: 'bulletin_pulledOf',
              component: admin_bulletin_pulledOf,
            },
            {
              path: 'bulletin_deleted',
              component: admin_bulletin_deleted,
            },
          ],
        },
        {
          path: 'login',
          component: admin_login,
        },
      ],
    },
  ],
})

const cantAccessPath = [
  '/stu/home',
  '/stu/friends',
  '/stu/enter',
  '/stu/fail',
  '/stu/success',
  '/stu/reject',
  '/stu/deleted',
  '/stu/query',
]

router.beforeEach((to, from) => {
  if (cantAccessPath.includes(to.fullPath)) {
    //判断是否存在token
    //存在
    //不存在
  }
  console.log(to)
})

export default router
