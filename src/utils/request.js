import axios from 'axios'

const stuInstance = axios.create({
  baseURL: 'http://127.0.0.1:4523/m1/6274780-5968989-default/api/stu',
  timeout: 5000,
})

const adminInstance = axios.create({
  baseURL: 'http://127.0.0.1:4523/m1/6274780-5968989-5f5be83e/api/admin',
  timeout: 5000,
})
