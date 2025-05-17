const studentInfo = STUDENT_INFO

export function getStudentInfo() {
  return localStorage.getItem(studentInfo)
    ? JSON.parse(localStorage.getItem(studentInfo))
    : { studentId: '', studentToken: '' }
}

export function setStudentInfo(newValue) {
  localStorage.setItem(studentInfo, JSON.stringify(newValue))
}

export function removeStudentInfo() {
  localStorage.removeItem(studentInfo)
}

const adminInfo = ADMIN_INFO

export function getAdministrationInfo() {
  return localStorage.getItem(adminInfo)
    ? JSON.parse(localStorage.getItem(adminInfo))
    : { username: '', adminToken: '' }
}

export function setAdministrationInfo(newValue) {
  localStorage.setItem(adminInfo, JSON.stringify(newValue))
}

export function removeAdministrationInfo() {
  localStorage.removeItem(adminInfo)
}
