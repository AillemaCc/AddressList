<script setup>
import { stuLogoutApi } from '@/apis/stu/login'
import { stuHomePageInfoApi } from '@/apis/stu/other'
import { useStuInfoStore } from '@/stores/stuInfo'
import { removeStudentInfo } from '@/utils/storage'
import {useRouter} from 'vue-router'

    const router = useRouter()
import { ref } from 'vue'
const stuName = ref('')

const stuInfoStore = useStuInfoStore()
async function logout() {
  const res = await stuLogoutApi({
    studentId: stuInfoStore.stuInfo.studentId,
    token: stuInfoStore.stuInfo.accessToken,
    refreshToken: stuInfoStore.stuInfo.refreshToken,
  })
  if (res.success) {
    stuInfoStore.setStuInfo({})
    removeStudentInfo()
    router.push('/')
    ElMessage({
      message: res.message,
      type: 'success',
      duration: 2000,
    })
  } else {
    ElMessage.error(res.message)
  }
}

stuHomePageInfoApi({
  studentId: stuInfoStore.stuInfo.studentId,
}).then((res) => {
  stuName.value = res.data.stuInfo.name
})
</script>

<template>
  <div class="all-container">
    <div class="header-container">
      <div class="title-container">
        <img src="../../../assets/imgs/txl.png" alt="" />
        网上通讯录
      </div>
      <div class="menu-container">
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical-demo"
          router
          mode="horizontal"
        >
          <el-menu-item index="/stu/home">
            <span>个人主页</span>
          </el-menu-item>
          <el-sub-menu index="2">
            <template #title>
              <span>通讯录好友</span>
            </template>
            <el-menu-item index="/stu/friends">
              <span>我的好友</span>
            </el-menu-item>
            <el-menu-item index="/stu/friends_deleted">
              <span>已删除好友</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="3">
            <template #title>
              <span>通讯录请求</span>
            </template>
            <el-menu-item index="/stu/enter">
              <span>已发送</span>
            </el-menu-item>
            <el-menu-item index="/stu/fail">
              <span>待回复</span>
            </el-menu-item>
            <el-menu-item index="/stu/success">
              <span>已通过</span>
            </el-menu-item>
            <el-menu-item index="/stu/reject">
              <span>已拒绝</span>
            </el-menu-item>
            <el-menu-item index="/stu/deleted">
              <span>已删除</span>
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/stu/query">
            <span>查询信息</span>
          </el-menu-item>
        </el-menu>
      </div>
      <div class="welcome-container">
        <div class="welcome-box">你好，{{ stuName }}</div>
        <div class="logout-box" @click="logout()">退出登录</div>
      </div>
    </div>
    <div class="main-container">
      <router-view></router-view>
    </div>
  </div>
</template>
<style scoped lang="scss">
.all-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-image: url(../../../assets/imgs/n_b2.jpg);
  background-repeat: repeat;
  .header-container {
    position: fixed;
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    height: 96px;
    min-height: 96px;
    background-color: #fff;
    z-index: 999;
    .title-container {
      min-width: 200px;
      display: flex;
      justify-content: center;
      align-items: center;
      margin-left: 40px;
      font-size: 24px;
      font-weight: 600;
      color: $mainColor;
      cursor: default;
      img {
        width: 80px;
        height: 80px;
      }
    }
    .menu-container {
      margin-right: auto;
      margin-left: 40px;
      width: 540px;
      :deep(.el-menu) {
        width: 100%;
      }
    }
    .welcome-container {
      display: flex;
      justify-content: center;
      align-items: center;
      margin-right: 60px;
      height: 50px;
      min-width: 180px;
      gap: 20px;
      .logout-box {
        cursor: pointer;
        &:hover {
          color: $mainColor;
        }
      }
    }
  }
  .main-container {
    margin-top: 96px;
    flex: 1;
  }
}
</style>
