<script setup>
import { ref, onUnmounted } from 'vue'
import dayjs from 'dayjs'
import axios from 'axios'
import {
  adminExportAllApi,
  adminExportDetailApi,
  adminGetHomeInfoApi,
} from '@/apis/admin/other'
import {useAdminInfoStore} from '@/stores/adminInfo'
import { animation } from '@/utils/animation'
import { ElMessage } from 'element-plus'

const adminInfoStore = useAdminInfoStore()

// 动态时间
const time = ref(dayjs().format('YYYY-MM-DD HH:mm:ss'))
const timer = ref(null)

// 启动计时器
const startTimer = () => {
  timer.value = setInterval(() => {
    time.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
  }, 1000)
}

// 组件挂载时启动计时器
startTimer()

// 组件卸载时清除计时器
onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})
const weather = ref()
axios({
  url: 'http://hmajax.itheima.net/api/weather',
  params: {
    city: 360100,
  },
}).then((res) => {
  weather.value = res.data.data.todayWeather.weather
})

const username = ref('')
const request = ref({
  pending: 0,
  approved: 0,
  rejected: 0,
})
const board = ref({
  draft: 0,
  released: 0,
  pulledoff: 0,
})
async function getHome() {
  const res = await adminGetHomeInfoApi({
    username:adminInfoStore.adminInfo.username
  })
  if (res.success) {
    username.value = res.data.username
    request.value = res.data.request
    for (let i in request.value) {
      animation(0, request.value[i], 1500, (value) => {
        request.value[i] = Math.floor(value)
      })
    }
    board.value = res.data.board
    for (let i in board.value) {
      animation(0, board.value[i], 1500, (value) => {
        board.value[i] = Math.floor(value)
      })
    }
  }
}
getHome()

const dialogVisible = ref(false)
const classNum = ref(0)
const majorNum = ref(0)
const enrollmentYear = ref('')
const graduationYear = ref('')

async function exportAll() {
  const res = await adminExportAllApi({})
  if (res.success) {
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url

    let fileName = 'export.xlsx'

    link.setAttribute('download', fileName)
    document.body.appendChild(link)
    link.click()

    // 清理
    link.remove()
    window.URL.revokeObjectURL(url)
    ElMessage.success(res.message)
  } else {
    ElMessage.error(res.message)
  }
}

function openDialog() {
  classNum.value = ''
  majorNum.value = ''
  enrollmentYear.value = ''
  graduationYear.value = ''
  dialogVisible.value = true
}
async function exportDetail() {
  const res = await adminExportDetailApi({
    classNum: classNum.value,
    majorNum: majorNum.value,
    enrollmentYear: enrollmentYear.value,
    graduationYear: graduationYear.value,
  })
  if (res.success) {
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url

    let fileName = 'export.xlsx'

    link.setAttribute('download', fileName)
    document.body.appendChild(link)
    link.click()

    // 清理
    link.remove()
    window.URL.revokeObjectURL(url)

    dialogVisible.value = false
    ElMessage.success(res.message)
  } else {
    ElMessage.error(res.message)
  }
}
</script>

