import { adminInstance } from '@/utils/request'

//新增公告
export function adminAddBulletinApi(data) {
  return adminInstance({
    url: '/api/admin/board/add',
    method: 'put',
    data,
  })
}
//修改公告
export function adminUpdateBulletinApi(data) {
  return adminInstance({
    url: '/api/admin/board/update',
    method: 'post',
    data,
  })
}
//删除公告
export function adminDeleteBulletinApi(data) {
  return adminInstance({
    url: '/api/admin/board/delete',
    method: 'delete',
    data,
  })
}

//分页查询所有草稿
export function adminDisplayDraftApi(data) {
  return adminInstance({
    url: '/api/admin/board/queryAllDraft',
    method: 'post',
    data,
  })
}

//分页查询所有已发布公告
export function adminDisplayReleasedApi(data) {
  return adminInstance({
    url: '/api/admin/board/queryAllReleased',
    method: 'post',
    data,
  })
}

//分页查询所有已下架公告
export function adminDisplayPulloffApi(data) {
  return adminInstance({
    url: '/api/admin/board/queryAllPullOff',
    method: 'post',
    data,
  })
}

//分页查询所有已删除公告
export function adminDisplayDeletedApi(data) {
  return adminInstance({
    url: '/api/admin/board/queryAllDeleted',
    method: 'post',
    data,
  })
}

//根据公告标识号查询公告
export function adminQueryBulletinApi(data) {
  return adminInstance({
    url: '/api/admin/board/queryById',
    method: 'post',
    data,
  })
}

//根据公告标识号发布草稿
export function adminReleaseDraftApi(data) {
  return adminInstance({
    url: '/api/admin/board/release',
    method: 'post',
    data,
  })
}

//根据公告标识号恢复已下架公告
export function adminRestorePulloffApi(data) {
  return adminInstance({
    url: '/api/admin/board/restore',
    method: 'post',
    data,
  })
}

//根据公告标识号下架公告
export function adminPulloffReleasedApi(data) {
  return adminInstance({
    url: '/api/admin/board/pullOff',
    method: 'post',
    data,
  })
}
