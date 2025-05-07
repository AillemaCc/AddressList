import stuInstance from '@/utils/request'

export function stuRegisterApi() {
  return stuInstance.axios({
    url: '/register',
    method: 'post',
  })
}
