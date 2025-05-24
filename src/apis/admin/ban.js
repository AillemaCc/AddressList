import { adminInstance } from '@/utils/request'

//根据学号ban用户
export function adminBanStudentApi(data) {
  return adminInstance({
    url: '/api/admin/info/ban',
    method: 'post',
    data,
  })
}
//根据学号unban用户
export function adminUnbanStudentApi(data) {
  return adminInstance({
    url: '/api/admin/info/unBan',
    method: 'post',
    data,
  })
}
