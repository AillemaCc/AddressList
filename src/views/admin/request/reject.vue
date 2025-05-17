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
    <div class="data-exist" v-if="fail_request.length > 0">
      <el-table :data="fail_request" style="width: 100%">
        <el-table-column prop="id" label="id" width="60" />
        <el-table-column prop="studentId" label="学号" width="180" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="电话号码" width="180" />
        <el-table-column prop="email" label="邮箱" width="220" />
        <el-table-column prop="password" label="密码" width="220" />
        <el-table-column prop="status" label="状态" width="90" />
        <el-table-column prop="remark" label="备注" width="90" />
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
  }
}
</style>
