import { adminInstance } from '@/utils/request'

//管理员用户登录接口
export function adminLoginApi(data) {
  return adminInstance({
    url: '/api/admin/login',
    method: 'post',
    data,
  })
}

//管理员用户登出接口
export function adminLogoutApi(data) {
  return adminInstance({
    url: '/api/admin/logout',
    method: 'delete',
    data,
  })
}
