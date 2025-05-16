import { defineStore } from 'pinia'
import { getStudentInfo, setStudentInfo } from '@/utils/storage.js'

export const useStuInfoStore = defineStore('stuInfo', () => {
  const stuInfo = getStudentInfo()

  function setStuInfo(newValue) {
    stuInfo.value = setStudentInfo(newValue)
  }

  return { stuInfo, setStuInfo }
})
