<script setup>
import { adminBanStudentApi, adminUnbanStudentApi } from '@/apis/admin/ban'
import {
  adminDisplayMajorApi,
  adminDisplayClassApi,
  adminDisplayStudentApi,
  adminUpdateMajorApi,
  adminUpdateClassApi,
  adminAddClassApi,
} from '@/apis/admin/query'
import { ElMessage } from 'element-plus'
import { ref, computed } from 'vue'

const radio1 = ref('')
const searchInput = ref('')
const dialogTitle = ref('')

const majorCurrent = ref(1)
const majorPages = ref(0)
const majorTotal = ref(0)

const classCurrent = ref(1)
const classPages = ref(0)
const classTotal = ref(0)

const studentCurrent = ref(1)
const studentPages = ref(0)
const studentTotal = ref(0)

// 获取查询到的专业
const majors = ref([])
async function getMajor(current) {
  const res = await adminDisplayMajorApi({
    academyNum: +searchInput.value,
    current,
  })
  if (res.success) {
    majors.value = res.data.records
    majorCurrent.value = res.data.current
    majorPages.value = res.data.pages
    majorTotal.value = res.data.total
  } else {
    ElMessage.error(res.message)
  }
}

// 获取查询到的班级
const classes = ref([])
async function getClass(current) {
  const res = await adminDisplayClassApi({
    majorNum: +searchInput.value,
    current,
  })
  if (res.success) {
    classes.value = res.data.records
    classCurrent.value = res.data.current
    classPages.value = res.data.pages
    classTotal.value = res.data.total
  } else {
    ElMessage.error(res.message)
  }
}

// 获取查询到的学生
const students = ref([])
async function getStudent(current) {
  const res = await adminDisplayStudentApi({
    classNum: searchInput.value,
    current,
  })
  if (res.success) {
    students.value = res.data.records
    studentCurrent.value = res.data.current
    studentPages.value = res.data.pages
    studentTotal.value = res.data.total
  } else {
    ElMessage.error(res.message)
  }
}
// 分页改变时的处理
const handleCurrentChange = (val) => {
  if (radio1.value === '查询个人') {
    studentCurrent.value = val
    getStudent(val)
  } else if (radio1.value === '查询班级') {
    classCurrent.value = val
    getClass(val)
  } else if (radio1.value === '查询专业') {
    majorCurrent.value = val
    getMajor(val)
  }
}

// 点击搜索的类别
function getListInfo() {
  if (radio1.value === '查询个人') {
    getStudent(1)
  } else if (radio1.value === '查询班级') {
    getClass(1)
  } else if (radio1.value === '查询专业') {
    getMajor(1)
  } else {
    ElMessage.warning('请先选择查询类别')
  }
}

// 输入框展示的信息
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

//点击单选框清空输入框
function clearInput() {
  searchInput.value = ''
}

// 专业编辑相关
const majorDialogVisible = ref(false)
const originalMajorNum = ref(0)
const editMajor_majorNum = ref(0)
const editMajor_majorName = ref('')
const editMajor_academyNum = ref(0)
const editMajor_academyName = ref('')

//打开编辑专业框
function openEditMajor(majorNum, majorName, academyNum, academyName) {
  dialogTitle.value = '编辑专业信息'
  originalMajorNum.value = majorNum
  editMajor_majorNum.value = majorNum
  editMajor_majorName.value = majorName
  editMajor_academyNum.value = academyNum
  editMajor_academyName.value = academyName
  majorDialogVisible.value = true
}

//点击确认更新专业信息
async function updateMajor() {
  const res = await adminUpdateMajorApi({
    majorNum: editMajor_majorNum.value,
    majorName: editMajor_majorName.value,
    academyNum: editMajor_academyNum.value,
    academyName: editMajor_academyName.value,
  })
  if (res.success) {
    const index = majors.value.findIndex(
      (item) => item.majorNum === originalMajorNum.value,
    )
    if (index !== -1) {
      majors.value[index] = {
        majorNum: editMajor_majorNum.value,
        major: editMajor_majorName.value,
        academyNum: editMajor_academyNum.value,
        academy: editMajor_academyName.value,
      }
    }
    ElMessage.success(res.message)
  } else {
    ElMessage.error(res.message)
  }
  majorDialogVisible.value = false
}

// 班级编辑相关
const classDialogVisible = ref(false)
const originalClassNum = ref('')
const editClass_classNum = ref(0)
const editClass_className = ref('')
const editClass_majorNum = ref(0)
const editClass_majorName = ref('')
const editClass_academyNum = ref(0)
const editClass_academyName = ref('')

