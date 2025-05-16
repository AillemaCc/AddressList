import { defineStore } from 'pinia'
import {
  getAdministrationInfo,
  setAdministrationInfo,
} from '@/utils/storage.js'

export const useAdminInfoStore = defineStore('adminInfo', () => {
  const adminInfo = getAdministrationInfo()

  function setAdminInfo(newValue) {
    adminInfo.value = setAdministrationInfo(newValue)
  }

  return { adminInfo, setAdminInfo }
})
