<template>
  <div class="announcement-board">
    <!-- 进度条 -->
    <el-steps
      class="process-container"
      :space="200"
      direction="vertical"
      :active="3"
      finish-status="finish"
    >
      <el-step title="新增公告" description="新增的公告默认为草稿" />
      <el-step
        title="正式发布"
        description="正式发布之后，公告状态在学生端变为可见状态"
      />
      <el-step
        title="下架公告"
        description="下架后，公告变为不可见，但可用再次上架"
      />
    </el-steps>
    <!-- 主要区域 -->
    <div class="main-container">
      <div class="header-container">
        <div class="header-title">已删除公告</div>
      </div>
      <div class="content-container">
        <div
          class="board-item"
          v-for="(item, index) in announcements"
          :key="index"
        >
          <div class="item-info">
            <!-- 公告头部信息 -->
            <div class="item-header">
              <span class="category">{{ item.category }}</span>
              <span class="priority">优先级: {{ item.priority }}</span>
              <span class="status" :class="`status-${item.status}`">{{
                statusText[item.status]
              }}</span>
            </div>

            <!-- 公告主体内容 -->
            <h2 class="item-title">{{ item.title }}</h2>
          </div>

          <div class="item-img">
            <div class="cover-image">
              <img :src="item.coverImage || no_img" alt="公告封面" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import no_img from '../../../assets/imgs/no_img.png'
import txlimg from '../../../assets/imgs/txl.png'

const announcements = ref([
  {
    title: '22222222222222',
    boardId: 0,
    category: '测试分类',
    content: '测试内容2',
    status: 2,
    priority: '0',
    coverImage: null,
  },
  {
    title: '111111111111111',
    boardId: 0,
    category: '测试分类5',
    content: '测试内容4',
    status: 1,
    priority: '0',
    coverImage: txlimg,
  },
  {
    title: '111111111111111',
    boardId: 0,
    category: '测试分类2',
    content: '测试内容1',
    status: 0,
    priority: '0',
    coverImage: txlimg,
  },
  {
    title: '111111111111111',
    boardId: 0,
    category: '测试分类2',
    content: '测试内容1',
    status: 2,
    priority: '0',
    coverImage: txlimg,
  },
])

// 状态文本映射
const statusText = {
  0: '草稿',
  1: '已发布',
  2: '已下架',
}

// 按优先级排序的公告列表
// const sortedAnnouncements = computed(() => {
//   return [...announcements].sort((a, b) => +b.priority - +a.priority)
// })
</script>

<style scoped lang="scss">
.announcement-board {
  display: flex;
  width: 90%;
  min-width: 800px;
  margin: 0 auto;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  .process-container {
    flex: 1;
    margin-top: 40px;
    @media screen and (max-width: 1440px) {
      display: none;
    }
  }
  .main-container {
    flex: 9;
    padding: 0 40px;
  }
}
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 48px;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eaeaea;
  .header-title {
    margin: 0;
    font-size: 20px;
    color: $mainColor;
  }
}
.content-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  .board-item {
    display: flex;
    width: 100%;
    height: 150px;
    padding: 14px;
    border-radius: 8px;
    background-color: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.2s ease;
    &:hover {
      transform: translateY(-2px);
    }
  }
  .item-info {
    flex: 1;
    .item-header {
      display: flex;
      gap: 12px;
      margin-bottom: 12px;
      font-size: 12px;
      color: #666;
    }

    .category {
      padding: 2px 8px;
      background-color: #f0f9eb;
      color: #67c23a;
      border-radius: 4px;
    }

    .priority {
      padding: 2px 8px;
      background-color: #ecf5ff;
      color: #409eff;
      border-radius: 4px;
    }

    .status {
      padding: 2px 8px;
      border-radius: 4px;
      &.status-0 {
        background-color: #fdf6ec;
        color: #e6a23c;
      }
      &.status-1 {
        background-color: #f0f9eb;
        color: #67c23a;
      }
      &.status-2 {
        background-color: #fef0f0;
        color: #f56c6c;
      }
    }

    .item-title {
      margin: 0 0 8px 0;
      font-size: 20px;
      font-weight: 500;
      color: #333;
    }
  }
  .item-img {
    width: 122px;
    height: 122px;
    .cover-image {
      width: 100%;
      height: 100%;
    }

    .cover-image img {
      width: 100%;
      border-radius: 4px;
      object-fit: cover;
    }
  }
  .item-operation {
    width: 100px;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
    button {
      width: 60px;
      height: 24px;
      font-size: 16px;
      border: none;
      outline: none;
      background-color: #fff;
      cursor: pointer;
      &.edit {
        color: $mainColor;
        &:hover {
          color: #739feb;
        }
      }
      &.release {
        color: #67c23a;
        &:hover {
          color: #afed90;
        }
      }
      &.delete {
        color: #f56c6c;
        &:hover {
          color: #f5b8b8;
        }
      }
    }
  }
}
</style>
