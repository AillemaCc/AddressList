<script setup>
import { ref } from 'vue'
const studentId = ref('')
const password = ref('')
// 小眼睛状态
const isPasswordVisible = ref(false)
// 各个框的的标签上移状态
const hasStudentIdFocused = ref(false)
const hasPasswordFocused = ref(false)

function changePasswordVisible() {
  isPasswordVisible.value = !isPasswordVisible.value
}
function handleStudentIdFocus() {
  hasStudentIdFocused.value = true
}

function handlePasswordFocus() {
  hasPasswordFocused.value = true
}

import { stuGetRemarkApi, stuLoginApi } from '@/apis/stu/login'
import { useStuInfoStore } from '@/stores/stuInfo'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
const router = useRouter()
const stuInfoStore = useStuInfoStore()
async function login() {
  const resp = await stuGetRemarkApi({
    studentId: studentId.value,
    password: password.value,
  })
  if (resp.success) {
    if (resp.data.status === 1) {
      const res = await stuLoginApi({
        studentId: studentId.value,
        password: password.value,
      })
      if (res.success) {
        stuInfoStore.setStuInfo({
          studentId: studentId.value,
          accessToken: res.data.accessToken,
          refreshToken: res.data.refreshToken,
          refreshRequired: false,
        })
        ElMessage({
          message: res.message,
          type: 'success',
          duration: 2000,
        })

          router.push('/stu/home')

      } else {
        ElMessage.error(res.message)
      }
    } else {
      router.push(`/stu/wait?status=${resp.data.status}`)
    }
  } else {
    ElMessage.error(resp.message)
  }
}
</script>

<template>
  <div class="container">
    <div class="header">
      <div class="logo-container">
        <img class="logo" src="../../../assets/imgs/txl.png" alt="" />
      </div>
      <div class="title-container">网上通讯录</div>
    </div>
    <div class="main-container">
      <div class="main-box-container">
        <div class="background-container">
          <div class="img">
            <img src="../../../assets/imgs/txl.png" alt="" />
          </div>
          <div class="title">网上通讯录</div>
        </div>
        <div class="form-container">
          <div class="loginForm">
            <h1>学生登录</h1>
            <div class="studentId">
              <input
                class="studentIdInput"
                type="text"
                id="studentId"
                v-model="studentId"
                @focus="handleStudentIdFocus"
              />
              <label
                for="studentId"
                :class="{ 'label-up': hasStudentIdFocused }"
                >学号</label
              >
              <div class="icon"><i class="iconfont icon-yonghu"></i></div>
            </div>

            <div class="password">
              <input
                class="passwordInput"
                :type="isPasswordVisible ? 'text' : 'password'"
                id="password"
                v-model="password"
                @focus="handlePasswordFocus"
              /><label
                for="password"
                :class="{ 'label-up': hasPasswordFocused }"
                >密码</label
              >
              <div class="icon" @click="changePasswordVisible">
                <i
                  class="iconfont passwordEye"
                  :class="
                    isPasswordVisible
                      ? ['icon-xiaoyanjingguanbi']
                      : ['icon-xiaoyanjingdakai']
                  "
                ></i>
              </div>
            </div>
            <div class="loginButtonContainer">
              <button class="loginButton" @click="login">登录</button>
            </div>
            <div class="toRigister">
              还没有账号？<router-link to="/stu/register">立即注册</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped lang="scss">
.container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: url(../../../assets/imgs/n_b2.jpg);
  .header {
    display: flex;
    align-items: center;
    width: 100%;
    height: 150px;
    .logo-container {
      display: flex;
      justify-content: center;
      align-items: center;
      margin-left: 80px;
      width: 96px;
      height: 96px;
      .logo {
        width: 100%;
        height: 100%;
      }
    }
    .title-container {
      font-size: 24px;
      font-weight: 600;
      color: $mainColor;
    }
  }
  .main-container {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    .main-box-container {
      display: flex;
      justify-content: center;
      align-items: center;
      border-radius: 10px;
      overflow: hidden;
      box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
      transform: translateY(-40px);
      @media screen and (max-height: 950px) {
        transform: translateY(0px);
      }
      .background-container {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        width: 450px;
        height: 500px;
        color: #326bcc;
        font-size: 30px;
        font-weight: 600;
        background: linear-gradient(
          135deg,
          #c6e2ff 0%,
          #f7f8fa 33%,
          #fef7f1 66%,
          #fef3ef 100%
        );
        .img {
          width: 200px;
          height: 200px;
          img {
            width: 100%;
            height: 100%;
          }
        }
        @media screen and (max-width: 940px) {
          display: none;
        }
      }
      .form-container {
        width: 450px;
        height: 500px;
      }
    }
  }
}
.loginForm {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 70px 40px;
  width: 100%;
  height: 100%;
  background-color: #fff;
  h1 {
    color: $mainColor;
    margin-bottom: 20px;
  }
  .studentId,
  .password,
  .toRigister {
    display: flex;
    position: relative;
  }
  .studentId,
  .password {
    transition: 0.3s;
    border-bottom: 1px solid #000;
  }
  label {
    position: absolute;
    top: 50%;
    left: 0;
    font-size: 16px;
    transform: translateY(-50%);
    transition: 0.3s;
    &.label-up {
      transform: translateY(-180%);
    }
  }
  .studentIdInput,
  .passwordInput {
    width: 330px;
    height: 40px;
    font-size: 16px;
    border: none;
    outline: none;
  }
  .studentIdInput:focus + label {
    color: $mainColor;
  }
  .passwordInput:focus + label {
    color: $mainColor;
  }
  .studentId:focus-within,
  .password:focus-within {
    border-bottom: 1px solid $mainColor;
  }
  .icon {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 40px;
    height: 100%;
    .iconfont {
      font-size: 20px;
      color: #000;
    }
    .passwordEye {
      cursor: pointer;
    }
  }
  .toRigister {
    display: flex;
    justify-content: center;
  }
  a {
    color: $mainColor;
    text-decoration: none;
  }
  a:hover {
    text-decoration: underline;
  }
  .loginButtonContainer {
    display: flex;
    justify-content: center;
    .loginButton {
      width: 100%;
      height: 40px;
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      background-color: #275dbb;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      &:hover {
        background-color: #3b76da;
      }
    }
  }
}
</style>
