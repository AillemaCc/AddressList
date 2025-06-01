//学生部分
const studentInfo = 'STUDENT_INFO'

export function getStudentInfo() {
  return localStorage.getItem(studentInfo)
    ? JSON.parse(localStorage.getItem(studentInfo))
    : {
        studentId: '',
        accessToken: '',
        refreshToken: '',
      }
}

export function setStudentInfo(newValue) {
  localStorage.setItem(studentInfo, JSON.stringify(newValue))
}
export function setStudentId(newValue) {
  const info = getStudentInfo()
  info.studentId = newValue
  setStudentInfo(info)
}
export function setStudentAccessToken(newValue) {
  const info = getStudentInfo()
  info.accessToken = newValue
  setStudentInfo(info)
}
export function setStudentRefreshToken(newValue){
    const info = getStudentInfo()
    info.refreshToken = newValue
    setStudentInfo(info)
}

export function removeStudentInfo() {
  localStorage.removeItem(studentInfo)
}

//管理员部分
const adminInfo = 'ADMIN_INFO'

export function getAdministrationInfo() {
  return localStorage.getItem(adminInfo)
    ? JSON.parse(localStorage.getItem(adminInfo))
    : {
        username: '',
        accessToken: '',
        refreshToken: '',
      }
}

export function setAdministrationInfo(newValue) {
  localStorage.setItem(adminInfo, JSON.stringify(newValue))
}
export function setAdministrationUsername(newValue) {
  const info = getAdministrationInfo()
  info.username = newValue
  localStorage.setItem(adminInfo, JSON.stringify(info))
}

export function setAdministrationAccessToken(newValue) {
  const info = getAdministrationInfo()
  info.accessToken = newValue
  localStorage.setItem(adminInfo, JSON.stringify(info))
}

export function setAdministrationRefreshToken(newValue) {
  const info = getAdministrationInfo()
  info.refreshToken = newValue
  localStorage.setItem(adminInfo, JSON.stringify(info))
}

export function removeAdministrationInfo() {
  localStorage.removeItem(adminInfo)
}
