<script setup>
import { ref } from 'vue'
import { useStuInfoStore } from '@/stores/stuInfo'
import { stuDeleteAddressApi, stuQueryDeletedApi } from '@/apis/stu/friends'
import { ElMessage } from 'element-plus'

const friends = ref([])
const stuInfoStore = useStuInfoStore()
const studentId = stuInfoStore.stuInfo.studentId
const current = ref(1)
const total = ref(0)
const pages = ref(0)

async function getDeletedFriends(num) {
  const res = await stuQueryDeletedApi({
    studentId: studentId.value,
    current: num,
    size: 10,
  })
  if (res.success) {
    friends.value = res.data.records
    current.value = res.data.current
    total.value = res.data.total
    pages.value = res.data.pages
  } else {
    ElMessage.error(res.message)
  }
}
getDeletedFriends(1)
function changePage(val) {
  current.value = val
  getFriends(val)
}
</script>
<template>
  <div class="container">
    <div class="title">已删除好友（共{{ total }}位）</div>
    <div class="data-exist" v-if="friends.length > 0">
      <el-table :data="friends" style="width: 100%">
        <el-table-column prop="studentId" label="学号" width="180" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="academy" label="学院" width="200" />
        <el-table-column prop="major" label="专业" width="200" />
        <el-table-column prop="className" label="班级" width="100" />
        <el-table-column prop="enrollmentYear" label="入学年份" width="100" />
        <el-table-column prop="graduationYear" label="毕业年份" width="100" />
        <el-table-column prop="employer" label="就业单位" width="200" />
        <el-table-column prop="city" label="就业城市" width="100" />
        <el-table-column prop="phone" label="手机号码" width="180" />
        <el-table-column prop="email" label="电子邮箱" width="220" />
      </el-table>

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
