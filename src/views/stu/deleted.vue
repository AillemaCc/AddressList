<script setup>
import { stuGetDeletedApi } from '@/apis/stu/request'
import { handleStatus } from '@/utils/handleStatus'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useStuInfoStore } from '@/stores/stuInfo'

const deleted = ref([
  {
    sender: '9109222258',
    senderName: '谢融悠',
    receiver: '9109222256',
    receiverName: '黎明',
    content: '111',
    status: 0,
    //验证状态 0待审核 1通过 2拒绝
  },
  {
    sender: '9109222258',
    senderName: '谢融悠',
    receiver: '9109222257',
    receiverName: '小王',
    content: '121',
    status: 1,
  },
  {
    sender: '9109222258',
    senderName: '谢融悠',
    receiver: '9109232259',
    receiverName: '小明',
    content: '113',
    status: 2,
  },
])
const stuInfoStore = useStuInfoStore()
const studentId = stuInfoStore.studentId
const current = ref(1)
const total = ref(0)
const pages = ref(0)

async function getDeleted() {
  const res = await stuGetDeletedApi({
    studentId: studentId.value,
  })
  if (res.success) {
    deleted.value = res.data.records
    current.value = res.data.current
    total.value = res.data.total
    pages.value = res.data.pages
  } else {
    ElMessage.error(res.message)
  }
}
getDeleted()

const handled_request = handleStatus(deleted.value)

const getStatusStyle = (status) => {
  const colorMap = {
    待通过: '#275dbb',
    已通过: '#67c23a',
    已拒绝: '#f56c6c',
  }
  return { color: colorMap[status] || '#606266' }
}
</script>
<template>
  <div class="container">
    <div class="title">已删除的请求（{{ total }}条）</div>
    <div class="data-exist" v-if="handled_request.length > 0">
      <el-table :data="handled_request" style="width: 100%">
        <el-table-column prop="receiver" label="发送对象学号" width="250" />
        <el-table-column prop="receiverName" label="发送对象姓名" width="250" />
        <el-table-column prop="content" label="发送内容" width="400" />
        <el-table-column label="请求状态" width="250">
          <template #default="{ row }">
            <span :style="getStatusStyle(row.status)">{{ row.status }}</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="example-pagination-block">
        <!-- total除以10，向上取整就是最大页数，total可以表示为请求总条数，配合size使用 -->
        <el-pagination layout="prev, pager, next" :total="total" />
      </div>
    </div>
    <el-empty class="data-non-exist" description="暂时没有数据" v-else />
  </div>
</template>

<style scoped lang="scss">
.container {
  padding: 0 40px;
  .title {
    padding-bottom: 10px;
    margin-bottom: 10px;
    font-size: 20px;
    color: $mainColor;
    background-color: #fff;
    border-bottom: 1px solid #a1a1a1;
    cursor: default;
  }
  :deep(.el-table thead th) {
    font-family: 'micro yahei';
    font-weight: 400;
  }
  .data-exist {
    margin: 0 auto;
  }
  .example-pagination-block {
    display: flex;
    justify-content: center;
    margin-top: 10px;
    :deep(.btn-prev),
    :deep(.number),
    :deep(.btn-next) {
      background-color: transparent;
    }
  }
}
</style>
