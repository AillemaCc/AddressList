<script setup>
import { ref, computed } from 'vue'
const radio1 = ref('')
const searchInput = ref('')
const majors = ref([
  {
    majorNum: 1,
    major: '计算机科学与技术',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
  {
    majorNum: 2,
    major: '大数据科学与技术',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
  {
    majorNum: 3,
    major: '应用数学',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
  {
    majorNum: 4,
    major: '软件工程',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
  {
    majorNum: 5,
    major: '人工智能',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
  {
    majorNum: 6,
    major: '信息安全',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
  {
    majorNum: 7,
    major: '计算数学',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
  {
    majorNum: 8,
    major: '物联网工程',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
  {
    majorNum: 9,
    major: '数据科学与大数据技术',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
  {
    majorNum: 10,
    major: '网络工程',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
  {
    majorNum: 11,
    major: '信息与计算科学',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
  {
    majorNum: 12,
    major: '智能科学与技术',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
])
const classes = ref([
  {
    class: 226,
    classNum: 6,
    majorNum: 1,
    major: '计算机科学与技术',
    academy: '数学与计算机学院',
    academyNum: 0,
  },
])
const students = ref([])

const inputPlaceHolder = computed(() => {
  if (radio1.value === '查询个人') {
    return '请输入班级编号'
  } else if (radio1.value === '查询班级') {
    return '请输入专业编号'
  } else if (radio1.value === '查询专业') {
    return '请输入学院编号'
  } else {
    return '请选择查询类别'
  }
})

function clearInput() {
  searchInput.value = ''
}
</script>

<template>
  <div class="container">
    <div class="search-form">
      <div class="search-radio">
        <el-radio-group v-model="radio1" size="large">
          <el-radio-button label="查询个人" @click="clearInput" />
          <el-radio-button label="查询班级" @click="clearInput" />
          <el-radio-button label="查询专业" @click="clearInput" />
        </el-radio-group>
      </div>
      <input
        class="search-input"
        v-model="searchInput"
        :placeholder="inputPlaceHolder"
      />
      <button class="search-button">
        <i class="iconfont icon-sousuo"></i>
      </button>
    </div>

    <div class="major-data-exist" v-if="radio1 === '查询专业'">
      <el-table :data="majors" style="width: 100%">
        <el-table-column prop="majorNum" label="专业编号" width="100" />
        <el-table-column prop="major" label="专业名称" width="200" />
        <el-table-column prop="academyNum" label="学院编号" width="100" />
        <el-table-column prop="academy" label="学院名称" width="200" />
        <el-table-column fixed="right" label="Operations" width="120">
          <template #default="scope">
            <el-button link type="primary" size="default">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="example-pagination-block" v-if="majors.length > 0">
        <!-- total除以10，向上取整就是最大页数，total可以表示为请求总条数，配合size使用 -->
        <el-pagination layout="prev, pager, next" :total="150" />
      </div>
    </div>
    <div class="class-data-exist" v-else-if="radio1 === '查询班级'">
      <el-table :data="classes" style="width: 100%">
        <el-table-column prop="classNum" label="班级编号" width="100" />
        <el-table-column prop="class" label="班级名称" width="200" />
        <el-table-column prop="majorNum" label="专业编号" width="100" />
        <el-table-column prop="major" label="专业名称" width="200" />
        <el-table-column prop="academyNum" label="学院编号" width="100" />
        <el-table-column prop="academy" label="学院名称" width="200" />
        <el-table-column fixed="right" label="Operations" width="120">
          <template #default="scope">
            <el-button link type="primary" size="default">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="example-pagination-block" v-if="classes.length > 0">
        <!-- total除以10，向上取整就是最大页数，total可以表示为请求总条数，配合size使用 -->
        <el-pagination layout="prev, pager, next" :total="150" />
      </div>
    </div>
    <div class="stu-data-exist" v-else-if="radio1 === '查询个人'">
      <el-table :data="students" style="width: 100%">
        <el-table-column prop="id" label="学生ID" width="80" />
        <el-table-column prop="studentId" label="学号" width="180" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号码" width="180" />
        <el-table-column prop="email" label="邮箱" width="220" />
        <el-table-column prop="password" label="密码" width="220" />
        <el-table-column prop="status" label="状态" width="90" />
        <el-table-column prop="remark" label="备注" width="90" />
        <el-table-column prop="registerToken" label="注册Token" width="220" />
      </el-table>

      <div class="example-pagination-block" v-if="students.length > 0">
        <!-- total除以10，向上取整就是最大页数，total可以表示为请求总条数，配合size使用 -->
        <el-pagination layout="prev, pager, next" :total="150" />
      </div>
    </div>
    <el-empty class="data-non-exist" description="暂时没有数据" v-else />
  </div>
</template>

<style scoped lang="scss">
.container {
  min-width: 720px;
  .search-form {
    display: flex;
    justify-content: space-around;
    align-items: center;
    width: 100%;
    height: 60px;
    .search-radio {
      margin-left: 20%;
      @media screen and (max-width: 1340px) {
        margin-left: 15%;
      }
      @media screen and (max-width: 1200px) {
        margin-left: 5%;
      }
    }
    .search-input {
      margin-left: auto;
      width: 240px;
      height: 40px;
      text-indent: 1em;
      border: 1px solid #999;
      outline: none;
      &:focus {
        border: 1px solid $mainColor;
      }
      &:focus + button {
        border: 1px solid $mainColor;
        border-left: none;
      }
    }
    .search-button {
      margin-right: 20%;
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
      @media screen and (max-width: 1340px) {
        margin-right: 15%;
      }
      @media screen and (max-width: 1200px) {
        margin-right: 5%;
      }
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
  .major-data-exist,
  .class-data-exist,
  .stu-data-exist {
    margin: 0 auto;
  }
  .example-pagination-block {
    display: flex;
    justify-content: center;
    margin-top: 10px;
  }
}
</style>
