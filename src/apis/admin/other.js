import { adminInstance } from '@/utils/request'

//获取管理员主页信息
export function adminGetHomeInfoApi() {
  return adminInstance.get('/api/admin/homepage/data')
}

//导出全量学生学籍信息
export function adminExportAllApi() {
  return adminInstance({
    url: '/api/admin/execl/exportStuDef',
    method: 'get',
    responseType: 'blob',
  })
}

//条件导出学籍信息
export function adminExportDetailApi(params) {
  return adminInstance({
    url: '/api/admin/execl/exportStuDefByCondition',
    method: 'get',
    params,
    responseType: 'blob',
  })
}
