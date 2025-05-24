<script setup>
import {
  adminDisplayFailRequestApi,
  adminRejectRequestApi,
  adminSuccessRequestApi,
} from '@/apis/admin/request'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
//获取待审核请求
const fail_request = ref([])
// 分页相关变量
const currentPage = ref(1)
const total = ref(0)
async function getFailRequest(num) {
  const res = await adminDisplayFailRequestApi({ current: num })
  if (res.success) {
    fail_request.value = res.data.records
    currentPage.value = res.data.current
    total.value = res.data.total
  } else {
    ElMessage.error(res.message)
  }
}
getFailRequest(1)

// 分页改变处理
const handleCurrentChange = (val) => {
  currentPage.value = val
  getFailRequest()
}

//同意请求按钮
async function successClick(studentId) {
  const res = await adminSuccessRequestApi({ studentId })
  if (res.success) {
    const index = fail_request.value.findIndex(
      (item) => item.studentId === studentId,
    )
    fail_request.value.splice(index, 1)
    if (fail_request.value.length === 0) {
      if (current.value === 1) {
        if (pages.value !== 1) {
          getFailRequest(1)
        }
      } else {
        current.value--
        getFailRequest(current.value)
      }
    }
    ElMessage.success(res.message)
  } else {
    ElMessage.error(res.message)
  }
}
let dialogVisible = ref(false)
let rejectStudentId = ref('')
let rejectName = ref('')
let rejextContent = ref('')
//点击拒绝，打开拒绝请求确认框
function rejectConfirm(studentId, name) {
  rejectStudentId.value = studentId
  rejectName.value = name
  rejextContent.value = ''
  dialogVisible.value = true
}
//确认拒绝请求
async function rejectClick() {
  dialogVisible.value = false
  const res = await adminRejectRequestApi({
    studentId: rejectStudentId.value,
    remark: rejextContent.value,
  })

  if (res.success) {
    const index = fail_request.value.findIndex(
      (item) => item.studentId === rejectStudentId.value,
    )
    fail_request.value.splice(index, 1)
    if (fail_request.value.length === 0) {
      if (current.value === 1) {
        if (pages.value !== 1) {
          getFailRequest(1)
        }
      } else {
        current.value--
        getFailRequest(current.value)
      }
    }
    ElMessage.success(res.message)
  } else {
    ElMessage.error(res.message)
  }
}
</script>

<template>
  <div class="dialog-container">
    <el-dialog v-model="dialogVisible" title="您确认要拒绝这条请求吗">
      <div class="dialog-body-container">
        <div class="reject-item">
          <div class="item-header">学生学号</div>
          <div class="item-body">{{ rejectStudentId }}</div>
        </div>
        <div class="reject-item">
          <div class="item-header">学生姓名</div>
          <div class="item-body">{{ rejectName }}</div>
        </div>
        <div class="reject-item">
          <div class="item-header">拒绝内容说明</div>
          <div class="item-body">
            <el-input class="reject" v-model="rejextContent" />
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="rejectClick"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>
  </div>

  <div class="container">
    <div class="title">待审核请求（{{ total }}条）</div>
    <div class="data-exist" v-if="fail_request.length > 0">
      <el-table :data="fail_request" style="width: 100%">
        <el-table-column prop="id" label="学生ID" width="80" />
        <el-table-column prop="studentId" label="学号" width="180" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号码" width="180" />
        <el-table-column prop="email" label="邮箱" width="300" />
        <el-table-column prop="password" label="密码" width="300" />
        <el-table-column prop="status" label="状态" width="90" />
        <el-table-column prop="registerToken" label="注册凭证" width="300" />
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="scope">
            <el-button
              link
              type="success"
              size="default"
              @click="successClick(scope.row.studentId)"
              >同意</el-button
            >
            <el-button
              link
              type="danger"
              size="default"
              @click="rejectConfirm(scope.row.studentId, scope.row.name)"
              >拒绝</el-button
            >
          </template>
        </el-table-column>
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
.dialog-container {
  :deep(.el-dialog) {
    min-width: 500px;
    padding: 24px;
  }

  .dialog-body-container {
    padding: 20px 20px;
    .reject-item {
      display: flex;
      justify-content: start;
      align-items: center;
      width: 400px;
      height: 40px;
      margin: 5px 0;
      .item-header {
        display: flex;
        justify-content: end;
        padding-right: 12px;
        width: 120px;
      }
      .item-body {
        padding-left: 10px;
        flex: 1;
      }
    }
  }
}
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
