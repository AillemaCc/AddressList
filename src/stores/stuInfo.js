import { defineStore } from 'pinia'
import {
  getStudentInfo,
  setStudentInfo,
  setStudentId,
  setStudentAccessToken,
  setStudentRefreshToken
} from '@/utils/storage.js'
import {ref} from 'vue'
export const useStuInfoStore = defineStore('stuInfo', () => {
  const stuInfo = ref(getStudentInfo())
  function setStuInfo(newValue) {
    stuInfo.value = newValue
    setStudentInfo(newValue)
  }

    function setStuId(newValue) {
      stuInfo.value.studentId = newValue
    setStudentId(newValue)
  }
  function setStuAccessToken(newValue) {
    stuInfo.value.accessToken = newValue
    setStudentAccessToken(newValue)
  }

  function setStuRefreshToken(newValue){
    stuInfo.value.refreshToken =newValue
    setStudentRefreshToken(newValue)
  }

  return {
    stuInfo,
    setStuInfo,
    setStuId,
    setStuAccessToken,
    setStuRefreshToken
  }
})
