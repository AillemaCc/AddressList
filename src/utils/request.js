import axios from 'axios'
import { useStuInfoStore } from '@/stores/stuInfo'
import { useAdminInfoStore } from '@/stores/adminInfo'
import { ElMessage } from 'element-plus'
import { removeAdministrationInfo, removeStudentInfo } from './storage'

//学生请求实例
export const stuInstance = axios.create({
  baseURL: 'http://127.0.0.1:4523/m1/6274780-5968989-default',
  timeout: 5000,
})
// 学生请求拦截器
stuInstance.interceptors.request.use(async (config) => {
  const stuInfoStore = useStuInfoStore()
  const stuInfo = stuInfoStore.getStuInfo()

  // 添加 accessToken
  if (stuInfo.accessToken) {
    config.headers.studentId = stuInfo.studentId
    config.headers.accessToken = stuInfo.accessToken
  }

  // token 刷新逻辑
  if (stuInfo.refreshRequired && !stuInfoStore.isRefreshing) {
    stuInfoStore.isRefreshing = true

    try {
      const { data } = await axios.post('/api/student/refreshtoken', {
        studentId: stuInfo.studentId,
        refreshToken: stuInfo.refreshToken,
      })

      stuInfoStore.setStuAccessToken(data.accessToken)
      stuInfoStore.setStuRefreshRequired(false)

      stuInfoStore.executeRefreshSubscribers(data.accessToken)
      stuInfoStore.clearRefreshSubscribers()
    } catch (err) {
      stuInfoStore.setStuInfo(null)
      removeStudentInfo()
      window.location.href = '/student/login'
      ElMessage.error('登录状态异常，请重新登录')
    } finally {
      stuInfoStore.isRefreshing = false
    }
  }

  // 正在刷新时的请求处理
  if (stuInfoStore.isRefreshing) {
    return new Promise((resolve) => {
      stuInfoStore.addRefreshSubscriber((newToken) => {
        config.headers.accessToken = newToken
        resolve(config)
      })
    })
  }

  return config
})

// 学生响应拦截器
stuInstance.interceptors.response.use(
  (response) => {
    if (response.headers['x-refresh-required'] === 'true') {
      const stuInfoStore = useStuInfoStore()
      stuInfoStore.setStuRefreshRequired(true)
    }

    if (response.data.code === 401) {
      const stuInfoStore = useStuInfoStore()

      if (response.data.message.includes('refreshToken无效')) {
        removeStudentInfo()
        window.location.href = '/student/login'
        ElMessage.error('登录过期，请重新登录')
      } else if (response.data.message === 'access') {
        return axios
          .post('/api/student/refreshtoken', {
            studentId: stuInfo.studentId,
            refreshToken: stuInfo.refreshToken,
          })
          .then((res) => {
            stuInfoStore.setStuAccessToken(res.data.accessToken)
            ElMessage.warning('请重试操作')
          })
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
  const adminInfoStore = useAdminInfoStore()
  const adminInfo = adminInfoStore.getStuInfo()

  if (adminInfo.accessToken) {
    config.headers.username = adminInfo.username
    config.headers.accessToken = adminInfo.accessToken
  }
  if (adminInfo.refreshRequired && !adminInfoStore.isRefreshing) {
    adminInfoStore.isRefreshing = true

    try {
      const { data } = await axios.post('/api/stu/refreshToken', {
        username: adminInfo.username,
        refreshToken: adminInfo.refreshToken,
      })
      // 更新accessToken,下次不需要携带Refreshtoken
      adminInfoStore.setAdminAccessToken(data.accessToken)
      adminInfoStore.setAdminRefreshRequired(false)

      // 调用 actions 来执行回调
      adminInfoStore.executeRefreshSubscribers(data.accessToken)
      adminInfoStore.clearRefreshSubscribers()
    } catch (err) {
      adminInfoStore.setAdminInfo(null) // 清除无效 token
      removeAdministrationInfo()
      window.location.href = '/admin/login'
      ElMessage.error('登录状态异常，请重新登录')
    } finally {
      adminInfoStore.isRefreshing = false
    }
  }

  if (adminInfoStore.isRefreshing) {
    return new Promise((resolve) => {
      adminInfoStore.addRefreshSubscriber((newToken) => {
        config.headers.accessToken = newToken
        resolve(config)
      })
    })
  }

  return config
})
//管理员响应拦截器
adminInstance.interceptors.response.use(
  function (response) {
    if (response.headers['x-refresh-required'] === 'true') {
      const adminInfoStore = useAdminInfoStore()
      adminInfoStore.setAdminRefreshRequired(true)
    }

    if (response.data.code === 401) {
      const adminInfoStore = useAdminInfoStore()

      if (
        response.data.message ===
        '会话已过期：refreshToken无效或已过期，请重新登录'
      ) {
        removeAdministrationInfo
        window.location.href = '/admin/login'
        ElMessage.error('登录过期，请重新登录')
      } else if (response.data.message === 'access') {
        axios
          .post('/api/admin/refreshtoken', {
            username: adminInfo.username,
            refreshToken: adminInfo.refreshToken,
          })
          .then((res) => {
            adminInfoStore.setAdminAccessToken(res.data.accessToken)
            ElMessage.warning('请重试操作')
          })
      }
    }

    return response.data
  },
  function (error) {
    return Promise.reject(error)
  },
)
