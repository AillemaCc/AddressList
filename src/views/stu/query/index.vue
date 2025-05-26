<script setup>
import { ref } from 'vue'
const friends = ref([
  {
    studentId: '9109222254',
    name: '黎明',
    academy: '数学与计算机学院',
    major: '计算机科学与技术',
    className: '226班',
    enrollmentYear: '2022',
    graduationYear: '2026',
    employer: '南昌大学',
    city: '南昌市',
    phone: '153****0172',
    email: '235645789@qq.com',
  },
  {
    studentId: '9109222255',
    name: '张伟',
    academy: '数学与计算机学院',
    major: '计算机科学与技术',
    className: '226班',
    enrollmentYear: '2022',
    graduationYear: '2026',
    employer: '腾讯科技',
    city: '深圳市',
    phone: '13800138001',
    email: 'zhangwei@example.com',
  },
  {
    studentId: '9109222256',
    name: '李娜',
    academy: '数学与计算机学院',
    major: '软件工程',
    className: '227班',
    enrollmentYear: '2022',
    graduationYear: '2026',
    employer: '阿里巴巴',
    city: '杭州市',
    phone: '13912345678',
    email: 'lina@example.com',
  },
  {
    studentId: '9109222257',
    name: '王强',
    academy: '数学与计算机学院',
    major: '人工智能',
    className: '228班',
    enrollmentYear: '2022',
    graduationYear: '2026',
    employer: '字节跳动',
    city: '北京市',
    phone: '18611112222',
    email: 'wangqiang@example.com',
  },
  {
    studentId: '9109222258',
    name: '陈静',
    academy: '数学与计算机学院',
    major: '数据科学与大数据技术',
    className: '229班',
    enrollmentYear: '2022',
    graduationYear: '2026',
    employer: '华为技术',
    city: '东莞市',
    phone: '15987654321',
    email: 'chenjing@example.com',
  },
  {
    studentId: '9109222259',
    name: '刘洋',
    academy: '数学与计算机学院',
    major: '信息安全',
    className: '230班',
    enrollmentYear: '2022',
    graduationYear: '2026',
    employer: '百度',
    city: '北京市',
    phone: '13524681357',
    email: 'liuyang@example.com',
  },
  {
    studentId: '9109222260',
    name: '赵敏',
    academy: '数学与计算机学院',
    major: '网络工程',
    className: '231班',
    enrollmentYear: '2022',
    graduationYear: '2026',
    employer: '京东集团',
    city: '上海市',
    phone: '13713572468',
    email: 'zhaomin@example.com',
  },
  {
    studentId: '9109222261',
    name: '周杰',
    academy: '数学与计算机学院',
    major: '物联网工程',
    className: '232班',
    enrollmentYear: '2022',
    graduationYear: '2026',
    employer: '小米科技',
    city: '武汉市',
    phone: '18888888888',
    email: 'zhoujie@example.com',
  },
])
</script>
<template>
  <div class="container">
    <el-input />
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
        <el-pagination layout="prev, pager, next" :total="friends.length" />
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
