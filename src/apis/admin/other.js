import { adminInstance } from '@/utils/request'

//获取管理员主页信息
export function adminGetHomeInfoApi() {
  return adminInstance.get('/api/admin/homepage/data')
}
