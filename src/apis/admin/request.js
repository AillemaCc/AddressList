import { adminInstance } from '@/utils/request'

//分页展示所有待审核请求
export function adminDisplayFailRequestApi() {
  return adminInstance({
    url: '/api/admin/info/auditList',
    method: 'get',
  })
}

//分页展示所有已通过的审核请求
export function adminDisplaySuccessRequestApi() {
  return adminInstance({
    url: '/api/admin/info/validList',
    method: 'get',
  })
}

//分页展示拒绝请求
export function adminDisplayRejectRequestApi() {
  return adminInstance({
    url: '/api/admin/info/refuseList',
    method: 'get',
  })
}

//管理员同意请求接口
export function adminSuccessRequestApi(data) {
  return adminInstance({
    url: '/api/admin/info/accept',
    method: 'post',
    data,
  })
}
//管理员拒绝注册请求接口
export function adminRejectRequestApi(data) {
  return adminInstance({
    url: '/api/admin/info/refuse',
    method: 'post',
    data,
  })
}
