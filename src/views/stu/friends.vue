<script setup>
import { ref } from 'vue'
import { useStuInfoStore } from '@/stores/stuInfo'
import {
  stuDeleteAddressApi,
  stuGetFriendsApi,
  stuQueryFriendsApi,
} from '@/apis/stu/friends'
import { ElMessage } from 'element-plus'

const friends = ref([])
const stuInfoStore = useStuInfoStore()
const studentId = stuInfoStore.stuInfo.studentId
const current = ref(1)
const total = ref(0)
const pages = ref(0)
const queryStudentId = ref('')

async function getFriends(num) {
  const res = await stuGetFriendsApi({
    ownerId: studentId,
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
getFriends(1)

async function deleteFriend(contactId) {
  const res = await stuDeleteAddressApi({
    ownerId: studentId,
    contactId,
  })
  if (res.success) {
    const index = friends.value.findIndex(
      (item) => item.studentId === contactId,
    )
    friends.value.splice(index, 1)
    if (friends.value.length === 0) {
      if (current.value === 1) {
        if (pages.value !== 1) {
          getFriends(1)
        }
      } else {
        current.value--
        getFriends(current.value)
      }
    }
    ElMessage.success(res.message)
  } else {
    ElMessage.error(res.message)
  }
}

async function query() {
  const res = await stuQueryFriendsApi({
    ownerId: studentId,
    contactId: queryStudentId.value,
  })
  friends.value = [{ ...res.data }]
  current.value = 1
  total.value = 1
  pages.value = 1
}
function changePage(val) {
  current.value = val
  getFriends(val)
}
</script>
<template>
  <div class="container">
    <div class="title">
      <div class="left">我的好友（共{{ total }}位）</div>
      <div class="right">
        <div class="input-container">
          <input
            type="text"
            v-model="queryStudentId"
            placeholder="请输入查询学号"
          />
          <button @click="query"><i class="iconfont icon-sousuo"></i></button>
        </div>
      </div>
    </div>
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
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="{ row }">
            <el-button
              link
              type="danger"
              size="large"
              @click="deleteFriend(row.studentId)"
              >删除</el-button
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
    display: flex;
    justify-content: space-between;
    padding-bottom: 10px;
    margin-bottom: 10px;
    background-color: #fff;
    border-bottom: 1px solid #a1a1a1;

    .left {
      font-size: 20px;
      color: $mainColor;
      cursor: default;
    }
    .right {
      width: 150px;
      height: 26px;
      .input-container {
        display: flex;
        border-radius: 13px;
        border: 1px solid #a1a1a1;
        overflow: hidden;
        input {
          width: 124px;
          flex: 1;
          border: none;
          outline: none;
          text-indent: 10px;
          font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        button {
          width: 26px;
          height: 26px;
          border: none;
          outline: none;
          font-weight: 600;
          background-color: #fff;
          cursor: pointer;
          &:hover {
            color: $mainColor;
          }
        }
      }
    }
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
