import { stuInstance } from '@/utils/request'

//通过姓名查询学生信息
export function stuNameQueryApi(data) {
  return stuInstance({
    url: '/api/application/querySomeone',
    method: 'post',
    data,
  })
}

//学生端HomePage个人信息查询接口
export function stuHomePageInfoApi(data) {
  return stuInstance({
    url: '/api/stu/homepage/query',
    method: 'post',
    data,
  })
}
