import { stuInstance } from '@/utils/request'
//学生用户登录接口
export function stuLoginApi(data) {
  return stuInstance({
    url: '/api/stu/login',
    method: 'post',
    data,
  })
}
//学生用户注册接口
export function stuRegisterApi(data) {
  return stuInstance({
    url: '/api/stu/register',
    method: 'post',
    data,
  })
}
//检查用户登录状态接口
export function stuGetRemarkApi(data) {
  return stuInstance({
    url: '/api/stu/getRemark',
    method: 'get',
    data,
  })
}
