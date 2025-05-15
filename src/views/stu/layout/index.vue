<script setup lang="ts">
// import { ref } from 'vue'
interface Tree {
  name: string
  router: string
  children?: Tree[]
}
const navData: Tree[] = [
  {
    name: '个人信息',
    router: '/stu/home',
  },
  {
    name: '我的通讯录',
    router: '/stu/friends',
    children: [
      {
        name: '通讯录好友',
        router: '/stu/friends',
      },
      {
        name: '通讯录请求',
        router: '/stu/enter',
        children: [
          {
            name: '已发送',
            router: '/stu/enter',
          },
          {
            name: '未通过',
            router: '/stu/fail',
          },
          {
            name: '已通过',
            router: '/stu/success',
          },
          {
            name: '已拒绝',
            router: '/stu/reject',
          },
          {
            name: '已删除',
            router: '/stu/deleted',
          },
        ],
      },
    ],
  },
  {
    name: '查询信息',
    router: '/stu/query',
  },
]

const defaultProps = {
  children: 'children',
  label: 'name',
}
</script>

<template>
  <div class="container">
    <div class="header-container">
      <div class="title-container">
        <img src="../../../assets/imgs/txl.png" alt="" />
        数计通讯录
      </div>
      <div class="welcome-container">
        <div class="welcome-box">你好，谢融悠</div>
        <div class="dropdown">
          <router-link to="/stu/home" class="option">个人资料</router-link>
          <router-link to="/stu/query" class="option">查询信息</router-link>
          <div class="option">退出登录</div>
        </div>
      </div>
    </div>
    <div class="main-container">
      <div class="left-nav">
        <el-tree :data="navData" :props="defaultProps">
          <template v-slot="{ data }">
            <router-link :to="data.router" class="nav-item"
              >{{ data.name }}
            </router-link>
          </template>
        </el-tree>
      </div>
      <div class="right-main">
        <router-view></router-view>
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
  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    height: 96px;
    .title-container {
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
    .welcome-container {
      position: relative;
      display: flex;
      justify-content: center;
      align-items: center;
      margin-right: 60px;
      height: 50px;
      &:hover .dropdown {
        opacity: 1;
        transform: scaleY(100%);
      }
      .welcome-box:hover {
        color: $mainColor;
        cursor: pointer;
      }

      .dropdown {
        position: absolute;
        top: 50px;
        left: 0;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        width: 100px;
        height: 120px;
        background-color: #fff;
        border: 1px solid rgba(0, 0, 0, 0.1);
        border-radius: 4px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        opacity: 0;
        transform: scaleY(0);
        transition:
          opacity 0.2s,
          transform 0.3s;
        transform-origin: top;
        z-index: 1;
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: 50%;
          width: 0;
          height: 0;
          border-top: 10px solid transparent;
          border-left: 10px solid transparent;
          border-right: 10px solid transparent;
          border-bottom: 10px solid #fff;
          /*用 filter 添加三角形阴影*/
          filter: drop-shadow(0 -7px 5px rgba(0, 0, 0, 0.1));
          transform: translateX(-50%) translateY(-100%);
          z-index: 0;
        }
        .option {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 100%;
          height: 35px;
          color: #000;
          cursor: pointer;
          &:hover {
            background-color: #f5f7fa;
            color: $mainColor;
          }
        }
      }
    }
  }
  .main-container {
    flex: 1;
  }
}
.main-container {
  display: flex;
  .left-nav {
    min-width: 200px;
    width: 240px;
    background: -webkit-linear-gradient(#fff 0%, #fff 70%, #fdf4ef 100%);
    &::v-deep(.el-tree-node__content) {
      height: 60px;
    }
    .nav-item {
      display: flex;
      align-items: center;
      flex: 1;
      width: 100%;
      height: 100%;
      padding: 0 10px;
      font-size: 16px;
      color: #000;
      text-decoration: none;

      &:hover,
      &.router-link-active {
        color: $mainColor;
      }
    }
  }
  .right-main {
    flex: 1;
  }
}
</style>
