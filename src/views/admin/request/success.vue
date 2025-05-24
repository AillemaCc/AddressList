<script setup>
import { adminDisplaySuccessRequestApi } from '@/apis/admin/request'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
//获取待审核请求
const success_request = ref([])
const currentPage = ref(1)
const total = ref(0)
const pages = ref(0)
async function getSuccessRequest(num) {
  const res = await adminDisplaySuccessRequestApi({ current: num })
  if (res.success) {
    success_request.value = res.data.records
    currentPage.value = res.data.current
    total.value = res.data.total
    pages.value = res.data.pages
  } else {
    ElMessage.error(res.message)
  }
}
getSuccessRequest(1)

const handleCurrentChange = (val) => {
  currentPage.value = val
  getSuccessRequest(val)
}
</script>

<template>
  <div class="container">
    <div class="title">已通过请求（{{ total }}条）</div>
    <div class="data-exist" v-if="success_request.length > 0">
      <el-table :data="success_request" style="width: 100%">
        <el-table-column prop="id" label="学生ID" width="80" />
        <el-table-column prop="studentId" label="学号" width="240" />
        <el-table-column prop="name" label="姓名" width="200" />
        <el-table-column prop="phone" label="电话号码" width="180" />
        <el-table-column prop="email" label="邮箱" width="300" />
        <el-table-column prop="password" label="密码" width="300" />
        <el-table-column prop="status" label="状态" width="90" />
        <el-table-column prop="registerToken" label="注册凭证" width="300" />
      </el-table>

      <div class="example-pagination-block">
        <!-- total除以10，向上取整就是最大页数，total可以表示为请求总条数，配合size使用 -->
        <el-pagination
          layout="prev, pager, next"
          :total="total"
          :current-page="currentPage"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    <el-empty class="data-non-exist" description="暂时没有请求" v-else />
  </div>
</template>

<style scoped lang="scss">
.container {
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
    :deep(.el-table__header-wrapper),
    :deep(.el-scrollbar__wrap) {
      display: flex;
      justify-content: center;
    }
  }
  .example-pagination-block {
    display: flex;
    justify-content: center;
    margin-top: 10px;
  }
}
</style>
