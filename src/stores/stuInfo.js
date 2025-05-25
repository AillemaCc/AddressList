import { defineStore } from 'pinia'
import {
  getStudentInfo,
  setStudentInfo,
  setStudentRefreshRequired,
  setStudentAccessToken,
} from '@/utils/storage.js'

export const useStuInfoStore = defineStore('stuInfo', () => {
  const stuInfo = getStudentInfo()
  const isRefreshing = false
  const refreshSubscribers = []
  function setStuInfo(newValue) {
    stuInfo.value = newValue
    setStudentInfo(newValue)
  }
  function setStuRefreshRequired(newValue) {
    setStudentRefreshRequired(newValue)
  }
  function setStuAccessToken(newValue) {
    setStudentAccessToken(newValue)
  }
  function addRefreshSubscriber(callback) {
    refreshSubscribers.push(callback)
  }
  function executeRefreshSubscribers(token) {
    refreshSubscribers.forEach((cb) => cb(token))
  }
  function clearRefreshSubscribers() {
    refreshSubscribers = []
  }

  return {
    stuInfo,
    isRefreshing,
    refreshSubscribers,
    setStuInfo,
    setStuRefreshRequired,
    setStuAccessToken,
    addRefreshSubscriber,
    clearRefreshSubscribers,
    executeRefreshSubscribers,
  }
})
