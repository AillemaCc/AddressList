import axios from 'axios'

export const stuInstance = axios.create({
  baseURL: 'http://127.0.0.1:4523/m1/6274780-5968989-default',
  timeout: 5000,
})
stuInstance.interceptors.request.use(
  function (config) {
    // 在发送请求之前做些什么
    return config
  },
  function (error) {
    // 对请求错误做些什么
    return Promise.reject(error)
  },
)

// 添加响应拦截器
stuInstance.interceptors.response.use(
  function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    const res = response.data
    return res
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error)
  },
)

export const adminInstance = axios.create({
  baseURL: 'http://127.0.0.1:4523/m1/6274780-5968989-5f5be83e',
  timeout: 5000,
})
