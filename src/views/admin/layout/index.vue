<script setup>
import { ref } from 'vue'
import { useAdminInfoStore } from '@/stores/adminInfo'
import { removeAdministrationInfo } from '@/utils/storage'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogoutApi } from '@/apis/admin/login'
import { adminGetHomeInfoApi } from '@/apis/admin/other'
const router = useRouter()
const adminInfoStore = useAdminInfoStore()

const failRequestCount = ref(20)
const userName = ref('')
const username = adminInfoStore.adminInfo.username
const accessToken = adminInfoStore.adminInfo.accessToken
const refreshToken = adminInfoStore.adminInfo.refreshToken
async function loginout() {
  const res = await adminLogoutApi({
    username: username,
    accessToken: accessToken,
    refreshToken: refreshToken,
  })
  if (res.success) {
    adminInfoStore.setAdminInfo({})
    removeAdministrationInfo()
    router.push('/admin/login')
    ElMessage.success(res.message)
  } else {
    ElMessage.error(res.message)
  }
}

adminGetHomeInfoApi({username}).then((res) => {
  if (res.success) {
    failRequestCount.value = res.data.request.pending
    userName.value = res.data.username
  }
})
</script>

<template>
  <div class="all-container">
    <div class="header-container">
      <div class="logo-container">
        <img src="../../../assets/imgs/txl.png" alt="" />
      </div>
      <div class="text-container">网上通讯录</div>

      <div class="icon-container">
        <el-tooltip effect="light" :content="`${failRequestCount}条待审核请求`">
          <router-link to="/admin/request_fail"
            ><i class="iconfont icon-youxiang"></i
          ></router-link>
        </el-tooltip>

        <el-tooltip effect="light" content="信息查询">
          <router-link to="/admin/query"
            ><i class="iconfont icon-sousuo"></i
          ></router-link>
        </el-tooltip>
        <el-tooltip effect="light" content="退出登录">
          <i class="iconfont icon-tuichudenglu" @click="loginout"></i>
        </el-tooltip>
      </div>
      <div class="welcome-container">
        <i class="iconfont icon-yonghu"></i> <span>{{ userName }}</span>
      </div>
    </div>

    <div class="main-container">
      <div class="menu-container">
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical-demo"
          router
        >
          <el-menu-item index="/admin/home">
            <span>首页</span>
          </el-menu-item>
          <el-sub-menu index="2">
            <template #title>
              <span>请求概览</span>
            </template>
            <el-menu-item index="/admin/request_fail">待审核</el-menu-item>
            <el-menu-item index="/admin/request_success">已通过</el-menu-item>
            <el-menu-item index="/admin/request_reject">已拒绝</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin/query">
            <span>信息查询</span>
          </el-menu-item>
          <el-sub-menu index="4">
            <template #title>
              <span>公告系统</span>
            </template>
            <el-menu-item index="/admin/bulletin_draft">草稿</el-menu-item>
            <el-menu-item index="/admin/bulletin_released">已发布</el-menu-item>
            <el-menu-item index="/admin/bulletin_pulledOff"
              >已下架</el-menu-item
            >
            <el-menu-item index="/admin/bulletin_deleted">已删除</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>
      <div class="router-container">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.all-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  .header-container {
    display: flex;
    justify-content: start;
    align-items: center;
    padding-left: 60px;
    height: 96px;
    min-height: 96px;
    .logo-container {
      width: 80px;
      height: 80px;
      img {
        width: 100%;
        height: 100%;
      }
    }
    .text-container {
      font-size: 24px;
      font-weight: 600;
      color: $mainColor;
    }
    .icon-container {
      margin-left: auto;
      display: flex;
      gap: 15px;
      .iconfont {
        font-size: 20px;
        color: #666;
        cursor: pointer;
        border-radius: 4px;
        &:hover {
          color: #000;
          background-color: #eee;
        }
      }
    }
    .welcome-container {
      margin: 0 60px 0 20px;
      cursor: default;
      i {
        display: inline-block;
        transform: translateY(-1px);
      }
    }
    .login-out-container {
      margin-right: 40px;
    }
  }
  .main-container {
    display: flex;
    flex: 1;
    .menu-container {
      min-width: 240px;
      width: 240px;
      background: linear-gradient(#fff 0, #fff 50%, #e3effb 100%);
      border-right: 1px solid #dcdfe6;
      @media screen and (max-width: 720px) {
        width: 180px;
      }
      :deep(.el-menu) {
        border-right: none;
      }
    }
    .router-container {
      padding: 0 40px;
      flex: 1;
    }
  }
}
</style>
