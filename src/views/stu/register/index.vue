<script setup>
import { ref } from 'vue'
const studentId = ref('')
const password = ref('')
const name = ref('')
const phoneNumber = ref('')
const email = ref('')

// 小眼睛状态
const isPasswordVisible = ref(false)
function changePasswordVisible() {
  isPasswordVisible.value = !isPasswordVisible.value
}

// 各个框的的标签上移状态
const hasFocus = ref({
  studentId: false,
  name: false,
  phoneNumber: false,
  email: false,
  password: false,
})

function handleFocus(key) {
  hasFocus.value[key] = true
}
</script>
<template>
  <div class="container">
    <div class="logo-container">
      <div class="logo-img-container">
        <img
          class="logo-img"
          src="../../../assets/imgs/academic_logo.png"
          alt=""
        />
      </div>
    </div>
    <div class="main-container">
      <div class="main-box-container">
        <div class="background-container">
          <div class="img">
            <img src="../../../assets/imgs/txl.png" alt="" />
          </div>
          <div class="title">数计通讯录</div>
        </div>
        <div class="form-container">
          <div class="registerForm">
            <h1>学生注册</h1>
            <div
              v-for="(item, index) in [
                { key: 'studentId', label: '学号', icon: 'icon-yonghu' },
                { key: 'name', label: '姓名', icon: 'icon-zuoxixingming' },
                { key: 'phoneNumber', label: '手机号码', icon: 'icon-dianhua' },
                { key: 'email', label: '邮箱', icon: 'icon-youxiang' },
              ]"
              :key="index"
              :class="item.key"
            >
              <input
                :class="`${item.key}Input`"
                :id="item.key"
                type="text"
                :v-model="item.key"
                @focus="handleFocus(item.key)"
              />
              <label
                :for="item.key"
                :class="{ 'label-up': hasFocus[item.key] }"
                >{{ item.label }}</label
              >
              <div class="icon">
                <i class="iconfont" :class="`${item.icon}`"></i>
              </div>
            </div>

            <div class="password">
              <input
                class="passwordInput"
                id="password"
                :type="isPasswordVisible ? 'text' : 'password'"
                v-model="password"
                @focus="handleFocus('password')"
              />
              <label
                for="password"
                :class="{ 'label-up': hasFocus['password'] }"
                >通讯录密码</label
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
            <div class="registerButtonContainer">
              <button class="registerButton">注册</button>
            </div>
            <div class="toLogin">
              已有账号？<router-link to="/stu/login">去登录></router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped lang="scss">
.container {
  display: flex;
  flex-direction: column;
  background: url(../../../assets/imgs/n_b2.jpg);
  .logo-container {
    width: 100%;
    height: 96px;
    .logo-img-container {
      width: 580px;
      height: 96px;
      .logo-img {
        width: 100%;
        height: 100%;
      }
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
      transform: translateY(-60px);
      .background-container {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        width: 500px;
        height: 700px;
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
        @media screen and (max-width: 1040px) {
          display: none;
        }
      }
      .form-container {
        width: 500px;
        height: 700px;
      }
    }
  }
}
.registerForm {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 60px 50px;
  width: 500px;
  height: 700px;
  background: #fff;
  h1 {
    color: $mainColor;
    margin-bottom: 20px;
  }
  .studentId,
  .name,
  .phoneNumber,
  .email,
  .password,
  .toRigister {
    display: flex;
    position: relative;
  }
  .studentId,
  .name,
  .phoneNumber,
  .email,
  .password {
    transition: 0.3s;
    border-bottom: 1px solid #000;
    //   下框线变蓝
    &:focus-within {
      border-bottom: 1px solid $mainColor;
    }
  }
  label {
    position: absolute;
    top: 50%;
    left: 0;
    font-size: 16px;
    transform: translateY(-50%);
    transition: 0.3s;
    // 标签上移
    &.label-up {
      transform: translateY(-180%);
    }
  }
  .studentIdInput,
  .nameInput,
  .phoneNumberInput,
  .emailInput,
  .passwordInput {
    width: 360px;
    height: 40px;
    font-size: 16px;
    background-color: transparent;
    border: none;
    outline: none;
    //   标签变蓝
    &:focus + label {
      color: $mainColor;
    }
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
  .toLogin {
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
  .registerButtonContainer {
    display: flex;
    justify-content: center;
    .registerButton {
      width: 100%;
      height: 40px;
      color: #fff;
      background-color: $mainColor;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      &:hover {
        background-color: #326bcc;
      }
    }
  }
}
</style>
