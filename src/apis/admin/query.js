import { adminInstance } from '@/utils/request'

//分页展示某个学院下的专业信息
export function adminDisplayMajorApi(data) {
  return adminInstance({
    url: '/api/admin/base/listAcademyMajor',
    method: 'post',
    data,
  })
}
//分页展示某个专业下的班级信息
export function adminDisplayClassApi(data) {
  return adminInstance({
    url: '/api/admin/base/listMajorClass',
    method: 'post',
    data,
  })
}
//分页展示某个班级下的学生信息
export function adminDisplayStudentApi(data) {
  return adminInstance({
    url: '/api/admin/base/listClassStu',
    method: 'post',
    data,
  })
}
//更新班级名称
export function adminUpdateClassNameApi(data) {
  return adminInstance({
    url: '/api/admin/base/updateClass',
    method: 'post',
    data,
  })
}

//更新班级信息
export function adminUpdateClassApi(data) {
  return adminInstance({
    url: '/api/admin/base/updateClassMA',
    method: 'post',
    data,
  })
}

//更新专业名称和所属学院名称
export function adminUpdateMajorApi(data) {
  return adminInstance({
    url: '/api/admin/base/updateMajor',
    method: 'post',
    data,
  })
}

//更新专业所属学院
export function adminUpdateMajorAcademyApi(data) {
  return adminInstance({
    url: '/api/admin/base/changeMajorAcademy',
    method: 'post',
    data,
  })
}

//更新学院信息
export function adminUpdateAcademyApi(data) {
  return adminInstance({
    url: '/api/admin/base/updateAcademy',
    method: 'post',
    data,
  })
}

//新增班级信息
export function adminAddClassApi(data) {
  return adminInstance({
    url: '/api/admin/base/addClass',
    method: 'put',
    data,
  })
}

//新增专业信息
export function adminAddMajorApi(data) {
  return adminInstance({
    url: '/api/admin/base/addMajor',
    method: 'put',
    data,
  })
}
