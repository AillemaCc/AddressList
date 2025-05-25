import { stuInstance } from '@/utils/request'
//分页查询个人全量通讯信息
export function stuGetFriendsApi(data) {
  return stuInstance({
    url: '/api/stu/info/contact/list',
    method: 'get',
    data,
  })
}
