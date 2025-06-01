import axios from 'axios'
import { useStuInfoStore } from '@/stores/stuInfo'
import { useAdminInfoStore } from '@/stores/adminInfo'
import { ElMessage } from 'element-plus'
import { removeAdministrationInfo, removeStudentInfo } from './storage'
import { stuRefreshToken,stuIsRefreshRequest } from './refresh'
import { useRouter } from 'vue-router'

//学生请求实例
export const stuInstance = axios.create({
  baseURL: 'http://127.0.0.1:4523/m1/6274780-5968989-default',
  timeout: 5000,
})
// 学生请求拦截器
stuInstance.interceptors.request.use(async (config) => {
  config.headers['Content-Type'] = 'application/json;charset=UTF-8'
  const stuInfoStore = useStuInfoStore()
  const stuInfo = stuInfoStore.stuInfo

  // 添加 accessToken
  if (stuInfo.accessToken) {
    config.headers.studentId = stuInfo.studentId
    config.headers.accessToken = stuInfo.accessToken
  }
  return config
})

// 学生响应拦截器
stuInstance.interceptors.response.use(
  async (response) => {
    const stuInfoStore = useStuInfoStore()
    if(response?.data?.data?.accessToken){
      stuInfoStore.setStuAccessToken(response.data.data.accessToken)
      
    }
    if(response?.data?.data?.refreshToken){
      stuInfoStore.setStuRefreshToken(response.data.data.refreshToken)
    }
    if(response.data.code === '401' && !stuIsRefreshRequest(response.config)){
      const isSuccess = await stuRefreshToken()
      if(isSuccess){
        response.config.headers.accessToken = stuInfoStore.stuInfo.accessToken
        const resp = await stuInstance.request(response.config)
        return resp
      }else{
        const router = useRouter()
        router.push('/stu/login')
        removeStudentInfo()
        stuInfoStore.stuInfo = {}
        ElMessage('登录已过期，请重新登录')
      }
      
    }
    return response.data
  },
  (error) => {
    return Promise.reject(error)
  },
)
//管理员请求实例
export const adminInstance = axios.create({
  baseURL: 'http://127.0.0.1:4523/m1/6274780-5968989-5f5be83e',
  timeout: 5000,
})
//管理员请求拦截器
adminInstance.interceptors.request.use(async (config) => {
  config.headers['Content-Type'] = 'application/json;charset=UTF-8'
  const adminInfoStore = useAdminInfoStore()
  const adminInfo = adminInfoStore.adminInfo

  if (adminInfo.accessToken) {
    config.headers.username = adminInfo.username
    config.headers.accessToken = adminInfo.accessToken
  }
 
  return config
})
//管理员响应拦截器
adminInstance.interceptors.response.use(
  async function (response) {
    const adminInfoStore = useAdminInfoStore()
    if(response?.data?.data?.accessToken){
      adminInfoStore.setAdminAccessToken(response.data.data.accessToken)
      
    }
    if(response?.data?.data?.refreshToken){
      adminInfoStore.setAdminRefreshToken(response.data.data.refreshToken)
    }
    if(response.data.code === '401' && !adminIsRefreshRequest(response.config)){
      const isSuccess = await adminRefreshToken()
      if(isSuccess){
        response.config.headers.accessToken = adminInfoStore.stuInfo.accessToken
        const resp = await adminInstance.request(response.config)
        return resp
      }else{
        const router = useRouter()
        router.push('/admin/login')
        removeAdministrationInfo()
        adminInfoStore.adminInfo = {}
        ElMessage('登录已过期，请重新登录')
      }
    }
    return response.data
  },
  function (error) {
    return Promise.reject(error)
  },
)