<template>
  <div class="dialog-container">
    <el-dialog v-model="dialogVisible" title="请输入导出学籍条件">
      <div class="dialog-body-container">
        <div class="reject-item">
          <div class="item-header">班级编号</div>
          <div class="item-body">
            <el-input class="reject" v-model="classNum" />
          </div>
        </div>
        <div class="reject-item">
          <div class="item-header">专业编号</div>
          <div class="item-body">
            <el-input class="reject" v-model="majorNum" />
          </div>
        </div>
        <div class="reject-item">
          <div class="item-header">入学年份</div>
          <div class="item-body">
            <el-input class="reject" v-model="enrollmentYear" />
          </div>
        </div>
        <div class="reject-item">
          <div class="item-header">毕业年份</div>
          <div class="item-body">
            <el-input class="reject" v-model="graduationYear" />
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="classDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="exportDetail"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
  <div class="container">
    <div class="one-container">
      <div class="one-left-container"></div>
      <div class="one-right-container">
        <div class="welcome-container">晚上好 {{ username }},</div>
        <div class="back-container">欢迎回来</div>
        <div class="time-container">现在是{{ time }}</div>
        <div class="weather-container">天气 {{ weather }}</div>
      </div>
    </div>
    <div class="two-container">
      <div class="two-left-container">
        <router-link to="/admin/request_fail"
          ><div class="request">
            您还有{{ request.pending }}条审核待处理
          </div></router-link
        >

        <div class="item-line">
          <router-link to="/admin/request_success">
            <div class="item">
              <div class="item-header">已通过</div>
              <div class="item-body">
                <div class="item-number">{{ request.approved }}</div>

                <div class="item-unit">条</div>
              </div>
            </div>
          </router-link>
          <router-link to="/admin/request_reject">
            <div class="item">
              <div class="item-header">已拒绝</div>
              <div class="item-body">
                <div class="item-number">{{ request.rejected }}</div>
                <div class="item-unit">条</div>
              </div>
            </div>
          </router-link>
        </div>
        <router-link to="/admin/bulletin_draft"
          ><div class="bulletin">
            还有{{ board.draft }}条公告草稿待发布
          </div></router-link
        >

        <div class="item-line">
          <router-link to="/admin/bulletin_released">
            <div class="item">
              <div class="item-header">已发布</div>
              <div class="item-body">
                <div class="item-number">{{ board.released }}</div>
                <div class="item-unit">条</div>
              </div>
            </div>
          </router-link>

          <router-link to="/admin/bulletin_pulledOff">
            <div class="item">
              <div class="item-header">已下架</div>
              <div class="item-body">
                <div class="item-number">{{ board.pulledoff }}</div>
                <div class="item-unit">条</div>
              </div>
            </div>
          </router-link>
        </div>
      </div>
      <div class="two-right-container"></div>
    </div>
    <div class="three-container">
      <div class="three-left-container"></div>
      <div class="three-right-container">
        <div class="all-table-container" @click="exportAll">
          导出全量学籍表 <i class="iconfont icon-xiazai"></i>
        </div>
        <div class="img-table-container">
          <img src="../../../assets/imgs/table.png" alt="" />
        </div>
        <div class="detail-table-container">
          导出具体班级学籍表,
          <span class="query-container" @click="openDialog"
            >请添加条件查询 &gt;</span
          >
        </div>
      </div>
    </div>
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
  display: flex;
  flex-direction: column;
  padding: 20px 20px 120px;
  gap: 40px;
  font-family: 'dengxian light';
  .one-container {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 100px;
    .one-left-container {
      width: 562px;
      height: 400px;
      background: url(../../../assets/imgs/blue1.png) no-repeat center/cover;
    }
    .one-right-container {
      width: 562px;
      height: 400px;
    }
  }
  .two-container {
    display: flex;
    justify-content: space-around;
    align-items: center;
    .two-left-container {
      width: 562px;
      height: 400px;
    }
    .two-right-container {
      width: 562px;
      height: 400px;
      background: url(../../../assets/imgs/blue2.png) no-repeat center/cover;
    }
  }
  .three-container {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 100px;
    .three-left-container {
      width: 562px;
      height: 400px;
      background: url(../../../assets/imgs/blue3.png) no-repeat center/cover;
    }
    .three-right-container {
      width: 562px;
      height: 400px;
    }
  }
}
.one-right-container {
  background-color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40px;

  .welcome-container {
    margin-bottom: 10px;
    font-size: 24px;
    color: #666;
  }

  .back-container {
    margin-bottom: 40px;
    font-size: 36px;
    font-weight: 600;
    color: #333;
  }

  .time-container {
    margin-bottom: 20px;
    font-size: 30px;
    color: $mainColor;
  }

  .weather-container {
    font-size: 30px;
    color: $mainColor;
    display: flex;
    align-items: center;
  }
}
.two-left-container {
  padding: 60px 40px;
  .request,
  .bulletin {
    margin-bottom: 20px;
    font-size: 32px;
    cursor: pointer;
    &:hover {
      color: $mainColor;
    }
  }
  .item-line {
    display: flex;
    justify-content: start;
    padding-left: 10px;
    margin-bottom: 40px;
    gap: 120px;
    .item {
      display: flex;
      flex-direction: column;
      .item-header {
        position: relative;
        margin-bottom: 10px;
        text-indent: 12px;
        font-size: 20px;
        &::before {
          position: absolute;
          top: 0;
          left: -1px;
          content: '';
          width: 1px;
          height: 20px;
          background-color: #666;
        }
      }
      .item-body {
        display: flex;
        text-indent: 16px;
        cursor: pointer;
        .item-number {
          font-size: 32px;
        }
        .item-unit {
          align-self: center;
          transform: translateX(-12px) translateY(4px);
        }
        &:hover {
          color: $mainColor;
        }
      }
    }
  }
}
.three-right-container {
  padding: 40px;
  .all-table-container {
    margin-bottom: 20px;
    font-size: 32px;
    cursor: pointer;
    &:hover {
      color: $mainColor;
    }
    .iconfont {
      font-size: 28px;
    }
  }
  .img-table-container {
    width: 360px;
    margin-bottom: 20px;
    img {
      width: 100%;
      height: 100%;
    }
  }
  .detail-table-container {
    display: flex;
    justify-content: end;
    font-size: 24px;
    .query-container {
      cursor: pointer;
      &:hover {
        color: $mainColor;
      }
    }
  }
}
</style>
