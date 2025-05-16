<script setup>
import { ref } from 'vue'

const fail_request = ref([
  {
    id: 46,
    studentId: '9109222268',
    name: '学生1',
    phone: '15682496592',
    email: 'vacsbt_om0@qq.com',
    status: 1,
    remark: '111',
    registerToken: '2c4b7820-3a1f-4b76-9b0d-795358fa8209',
  },
])
</script>

<template>
  <div class="container">
    <div class="title">已通过请求</div>
    <div class="data-exist" v-if="fail_request.length > 0">
      <el-table :data="fail_request" style="width: 100%">
        <el-table-column prop="id" label="id" width="60" />
        <el-table-column prop="studentId" label="studentId" width="180" />
        <el-table-column prop="name" label="name" width="100" />
        <el-table-column prop="phone" label="phone" width="180" />
        <el-table-column prop="email" label="email" width="220" />
        <el-table-column prop="password" label="password" width="220" />
        <el-table-column prop="status" label="status" width="90" />
        <el-table-column prop="remark" label="remark" width="90" />
        <el-table-column
          prop="registerToken"
          label="registerToken"
          width="220"
        />
      </el-table>

      <div class="example-pagination-block">
        <!-- total除以10，向上取整就是最大页数，total可以表示为请求总条数，配合size使用 -->
        <el-pagination layout="prev, pager, next" :total="150" />
      </div>
    </div>
    <el-empty class="data-non-exist" description="暂时没有请求" v-else />
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
  .data-exist {
    margin: 0 auto;
  }
  .example-pagination-block {
    display: flex;
    justify-content: center;
    margin-top: 10px;
  }
}
</style>
