import { stuInstance } from '@/utils/request'

//新增通讯信息
export function stuCreateAddressApi(data) {
  return stuInstance({
    url: '/api/stu/info/contact/add',
    method: 'put',
    data,
  })
}

//删除通讯信息
export function stuDeleteAddressApi(data) {
  return stuInstance({
    url: '/api/stu/info/contact/delete',
    method: 'delete',
    data,
  })
}

//修改通讯信息
export function stuUpdateAddressApi(data) {
  return stuInstance({
    url: '/api/stu/info/contact/update',
    method: 'post',
    data,
  })
}

//按学号查询拥有的通讯信息
export function stuQueryFriendsApi(data) {
  return stuInstance({
    url: '/api/stu/info/contact/query',
    method: 'get',
    data,
  })
}

//分页查询个人全量通讯信息
export function stuGetFriendsApi(data) {
  return stuInstance({
    url: '/api/stu/info/contact/list',
    method: 'get',
    data,
  })
}

//查询自己删除的通讯录信息
export function stuQueryDeletedApi(data) {
  return stuInstance({
    url: '/api/stu/info/contact/listAllDelete',
    method: 'get',
    data,
  })
}
