<script setup>
import { ref, onUnmounted } from 'vue'
import { marked } from 'marked'
import { useRoute, useRouter } from 'vue-router'
import {
  adminAddBulletinApi,
  adminQueryBulletinApi,
  adminUpdateBulletinApi,
} from '@/apis/admin/bulletin'
const route = useRoute()
const router = useRouter()
function routerBack() {
  router.back()
}
const bulletinTitle = ref('')
const bulletinCategory = ref('')
const bulletinStatus = ref('0')
const bulletinPriority = ref('')
const bulletinCoverImgUrl = ref('')
const options = [
  {
    value: '0',
    label: '草稿',
  },
  {
    value: '1',
    label: '已发布',
  },
  {
    value: '2',
    label: '已下架',
  },
]

const rawText = ref('')

//如果有id回显公告数据
if (route.query.id) {
  ;(async () => {
    const res = await adminQueryBulletinApi()
    if (res.success) {
      bulletinTitle.value = res.data.title
      bulletinCategory.value = res.data.category
      bulletinStatus.value = res.data.status
      bulletinPriority.value = res.data.priority
      bulletinCoverImgUrl.value = res.data.coverImage
      rawText.value = res.data.content
    } else {
      ElMessage.error(res.message)
    }
  })()
}

//发布公告
async function release() {
  if (route.query.id) {
    //编辑公告
    const res = await adminUpdateBulletinApi({
      title: bulletinTitle.value,
      boardId: +route.query.id,
      category: bulletinCategory.value,
      content: rawText.value,
      status: +bulletinStatus.value,
      priority: bulletinPriority.value,
      coverImage: bulletinCoverImgUrl.value,
    })
    if (res.success) {
      ElMessage.success(res.message)
      setTimeout(() => {
        router.back()
      }, 1000)
    } else {
      ElMessage.error(res.message)
    }
  } else {
    //新增公告
    const res = await adminAddBulletinApi({
      title: bulletinTitle.value,
      category: bulletinCategory.value,
      content: rawText.value,
      status: +bulletinStatus.value,
      priority: bulletinPriority.value,
      coverImage: bulletinCoverImgUrl.value,
    })
    if (res.success) {
      ElMessage.success(res.message)
      setTimeout(() => {
        router.back()
      }, 1000)
    } else {
      ElMessage.error(res.message)
    }
  }
}

// 防抖延时器
let debounceTimer = null
const compiledMarkdown = ref('')

const debouncedParseMarkdown = () => {
  if (debounceTimer) {
    clearTimeout(debounceTimer)
  }
  debounceTimer = setTimeout(() => {
    compiledMarkdown.value = marked.parse(rawText.value)
  }, 500)
}

const parseMarkdown = () => {
  debouncedParseMarkdown()
}
onUnmounted(() => {
  if (debounceTimer) {
    clearTimeout(debounceTimer)
  }
})
</script>
<template>
  <div class="container">
    <div class="header-container">
      <div class="back-button">
        <el-button @click="routerBack"
          ><i class="iconfont icon-zuojiantou"></i
        ></el-button>
      </div>
      <div class="header">编辑公告</div>
      <div class="release-button">
        <el-button @click="release">发布</el-button>
      </div>
    </div>
    <div class="main-container">
      <div class="detail-container">
        <div class="desc-container">
          <div class="desc">
            <label for="title">标题</label>
            <div class="input-container">
              <el-input
                id="title"
                v-model="bulletinTitle"
                size="large"
                placeholder="请输入公告标题"
              />
            </div>
          </div>
          <div class="desc">
            <label for="category">分类</label>
            <div class="input-container">
              <el-input
                id="category"
                v-model="bulletinCategory"
                size="large"
                placeholder="请输入公告分类"
              />
            </div>
          </div>
          <div class="desc">
            <label for="status">状态</label>
            <div class="input-container">
              <el-select
                v-model="bulletinStatus"
                placeholder="请选择公告状态"
                size="large"
                id="status"
              >
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </div>
          </div>
          <div class="desc">
            <label for="priority">优先级</label>
            <div class="input-container">
              <el-input
                id="priority"
                v-model="bulletinPriority"
                size="large"
                placeholder="请输入公告优先级"
              />
            </div>
          </div>
          <div class="desc">
            <label for="coverImg">图片链接</label>
            <div class="input-container">
              <el-input
                id="coverImg"
                v-model="bulletinCoverImgUrl"
                size="large"
                placeholder="请输入公告封面外链地址"
              />
            </div>
          </div>
        </div>
      </div>

      <div class="content-container">
        <div class="input-section">
          <textarea
            v-model="rawText"
            placeholder="在这里输入公告内容(markdown格式)"
            @input="parseMarkdown"
          ></textarea>
        </div>
        <div class="preview-section">
          <h3>公告预览</h3>
          <div class="markdown-preview" v-html="compiledMarkdown"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.container {
  min-width: 600px;
  display: flex;
  flex-direction: column;
  .header-container {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 50px;
    border-bottom: 1px solid #999;
    .back-button {
      position: absolute;
      left: 0;
    }
    .release-button {
      position: absolute;
      right: 0;
    }
    .header {
      font-size: 20px;
      color: $mainColor;
      cursor: default;
    }
  }
  .main-container {
    flex: 1;
  }
}
.detail-container {
  display: flex;
  gap: 20px;
  .desc-container {
    flex: 4;
  }
  .img-container {
    flex: 1;
  }
}
.desc-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  margin: 20px 30px;
  gap: 20px;
  .desc {
    display: flex;
    justify-content: center;
    align-items: center;
    label {
      width: 20%;
      min-width: 100px;
      display: flex;
      justify-content: center;
      align-items: center;
    }
    .input-container {
      width: 60%;
    }
  }
}
.img-container {
  background-color: skyblue;
}

.content-container {
  display: grid;
  grid-template-columns: 1fr 1fr; // 左右等宽
  margin: 20px 30px;
  min-height: 500px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.08);
  background: #fff;
  @media (max-width: 1200px) {
    grid-template-columns: 1fr;
  }

  .input-section,
  .preview-section {
    padding: 16px;
  }

  .input-section {
    border-right: 1px solid #ebeef5;
    background: #fafafa;

    textarea {
      width: 100%;
      height: 100%;
      min-height: 600px;
      padding: 12px;
      border: 1px solid #dcdfe6;
      border-radius: 4px;
      resize: none;
      font-family: 'Segoe UI';
      font-size: 14px;
      line-height: 1.6;
      color: #333;
      background: #fff;
      transition: border-color 0.3s;

      &:focus {
        border-color: #409eff;
        outline: none;
      }

      &::placeholder {
        color: #c0c4cc;
      }
    }
  }

  .preview-section {
    h3 {
      margin: 0 0 12px 0;
      padding-bottom: 8px;
      border-bottom: 1px solid #ebeef5;
      color: #275dbb;
      font-size: 16px;
    }

    .markdown-preview {
      padding: 0 20px;
      // overflow-y: auto;
      font-family:
        -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue',
        Arial, sans-serif;
      line-height: 1.6;
      color: #333;
    }
  }
}
</style>
