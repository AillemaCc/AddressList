<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { marked } from 'marked'
import {
  adminQueryBulletinApi,
  adminDisplayReleasedApi,
} from '@/apis/admin/bulletin'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const bulletinList = ref([])
const board = ref({ content: '' })
const current = ref(1)
const pages = ref(0)
const compiledMarkdown = computed(() => marked.parse(board.value.content))
function Back() {
  router.back()
}
async function getList(num) {
  const res = await adminDisplayReleasedApi({ current: num })
  console.log(res)

  if (res.success) {
    bulletinList.value = res.data.records
    current.value = res.data.current
    pages.value = res.data.pages
  } else {
    ElMessage.error(res.message)
  }
}
async function getDetail(id) {
  const res = await adminQueryBulletinApi({ id })
  if (res.success) {
    board.value = res.data
  } else {
    ElMessage.error(res.message)
  }
}
function lastpage() {
  if (current.value > 1) {
    current.value--
    getList(current.value)
  }
}
function nextpage() {
  if (current.value < pages.value) {
    current.value++
    getList(current.value)
  }
}

watch(
  () => route.query.id,
  (newValue) => {
    getDetail(newValue)
  },
)

getList(1)
getDetail(route.query.id)
</script>

<template>
  <div class="bgc">
    <img :src="board.coverImage" alt="" />
  </div>
  <div class="container">
    <div class="left-list">
      <div class="item-header">
        <el-button @click="Back">返回</el-button>
        <!-- <div class="page-container">当前第1页，共2页</div>  -->
      </div>
      <div class="item-list">
        <div
          class="item"
          v-for="item in bulletinList"
          :key="item.boardId"
          @click="router.replace(`/stu/bulletin/detail?id=${item.boardId}`)"
        >
          <div class="item-title">{{ item.title }}</div>
          <div class="item-create-time">{{ item.createTime.slice(0, 10) }}</div>
        </div>
        <div class="page-container">
          <button @click="lastpage">-</button>
          <div>{{ current }}</div>
          <button @click="nextpage">+</button>
        </div>
      </div>
    </div>
    <div class="right-detail">
      <div class="content-container">
        <div class="title">{{ board.title }}</div>
        <div class="create-time">发布时间:{{ board.createTime }}</div>
        <div class="content">
          <div class="markdown-preview" v-html="compiledMarkdown"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.bgc {
  position: fixed;
  width: 100vw;
  height: 100vh;
  //   background-color: skyblue;
  z-index: -1;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center;
  }
}
.container {
  display: flex;
  width: 80vw;
  margin: 0 auto;
  //   background-color: #f8f8f8;
  .left-list {
    flex: 1;
    min-width: 200px;
    min-height: 100vh;
  }
  .right-detail {
    flex: 7;
  }
}
.left-list {
  padding: 60px 20px;
  .item-header {
    margin-bottom: 20px;
  }

  .item-list {
    display: flex;
    flex-direction: column;
    gap: 15px;

    .item {
      padding: 16px 20px;
      background-color: white;
      transition: all 0.3s ease;
      cursor: pointer;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
      }

      .item-title {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 6px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .item-create-time {
        font-size: 12px;
        color: #999;
      }
    }
    .page-container {
      display: flex;
      justify-content: center;
      align-items: center;
      button {
        width: 24px;
        height: 24px;
        font-size: 16px;
        border: none;
        outline: none;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: #fff;
        cursor: pointer;
        &:hover {
          color: $mainColor;
        }
      }
      div {
        width: 24px;
        height: 24px;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: #fff;
      }
    }
  }
}
.right-detail {
  padding: 60px;
  min-width: 666px;
  .content-container {
    padding: 30px;
    width: 100%;
    min-height: 100%;
    background-color: #fff;
    box-shadow: 0 0 4px rgba(0, 0, 0, 0.1);
    .title {
      display: flex;
      justify-content: center;
      margin: 10px;
      font-size: 28px;
      font-weight: 600;
    }
    .create-time {
      display: flex;
      justify-content: center;
      margin-bottom: 60px;
      color: #666;
    }
    .content {
      padding: 0 60px;
    }
  }
}
</style>