//打开编辑班级框
function openEditClass(
  title,
  classNum,
  className,
  majorNum,
  majorName,
  academyNum,
  academyName,
) {
  dialogTitle.value = title
  originalClassNum.value = classNum
  editClass_classNum.value = classNum
  editClass_className.value = className
  editClass_majorNum.value = majorNum
  editClass_majorName.value = majorName
  editClass_academyNum.value = academyNum
  editClass_academyName.value = academyName
  classDialogVisible.value = true
}

//根据打开的编辑框的不同标题确认是新增班级还是修改班级信息
async function updateClass() {
  if (dialogTitle.value === '编辑班级信息') {
    const res = await adminUpdateClassApi({
      classNum: editClass_classNum.value,
      className: editClass_className.value,
      majorNum: editClass_majorNum.value,
      majorName: editClass_majorName.value,
      academyNum: editClass_academyNum.value,
      academyName: editClass_academyName.value,
    })
    if (res.success) {
      //回显到原表格
      const index = classes.value.findIndex(
        (item) => item.classNum === originalClassNum.value,
      )
      if (index !== -1) {
        classes.value[index] = {
          classNum: editClass_classNum.value,
          className: editClass_className.value,
          majorNum: editClass_majorNum.value,
          major: editClass_majorName.value,
          academyNum: editClass_academyNum.value,
          academy: editClass_academyName.value,
        }
      }
      ElMessage.success(res.message)
    } else {
      ElMessage.error(res.message)
    }
  } else if (dialogTitle.value === '新增班级') {
    const res = await adminAddClassApi({
      classNum: editClass_classNum.value,
      className: editClass_className.value,
      majorNum: editClass_majorNum.value,
      majorName: editClass_majorName.value,
      academyNum: editClass_academyNum.value,
      academyName: editClass_academyName.value,
    })
    if (res.success) {
      ElMessage.success(res.message)
    } else {
      ElMessage.error(res.message)
    }
  }

  classDialogVisible.value = false
}

