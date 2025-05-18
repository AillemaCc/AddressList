<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
const status = route.query.status
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
      <div class="wait main-box" v-if="status === '0'">
        <i class="iconfont icon-dengdai"></i>
        <h2>请等待管理员审核后再次访问</h2>
      </div>
      <div class="reject main-box" v-if="status === '2'">
        <i class="iconfont icon-jujue"></i>
        <h2>您的账号已被管理员拒绝访问</h2>
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
    .main-box {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      gap: 20px;
      .iconfont {
        font-size: 60px;
      }
      .icon-dengdai {
        color: $mainColor;
        & + h2 {
          color: $mainColor;
        }
      }
      .icon-jujue {
        color: #f89898;
        & + h2 {
          color: #f89898;
        }
      }
    }
  }
}
</style>
