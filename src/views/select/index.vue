<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
function goToStudent() {
  router.push('/stu')
}
function goToAdministrator() {
  router.push('/admin')
}
</script>

<template>
  <div class="header">
    <div class="academic-logo-container">
      <img
        class="academic-logo"
        src="../../assets/imgs/academic_logo.png"
        alt=""
      />
    </div>
  </div>
  <div class="main-container">
    <h1 class="title">网上通讯录系统</h1>
  </div>
  <div class="subtitle">请选择您的登录入口</div>
  <div class="port-card">
    <div class="stu" @click="goToStudent">
      <div class="card-icon student">
        <i class="iconfont icon-xuesheng"></i>
      </div>
      <div class="card-name">学生端</div>
      <div class="card-desc">注册审核后，可完善并查询同学通讯信息</div>
    </div>
    <div class="admin" @click="goToAdministrator">
      <div class="card-icon administrator">
        <i class="iconfont icon-guanliyuan_jiaoseguanli"></i>
      </div>
      <div class="card-name">管理员端</div>
      <div class="card-desc">审核账户、管理专业信息，维护系统运行</div>
    </div>
  </div>
  <div class="detail">2025 javaweb期末作业 | 第六组</div>
</template>

<style scoped lang="scss">
.header {
  display: flex;
  align-items: center;
  width: 100%;
  height: 150px;
  .academic-logo-container {
    width: 580px;
    height: 96px;
    .academic-logo {
      width: 100%;
      height: 100%;
    }
  }
}
.main-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px auto;
  .title {
    font-size: 40px;
    color: $mainColor;
  }
}
.subtitle {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 10px auto;
  font-size: 18px;
  color: #666;
}
.port-card {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 50px;
  margin-top: 50px;
  .stu,
  .admin {
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    padding: 40px;
    width: 280px;
    height: 280px;
    border-radius: 10px;
    box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.1);
    transition: 0.3s;
    cursor: pointer;
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
    }
    &:hover .iconfont,
    &:hover .card-name {
      color: $mainColor;
    }
    .card-icon {
      width: 60px;
      height: 60px;
      .iconfont {
        font-size: 60px;
      }
    }
    .card-name {
      margin: 20px 0;
      font-weight: 600;
      font-size: 24px;
    }
    .card-desc {
      color: #666;
      text-align-last: center;
    }
  }
  @media screen and (max-width: 720px) {
    flex-direction: column;
  }
}

.detail {
  display: flex;
  justify-content: center;
  margin-top: 100px;
  color: #777;
  font-size: 16px;
}
</style>