//禁用学生
async function ban(studentId) {
  const res = await adminBanStudentApi({ studentId })
  if (res.success) {
    ElMessage.success(res.message)
  } else {
    ElMessage.error(res.message)
  }
}
//解禁学生
async function unban(studentId) {
  const res = await adminUnbanStudentApi({ studentId })
  if (res.success) {
    ElMessage.success(res.message)
  } else {
    ElMessage.error(res.message)
  }
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
      <button class="search-button" @click="getListInfo()">
        <i class="iconfont icon-sousuo"></i>
      </button>
    </div>

    <!-- 专业信息部分 -->
    <div class="major-data-exist" v-if="radio1 === '查询专业'">
      <div class="dialog-container">
        <el-dialog v-model="majorDialogVisible" :title="dialogTitle">
          <div class="dialog-body-container">
            <div class="reject-item">
              <div class="item-header">专业编号</div>
              <div class="item-body">
                <el-input class="reject" v-model="editMajor_majorNum" />
              </div>
            </div>
            <div class="reject-item">
              <div class="item-header">专业名称</div>
              <div class="item-body">
                <el-input class="reject" v-model="editMajor_majorName" />
              </div>
            </div>
            <div class="reject-item">
              <div class="item-header">学院编号</div>
              <div class="item-body">
                <el-input class="reject" v-model="editMajor_academyNum" />
              </div>
            </div>
            <div class="reject-item">
              <div class="item-header">学院名称</div>
              <div class="item-body">
                <el-input class="reject" v-model="editMajor_academyName" />
              </div>
            </div>
          </div>

          <template #footer>
            <span class="dialog-footer">
              <el-button @click="majorDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="updateMajor()">
                确认
              </el-button>
            </span>
          </template>
        </el-dialog>
      </div>
      <el-table :data="majors" style="width: 100%">
        <el-table-column prop="majorNum" label="专业编号" width="240" />
        <el-table-column prop="major" label="专业名称" width="480" />
        <el-table-column prop="academyNum" label="学院编号" width="240" />
        <el-table-column prop="academy" label="学院名称" width="480" />
        <el-table-column fixed="right" label="操作" width="240">
          <template #default="scope">
            <el-button
              link
              type="primary"
              size="default"
              @click="
                openEditMajor(
                  scope.row.majorNum,
                  scope.row.major,
                  scope.row.academyNum,
                  scope.row.academy,
                )
              "
              >编辑</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <div class="example-pagination-block">
        <el-pagination
          layout="prev, pager, next"
          :total="majorTotal"
          v-model:current-page="majorCurrent"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 班级信息部分 -->
    <div class="class-data-exist" v-else-if="radio1 === '查询班级'">
      <div class="dialog-container">
        <el-dialog v-model="classDialogVisible" :title="dialogTitle">
          <div class="dialog-body-container">
            <div class="reject-item">
              <div class="item-header">班级编号</div>
              <div class="item-body">
                <el-input class="reject" v-model="editClass_classNum" />
              </div>
            </div>
            <div class="reject-item">
              <div class="item-header">班级名称</div>
              <div class="item-body">
                <el-input class="reject" v-model="editClass_className" />
              </div>
            </div>
            <div class="reject-item">
              <div class="item-header">专业编号</div>
              <div class="item-body">
                <el-input class="reject" v-model="editClass_majorNum" />
              </div>
            </div>
            <div class="reject-item">
              <div class="item-header">专业名称</div>
              <div class="item-body">
                <el-input class="reject" v-model="editClass_majorName" />
              </div>
            </div>
            <div class="reject-item">
              <div class="item-header">学院编号</div>
              <div class="item-body">
                <el-input class="reject" v-model="editClass_academyNum" />
              </div>
            </div>
            <div class="reject-item">
              <div class="item-header">学院名称</div>
              <div class="item-body">
                <el-input class="reject" v-model="editClass_academyName" />
              </div>
            </div>
          </div>

          <template #footer>
            <span class="dialog-footer">
              <el-button @click="classDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="updateClass()">
                确认
              </el-button>
            </span>
          </template>
        </el-dialog>
      </div>

      <div class="create-class-button-container">
        <div
          class="create-class-button"
          @click="openEditClass('新增班级', '', '', '', '', '', '')"
        >
          +新增班级
        </div>
      </div>

      <el-table :data="classes" style="width: 100%">
        <el-table-column prop="classNum" label="班级编号" width="220" />
        <el-table-column prop="className" label="班级名称" width="300" />
        <el-table-column prop="majorNum" label="专业编号" width="220" />
        <el-table-column prop="major" label="专业名称" width="300" />
        <el-table-column prop="academyNum" label="学院编号" width="220" />
        <el-table-column prop="academy" label="学院名称" width="300" />
        <el-table-column fixed="right" label="操作" width="150">
          <template #default="scope">
            <el-button
              link
              type="primary"
              size="default"
              @click="
                openEditClass(
                  '编辑班级信息',
                  scope.row.classNum,
                  scope.row.className,
                  scope.row.majorNum,
                  scope.row.major,
                  scope.row.academyNum,
                  scope.row.academy,
                )
              "
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="example-pagination-block">
        <el-pagination
          layout="prev, pager, next"
          :total="classTotal"
          v-model:current-page="classCurrent"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 学生信息部分 -->
    <div class="stu-data-exist" v-else-if="radio1 === '查询个人'">
      <el-table :data="students" style="width: 100%">
        <el-table-column prop="studentId" label="学号" width="160" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="enrollmentYear" label="入学年份" width="100" />
        <el-table-column prop="graduationYear" label="毕业年份" width="100" />
        <el-table-column prop="employer" label="就业单位" width="150" />
        <el-table-column prop="city" label="就业城市" width="100" />
        <el-table-column prop="phone" label="手机号码" width="140" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="registrationTime" label="注册时间" width="200" />
        <el-table-column
          prop="lastLoginTime"
          label="上次登录时间"
          width="200"
        />
        <el-table-column prop="status" label="状态" width="60" />
        <el-table-column fixed="right" label="操作" width="180">
          <template #default="scope">
            <el-button
              link
              type="danger"
              size="default"
              @click="ban(scope.row.studentId)"
              >禁用</el-button
            >
            <el-button
              link
              type="success"
              size="default"
              @click="unban(scope.row.studentId)"
              >解禁</el-button
            >
          </template></el-table-column
        >
      </el-table>

      <div class="example-pagination-block">
        <el-pagination
          layout="prev, pager, next"
          :total="studentTotal"
          v-model:current-page="studentCurrent"
          @current-change="handleCurrentChange"
        />
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
    .create-class-button-container {
      display: flex;
      justify-content: end;
      align-items: center;
      width: 100%;
      height: 60px;
    }
    .create-class-button {
      display: flex;
      justify-content: center;
      align-items: center;
      margin-right: 60px;
      width: 100px;
      height: 40px;
      border: solid 1px $mainColor;
      border-radius: 10px;
      color: #fff;
      background-color: $mainColor;
      cursor: pointer;
      &:hover {
        background-color: #5289ea;
      }
    }
    :deep(.el-table__header-wrapper),
    :deep(.el-scrollbar__wrap) {
      display: flex;
      justify-content: center;
    }
  }
  .example-pagination-block {
    position: relative;
    display: flex;
    justify-content: center;
    margin-top: 10px;
  }
}
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
</style>
