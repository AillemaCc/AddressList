<script setup>
import { ref } from 'vue'
import no_img from '../../../assets/imgs/no_img.png'
import { ElMessage } from 'element-plus'
import {
  adminDeleteBulletinApi,
  adminPulloffReleasedApi,
  adminDisplayReleasedApi,
} from '@/apis/admin/bulletin'

const announcements = ref([])
const current = ref(1)
const total = ref(0)
const pages = ref(0)
async function getReleased(num) {
  const res = await adminDisplayReleasedApi({ current: num, size: 10 })
  if (res.success) {
    announcements.value = res.data.records
    current.value = res.data.current
    total.value = res.data.total
    pages.value = res.data.pages
  } else {
    ElMessage.error(res.message)
  }
}
getReleased(1)
const changePage = (val) => {
  current.value = val
  getReleased(val)
}
// 状态文本映射
const statusText = {
  0: '草稿',
  1: '已发布',
  2: '已下架',
}

// 下架已发布公告
const pulloffRelease = async (boardId) => {
  const res = await adminPulloffReleasedApi({ boardId })
  if (res.success) {
    const index = announcements.value.findIndex(
      (item) => item.boardId === boardId,
    )
    announcements.value.splice(index, 1)

    if (announcements.value.length === 0) {
      if (current.value === 1) {
        if (pages.value !== 1) {
          getReleased(1)
        }
      } else {
        current.value--
        getReleased(current.value)
      }
    }
    ElMessage.success(res.message)
  } else {
    ElMessage.error(res.message)
  }
}
//删除公告
const deleteBulletin = async (boardId) => {
  const res = await adminDeleteBulletinApi({ boardId })
  if (res.success) {
    const index = announcements.value.findIndex(
      (item) => item.boardId === boardId,
    )
    announcements.value.splice(index, 1)
    if (announcements.value.length === 0) {
      if (current.value === 1) {
        if (pages.value !== 1) {
          getReleased(1)
        }
      } else {
        current.value--
        getReleased(current.value)
      }
    }
    ElMessage.success(res.message)
  } else {
    ElMessage.error(res.message)
  }
}
</script>

<template>
  <div class="announcement-board">
    <!-- 进度条 -->
    <el-steps
      class="process-container"
      :space="200"
      direction="vertical"
      :active="1"
      finish-status="success"
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
        <div class="header-title">已发布公告（{{ total }}条）</div>
        <router-link to="/admin/bulletin_edit"
          ><button class="add-button">
            <span>新增公告</span>
          </button></router-link
        >
      </div>
      <div class="content-container">
        <div class="data-exist" v-if="announcements.length > 0">
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
              <div class="item">
                <div class="item-desc">创建时间</div>
                ：
                <div class="item-body">{{ item.createTime }}</div>
              </div>
              <div class="item">
                <div class="item-desc">最近更新时间</div>
                ：
                <div class="item-body">{{ item.updateTime }}</div>
              </div>
            </div>

            <div class="item-img">
              <div class="cover-image">
                <img :src="item.coverImage || no_img" alt="公告封面" />
              </div>
            </div>

            <div class="item-operation">
              <router-link :to="`/admin/bulletin_edit?id=${item.boardId}`"
                ><button class="edit">编辑</button></router-link
              >
              <button class="release" @click="pulloffRelease(item.boardId)">
                下架
              </button>
              <button class="delete" @click="deleteBulletin(item.boardId)">
                删除
              </button>
            </div>
          </div>
          <div class="example-pagination-block">
            <!-- total除以10，向上取整就是最大页数，total可以表示为请求总条数，配合size使用 -->
            <el-pagination
              layout="prev, pager, next"
              :total="total"
              v-model:current-page="current"
              @current-change="changePage"
            />
          </div>
        </div>
        <el-empty
          class="data-non-exist"
          description="暂时没有该类公告"
          v-else
        />
      </div>
    </div>
  </div>
</template>

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
  .add-button {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    background-color: #409eff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s ease;
    &:hover {
      background-color: #66b1ff;
    }
  }
}
.content-container {
  display: flex;
  flex-direction: column;
  .board-item {
    display: flex;
    width: 100%;
    height: 150px;
    margin: 20px 0;
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
    .item {
      display: flex;
      align-items: center;
      margin: 4px 0;
      font-size: 14px;
      color: #666;

      .item-desc {
        width: 100px;
        text-align: right;
        color: #999;
        font-weight: 400;
        margin-right: 8px;
      }

      .item-body {
        flex: 1;
        color: #333;
        font-weight: 500;
        margin-left: 8px;
        word-break: break-all;
      }

      &:before {
        content: '';
        display: inline-block;
        width: 4px;
        height: 4px;
        background-color: #ddd;
        border-radius: 50%;
        margin-right: 8px;
      }
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
.example-pagination-block {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}
</style>
