<script setup>
import { ref } from 'vue'

const fail_request = ref([
  {
    id: 27,
    studentId: '9646857155',
    name: '陈詩涵',
    phone: '2132594034',
    email: 'chenshih@gmail.com',
    status: 2,
    remark: null,
    registerToken: '5PaH5oD3sR',
  },
])
</script>

<template>
  <div class="container">
    <div class="title">已拒绝请求</div>
    <el-table
      :data="fail_request"
      style="width: 100%"
      v-if="fail_request.length > 0"
    >
      <el-table-column prop="id" label="id" width="60" />
      <el-table-column prop="studentId" label="studentId" width="180" />
      <el-table-column prop="name" label="name" width="100" />
      <el-table-column prop="phone" label="phone" width="180" />
      <el-table-column prop="email" label="email" width="220" />
      <el-table-column prop="password" label="password" width="220" />
      <el-table-column prop="status" label="status" width="90" />
      <el-table-column prop="remark" label="remark" width="90" />
      <el-table-column prop="registerToken" label="registerToken" width="220" />
    </el-table>
    <div class="example-pagination-block">
      <!-- total除以10，向上取整就是最大页数，total可以表示为请求总条数，配合size使用 -->
      <el-pagination layout="prev, pager, next" :total="150" />
    </div>
  </div>
</template>

<style scoped lang="scss">
.container {
  .title {
    color: $mainColor;
    background-color: #fff;
    cursor: default;
  }
  :deep(.el-table thead th) {
    font-family: 'micro yahei';
    font-weight: 400;
  }
  .example-pagination-block {
    display: flex;
    justify-content: center;
    margin-top: 10px;
    transform: translateX(-100px);
  }
}
</style>
