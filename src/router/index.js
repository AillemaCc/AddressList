import { createRouter, createWebHistory } from 'vue-router'
import select from '@/views/select/index.vue'
import stu_layout from '@/views/stu/layout/index.vue'
import stu_home from '@/views/stu/home/index.vue'
import stu_friends from '@/views/stu/friends.vue'
import stu_friends_deleted from '@/views/stu/friendsDeleted.vue'
import stu_enter from '@/views/stu/enter.vue'
import stu_fail from '@/views/stu/fail.vue'
import stu_success from '@/views/stu/success.vue'
import stu_reject from '@/views/stu/reject.vue'
import stu_deleted from '@/views/stu/deleted.vue'
import stu_query from '@/views/stu/query/index.vue'
import stu_login from '@/views/stu/login/index.vue'
import stu_register from '@/views/stu/register/index.vue'
import stu_wait from '@/views/stu/wait/index.vue'
import stu_bulletinDetail from '@/views/stu/bulletin/detail.vue'

import admin_layout from '@/views/admin/layout/index.vue'
import admin_home from '@/views/admin/home/index.vue'
import admin_request_fail from '@/views/admin/request/fail.vue'
import admin_request_success from '@/views/admin/request/success.vue'
import admin_request_reject from '@/views/admin/request/reject.vue'
import admin_query from '@/views/admin/query/index.vue'
import admin_login from '@/views/admin/login/index.vue'
import admin_bulletin_draft from '@/views/admin/bulletin/draft.vue'
import admin_bulletin_released from '@/views/admin/bulletin/released.vue'
import admin_bulletin_pulledOff from '@/views/admin/bulletin/pulledOff.vue'
import admin_bulletin_deleted from '@/views/admin/bulletin/deleted.vue'
import admin_bulletin_edit from '@/views/admin/bulletin/edit.vue'

import { useStuInfoStore } from '@/stores/stuInfo'
import { useAdminInfoStore } from '@/stores/adminInfo'

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
            { path: 'friends_deleted', component: stu_friends_deleted },
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
        {
          path: 'wait',
          component: stu_wait,
        },
        {
          path: 'bulletin/detail',
          component: stu_bulletinDetail,
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
              path: 'bulletin_pulledOff',
              component: admin_bulletin_pulledOff,
            },
            {
              path: 'bulletin_deleted',
              component: admin_bulletin_deleted,
            },
            {
              path: 'bulletin_edit',
              component: admin_bulletin_edit,
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

const stuCantAccessPath = [
  '/stu/home',
  '/stu/friends',
  '/stu/enter',
  '/stu/fail',
  '/stu/success',
  '/stu/reject',
  '/stu/deleted',
  '/stu/query',
  '/stu/bulletin/detail',
]

const adminCantAccessPath = [
  '/admin/home',
  '/admin/query',
  '/admin/request_fail',
  '/admin/request_success',
  '/admin/request_reject',
  '/admin/bulletin_draft',
  '/admin/bulletin_released',
  '/admin/bulletin_pulledOff',
  '/admin/bulletin_deleted',
  '/admin/bulletin_edit',
]
router.beforeEach((to, from, next) => {
  const stuInfoStore = useStuInfoStore()
  const adminInfoStore = useAdminInfoStore()

  // 1. 先检查学生权限路径
  if (stuCantAccessPath.includes(to.fullPath)) {
    if (stuInfoStore.stuInfo.accessToken) {
      return next()
    } else {
      return next('/stu/login') // 使用 return 终止后续逻辑
    }
  }

  // 2. 再检查管理员权限路径
  if (adminCantAccessPath.includes(to.fullPath)) {
    if (adminInfoStore.adminInfo.accessToken) {
      return next()
    } else {
      return next('/admin/login') // 使用 return 终止后续逻辑
    }
  }

  // 3. 默认放行
  next()
})

export default router
