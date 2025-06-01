import { defineStore } from 'pinia'
import {
  getAdministrationInfo,
  setAdministrationInfo,
  setAdministrationUsername,
  setAdministrationAccessToken,setAdministrationRefreshToken
} from '@/utils/storage.js'
import {ref} from 'vue'
export const useAdminInfoStore = defineStore('adminInfo', () => {
  const adminInfo = ref(getAdministrationInfo())

  function setAdminInfo(newValue) {
    adminInfo.value = newValue
    setAdministrationInfo(newValue)
  }

  function setAdminUsername(newValue){
    adminInfo.value.username = newValue
    setAdministrationUsername (newValue)
  }

  function setAdminAccessToken(newValue) {
    adminInfo.value.accessToken = newValue
    setAdministrationAccessToken(newValue)
  }
  function setAdminRefreshToken(newValue) {
    adminInfo.value.refreshToken = newValue
    setAdministrationRefreshToken(newValue)
  }
  return {
    adminInfo,
    setAdminInfo,
    setAdminUsername,
    setAdminAccessToken,
    setAdminRefreshToken
  }
})
