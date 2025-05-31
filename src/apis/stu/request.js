import { stuInstance } from '@/utils/request'
//分页查询已发送的站内信
export function stuGetEnterApi(data) {
  return stuInstance({
    url: '/api/application/listAllSend',
    method: 'post',
    data,
  })
}

//分页查询未通过的站内信
export function stuGetFailApi(data) {
  return stuInstance({
    url: '/api/application/listAll',
    method: 'post',
    data,
  })
}

//分页查询已通过的站内信
export function stuGetSuccessApi(data) {
  return stuInstance({
    url: '/api/application/listAllAccept',
    method: 'post',
    data,
  })
}

//分页查询已拒绝的站内信
export function stuGetRejectApi(data) {
  return stuInstance({
    url: '/api/application/listAllRefuse',
    method: 'post',
    data,
  })
}

//分页查询已删除的站内信
export function stuGetDeletedApi(data) {
  return stuInstance({
    url: '/api/application/listAllDelete',
    method: 'post',
    data,
  })
}

//同意某条站内信
export function stuSuccessRequestApi(data) {
  return stuInstance({
    url: '/api/application/acceptSingle',
    method: 'put',
    data,
  })
}

//拒绝某条站内信
export function stuRejectRequestApi(data) {
  return stuInstance({
    url: '/api/application/refuseSingle',
    method: 'put',
    data,
  })
}

//删除某条站内信请求
export function stuDeleteRequestApi(data) {
  return stuInstance({
    url: '/api/application/deleteSingle',
    method: 'delete',
    data,
  })
}

//发送申请站内信
export function stuEnterRequestApi(data) {
  return stuInstance({
    url: '/api/application/send',
    method: 'put',
    data,
  })
}
