<script setup>
import { stuEnterRequestApi } from '@/apis/stu/request'
import { stuNameQueryApi } from '@/apis/stu/other'
import { ref } from 'vue'
const queryInput = ref('')
const queryResult = ref([])
const current = ref(1)
const total = ref(0)
const pages = ref(0)
const dialogVisible = ref(false)
const enterStudentId = ref('')
const enterName = ref('')
const enterContent = ref('')
async function nameQuery(num) {
  const res = await stuNameQueryApi({
    name: queryInput.value,
    current: num,
  })
  console.log(res)
  if (res.success) {
    queryResult.value = res.data.records
    current.value = res.data.current
    total.value = res.data.total
    pages.value = res.data.pages
  } else {
    ElMessage.error(res.message)
  }
}
function openDialog(studentId, name) {
  enterStudentId.value = studentId
  enterName.value = name
  dialogVisible.value = true
}
async function EnterRequest() {
  dialogVisible.value = false
  const res = await stuEnterRequestApi({
    receiver: enterStudentId.value,
    content: enterContent.value,
  })

  if (res.success) {
    ElMessage.success(res.message)
  } else {
    ElMessage.error(res.message)
  }
}
function changePage(val) {
  current.value = val
  nameQuery(val)
}
</script>
<template>
  <div class="dialog-container">
    <el-dialog v-model="dialogVisible" title="您确认要向他/她发送申请请求吗">
      <div class="dialog-body-container">
        <div class="reject-item">
          <div class="item-header">学生学号</div>
          <div class="item-body">{{ enterStudentId }}</div>
        </div>
        <div class="reject-item">
          <div class="item-header">学生姓名</div>
          <div class="item-body">{{ enterName }}</div>
        </div>
        <div class="reject-item">
          <div class="item-header">通讯录申请留言</div>
          <div class="item-body">
            <el-input class="reject" v-model="enterContent" />
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="EnterRequest"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>
  </div>

  <div class="container">
    <div class="input-container">
      <input
        class="query-input"
        v-model="searchInput"
        placeholder="请输入查询学生的姓名"
      />
      <button class="query-button" @click="nameQuery(1)">
        <i class="iconfont icon-sousuo"></i>
      </button>
    </div>

    <div class="data-exist" v-if="queryResult.length > 0">
      <el-table :data="queryResult" style="width: 100%">
        <el-table-column prop="studentId" label="学号" width="220" />
        <el-table-column prop="name" label="姓名" width="150" />
        <el-table-column prop="academyName" label="学院" width="200" />
        <el-table-column prop="majorName" label="专业" width="200" />
        <el-table-column prop="className" label="班级" width="200" />
        <el-table-column prop="enrollmentYear" label="入学年份" width="120" />
        <el-table-column prop="graduationYear" label="毕业年份" width="120" />
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="{ row }">
            <el-button
              link
              type="success"
              size="large"
              @click="openDialog(row.studentId, row.name)"
              >申请</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <div class="example-pagination-block">
        <!-- total除以10，向上取整就是最大页数，total可以表示为请求总条数，配合size使用 -->
        <el-pagination
          layout="prev, pager, next"
          :total="total"
          v-model:current-page="current"
          @current-change="changePage()"
        />
      </div>
    </div>
    <el-empty class="data-non-exist" description="暂时没有数据" v-else />
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
  padding: 0 40px;
  .input-container {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 20px;
    .query-input {
      width: 240px;
      height: 40px;
      text-indent: 1em;
      border: 1px solid #999;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      outline: none;
      &:focus {
        border: 1px solid $mainColor;
      }
      &:focus + button {
        border: 1px solid $mainColor;
        border-left: none;
      }
    }
    .query-button {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 40px;
      height: 40px;
      background-color: #fff;
      border: 1px solid #999;
      border-left: none;
      outline: none;
      transition: 0.2s ease;
      cursor: pointer;
      .iconfont {
        font-size: 18px;
        font-weight: 600;
      }
      &:hover {
        color: #fff;
        background-color: $mainColor;
      }
    }
  }

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
