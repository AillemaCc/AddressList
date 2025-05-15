<script setup></script>

<template>
  <div class="container">
    <div class="header-container">
      <div class="logo-container">
        <img src="../../../assets/imgs/txl.png" alt="" />
      </div>
      <div class="text-container">数计通讯录</div>
      <div class="welcome-container">
        <i class="iconfont icon-yonghu"></i> <span>系统管理员1</span>
      </div>
      <div class="login-out-container">退出登录</div>
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
            <el-menu-item index="/admin/fail">待审核</el-menu-item>
            <el-menu-item index="/admin/success">已通过</el-menu-item>
            <el-menu-item index="/admin/reject">已拒绝</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin/query">
            <span>信息查询</span>
          </el-menu-item>

          <el-menu-item index="/admin/bulletin">
            <span>公告系统</span>
          </el-menu-item>
        </el-menu>
      </div>
      <div class="router-container">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  .header-container {
    display: flex;
    justify-content: start;
    align-items: center;
    padding-left: 60px;
    height: 96px;
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
    .welcome-container {
      margin-left: auto;
      margin-right: 20px;
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
      min-width: 180px;
      width: 220px;
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
      padding-left: 40px;
      flex: 1;
    }
  }
}
</style>
