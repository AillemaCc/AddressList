import { defineStore } from 'pinia'
import {
  getAdministrationInfo,
  setAdministrationInfo,
  setAdministrationRefreshRequired,
  setAdministrationAccessToken,
} from '@/utils/storage.js'

export const useAdminInfoStore = defineStore('adminInfo', () => {
  const adminInfo = getAdministrationInfo()

  // 用来存储每个管理员的刷新状态和订阅者
  let isRefreshing = false
  let refreshSubscribers = []

  function setAdminInfo(newValue) {
    adminInfo.value = newValue
    setAdministrationInfo(newValue)
  }

  function setAdminRefreshRequired(newValue) {
    adminInfo.value.refreshRequired = newValue
    setAdministrationRefreshRequired(newValue)
  }

  function setAdminAccessToken(newValue) {
    adminInfo.value.accessToken = newValue
    setAdministrationAccessToken(newValue)
  }

  // 在Pinia的actions中定义操作
  function addRefreshSubscriber(cb) {
    refreshSubscribers.value.push(cb)
  }

  function clearRefreshSubscribers() {
    refreshSubscribers.value = []
  }

  function executeRefreshSubscribers(newToken) {
    refreshSubscribers.value.forEach((cb) => cb(newToken))
  }

  return {
    adminInfo,
    isRefreshing,
    refreshSubscribers,
    setAdminInfo,
    setAdminRefreshRequired,
    setAdminAccessToken,
    addRefreshSubscriber,
    clearRefreshSubscribers,
    executeRefreshSubscribers,
  }
})
