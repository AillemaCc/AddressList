<script setup>
import { ref } from 'vue'

const fail_request = ref([
  {
    id: 7,
    studentId: '9109222268',
    name: '学生1',
    phone: '15682496592',
    email: 'vacsbt_om0@qq.com',
    password: '5f_b6aYn0eI0vrG',
    status: 0,
    remark: null,
    registerToken: 'd9c8030a-ec7e-46fb-9432-46064cfa2cfa',
  },
  {
    id: 14,
    studentId: '7861232160',
    name: '谢杰宏',
    phone: '76908043742',
    email: 'xiejiehong@hotmail.com',
    password: 'pg67FEVQd9',
    status: 0,
    remark: null,
    registerToken: 'QmpaCSNMuV',
  },
  {
    id: 16,
    studentId: '9977654280',
    name: '丁安琪',
    phone: '75531174119',
    email: 'anqiding10@gmail.com',
    password: 'OjnJ2ORzGi',
    status: 0,
    remark: null,
    registerToken: 'leTidb7Ikq',
  },
  {
    id: 24,
    studentId: '0102450291',
    name: '蒋震南',
    phone: '14570535938',
    email: 'jizhenn2@icloud.com',
    password: 'mDDnQKR5F7',
    status: 0,
    remark: null,
    registerToken: 'EO5Dvn5Hpx',
  },
  {
    id: 18,
    studentId: '9929013995',
    name: '钟晓明',
    phone: '14083198181',
    email: 'zhongxiaoming94@outlook.com',
    password: 's9VPTu9sYM',
    status: 0,
    remark: null,
    registerToken: 'pCsufxAJCF',
  },
  {
    id: 23,
    studentId: '6036732234',
    name: '苏云熙',
    phone: '7696628026',
    email: 'suyunx@gmail.com',
    password: 'WRJYpJt9If',
    status: 0,
    remark: null,
    registerToken: 'icc87GMvLY',
  },
  {
    id: 15,
    studentId: '6229829390',
    name: '潘震南',
    phone: '16959728730',
    email: 'zhennpa325@gmail.com',
    password: 'OIkurvnYPA',
    status: 0,
    remark: null,
    registerToken: 'lZ9Ony0ugr',
  },
  {
    id: 17,
    studentId: '4756166277',
    name: '林子异',
    phone: '7691679909',
    email: 'ziyil59@mail.com',
    password: 'BaJBCAgMb1',
    status: 0,
    remark: null,
    registerToken: 'ck05DYPpUz',
  },
  {
    id: 13,
    studentId: '5003525550',
    name: '何安琪',
    phone: '1047590707',
    email: 'hean@icloud.com',
    password: 'p6Fin0Zon8',
    status: 0,
    remark: null,
    registerToken: 'mheuVCT9k0',
  },
  {
    id: 22,
    studentId: '7473179618',
    name: '罗嘉伦',
    phone: '14936431696',
    email: 'ljialun@gmail.com',
    password: 'VR19JUIpZN',
    status: 0,
    remark: null,
    registerToken: 'RiSMnF2eT0',
  },
  {
    id: 9,
    studentId: '3074093266',
    name: '袁安琪',
    phone: '19933193063',
    email: 'anqiyuan@outlook.com',
    password: 'DKzGhYOUGU',
    status: 0,
    remark: null,
    registerToken: 'hYe2GlFTfa',
  },
  {
    id: 20,
    studentId: '5205751527',
    name: '金睿',
    phone: '214994162',
    email: 'jirui10@outlook.com',
    password: 'ZhXuT9Xn3Z',
    status: 0,
    remark: null,
    registerToken: 'k12v5of1ma',
  },
  {
    id: 25,
    studentId: '7180203832',
    name: '李晓明',
    phone: '75569735962',
    email: 'xiaomingli@hotmail.com',
    password: 'e0leyoYQHd',
    status: 0,
    remark: null,
    registerToken: '2qSpxRstzP',
  },
  {
    id: 21,
    studentId: '7081186143',
    name: '薛嘉伦',
    phone: '288996887',
    email: 'xueji@gmail.com',
    password: 'rWNxrNFwnL',
    status: 0,
    remark: null,
    registerToken: '2V3T5Cf6Uv',
  },
  {
    id: 10,
    studentId: '9617285077',
    name: '王震南',
    phone: '17011074791',
    email: 'wangz@hotmail.com',
    password: 'QystNvwcMK',
    status: 0,
    remark: null,
    registerToken: 'eaBclxpY21',
  },
  {
    id: 12,
    studentId: '8945900647',
    name: '田安琪',
    phone: '7550062254',
    email: 'ta4@hotmail.com',
    password: '51xgXc0cPs',
    status: 0,
    remark: null,
    registerToken: '9UvgdMwsel',
  },
  {
    id: 19,
    studentId: '5288686811',
    name: '秦致远',
    phone: '7602356489',
    email: 'zqin@icloud.com',
    password: 'eNZgJpUfAd',
    status: 0,
    remark: null,
    registerToken: 'kgMOl3yQOQ',
  },
  {
    id: 8,
    studentId: '0586460928',
    name: '丁子韬',
    phone: '2884445518',
    email: 'zdin@gmail.com',
    password: 'stJQzEWSvv',
    status: 0,
    remark: null,
    registerToken: 'vOFcGeO4yY',
  },
  {
    id: 11,
    studentId: '9637271692',
    name: '莫云熙',
    phone: '104074406',
    email: 'moyu@gmail.com',
    password: '26iW7SfydF',
    status: 0,
    remark: null,
    registerToken: 'qwLk9zo8Oa',
  },
  {
    id: 26,
    studentId: '4408633959',
    name: '郭震南',
    phone: '76961262345',
    email: 'guo1001@icloud.com',
    password: 'wWZUEnQCXl',
    status: 0,
    remark: null,
    registerToken: 'CqOXk3d3zU',
  },
  {
    id: 27,
    studentId: '9646857155',
    name: '陈詩涵',
    phone: '2132594034',
    email: 'chenshih@gmail.com',
    password: 'c5xdIa8LzK',
    status: 0,
    remark: null,
    registerToken: '5PaH5oD3sR',
  },
])
function successClick(studentId) {
  console.log(studentId)
}
function rejectClick(studentId) {
  console.log(studentId)
}
</script>

<template>
  <div class="container">
    <div class="title">待审核请求</div>
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
        <el-table-column fixed="right" label="Operations" width="120">
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
              @click="rejectClick(scope.row.studentId)"
              >拒绝</el-button
            >
          </template>
        </el-table-column>
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
  .example-pagination-block {
    display: flex;
    justify-content: center;
    margin-top: 10px;
    transform: translateX(-100px);
  }
}
</style>
