<script setup>
import { stuCreateAddressApi, stuUpdateAddressApi } from '@/apis/stu/friends'
import { stuHomePageInfoApi } from '@/apis/stu/other'
import { useStuInfoStore } from '@/stores/stuInfo'
import { animation } from '@/utils/animation'
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import noimg from '@/assets/imgs/noimg.png'
import { adminDisplayReleasedApi } from '@/apis/admin/bulletin'

const router = useRouter()
const stuInfoStore = useStuInfoStore()
const studentId = stuInfoStore.stuInfo.studentId
const info = ref({})
const details = ref([])
const targetValues = ref({})
const operation = ref(1)
const dialogVisible = ref(false)

async function getInfo() {
  const res = await stuHomePageInfoApi({
    studentId: studentId.value,
  })
  if (res.success) {
    info.value = res.data.stuInfo
    targetValues.value = res.data.dataStatistic
    for (let i in targetValues.value) {
      animation(0, targetValues.value[i], 800, (value) => {
        targetValues.value[i] = Math.floor(value)
      })
    }
  } else {
    ElMessage.error(res.message)
  }
}
getInfo()
function moreBulletin() {
  if (details.length > 0) {
    router.push(`/stu/bulletin/detail?id=${details[0].boardId}`)
  } else {
    ElMessage.error('暂无公告')
  }
}
async function getDetails() {
  const res = await adminDisplayReleasedApi({
    current: 1,
    size: 10,
  })
  if (res.success) {
    details.value = res.data.records
    console.log(res.data.records)

    if (details.value.length > 7) {
      details.value = details.value.slice(0, 7)
    }
  }
}
getDetails()
console.log(details.value)

function create() {
  operation.value = 1
  info.value.employer = ''
  info.value.city = ''
  dialogVisible.value = true
}
function edit() {
  operation.value = 2
  dialogVisible.value = true
}
async function submit() {
  if (operation.value == 1) {
    if (info.value.employer !== '' && info.value.city !== '') {
      const res = await stuCreateAddressApi({
        studentId: studentId.value,
        employer: info.value.employer,
        city: info.value.city,
      })
      if (res.success) {
        ElMessage.success(res.message)
        dialogVisible.value = false
      } else {
        ElMessage.error(res.message)
      }
    } else {
      ElMessage.error('就业单位或就业城市不能为空')
    }
  } else {
    if (info.value.employer !== '' && info.value.city !== '') {
      const res = await stuUpdateAddressApi({
        studentId: studentId.value,
        employer: info.value.employer,
        city: info.value.city,
      })
      if (res.success) {
        ElMessage.success(res.message)
        dialogVisible.value = false
      } else {
        ElMessage.error(res.message)
      }
    } else {
      ElMessage.error('就业单位或就业城市不能为空')
    }
  }
}
</script>
<template>
  <div class="dialog-container">
    <el-dialog v-model="dialogVisible" title="编辑自己对外的通讯信息">
      <div class="dialog-body-container">
        <div class="reject-item">
          <div class="item-header">就业单位</div>
          <div class="item-body">
            <el-input class="reject" v-model="info.employer" />
          </div>
        </div>
        <div class="reject-item">
          <div class="item-header">就业城市</div>
          <div class="item-body">
            <el-input class="reject" v-model="info.city" />
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
  <div class="container">
    <div class="one-container">
      <div class="bulletin-container">
        <div class="bulletin-container-header">
          <div class="left">
            <div class="icon-container">
              <i class="iconfont icon-gonggaolan"></i>
            </div>
            公告栏
          </div>
          <div class="right" @click="moreBulletin">更多></div>
        </div>
        <div class="bulletin-container-body" v-if="details.length > 0">
          <div
            class="bulletin-item"
            v-for="item in details"
            :key="item.boardId"
          >
            <div class="title">{{ item.title }}</div>
            <div class="time">{{ item.createTime.slice(0, 10) }}</div>
          </div>
        </div>
        <div class="bulletin-container-body" v-else>
          <el-empty description="暂无公告数据" />
        </div>
      </div>
      <div class="banner-container">
        <div class="block text-center">
          <el-carousel trigger="click" height="500px">
            <!-- <el-carousel-item v-for="item in 5" :key="item">
              <div class="box-container">
                <div class="bulletin-title-container">
                  {{
                    '测试公告标题' +
                    `${item}` +
                    '公告标题' +
                    `${item}` +
                    '测试标题' +
                    `${item}`
                  }}
                </div>
                <img :src="`https://picsum.photos/1920/1080?${item}`" alt="" />
              </div>
            </el-carousel-item> -->
            <el-carousel trigger="click" height="500px">
              <div v-if="details.length > 0">
                <el-carousel-item v-for="item in details" :key="item.boardId">
                  <div class="box-container">
                    <div class="bulletin-title-container">{{ item.title }}</div>
                    <img :src="item.coverImg" alt="" />
                  </div>
                </el-carousel-item>
              </div>
              <div v-else>
                <el-carousel-item>
                  <div class="box-container">
                    <div class="bulletin-title-container">暂无公告</div>
                    <img :src="noimg" alt="" />
                  </div>
                </el-carousel-item>
              </div>
            </el-carousel>
          </el-carousel>
        </div>
      </div>
    </div>
    <div class="two-container">
      <div class="information-container">
        <div class="information-header">
          <div class="left">
            <div class="icon-container">
              <i class="iconfont icon-zuoxixingming"></i>
            </div>
            个人信息
          </div>
          <div class="right">
            <span @click="create">新增</span><span @click="edit">编辑</span>
          </div>
        </div>
        <div class="information-body">
          <div class="information-item">
            <div class="item-header">学号</div>
            <div class="item-body">{{ info.studentId }}</div>
          </div>
          <div class="information-item">
            <div class="item-header">姓名</div>
            <div class="item-body">{{ info.name }}</div>
          </div>
          <div class="information-item">
            <div class="item-header">电话</div>
            <div class="item-body">{{ info.phone }}</div>
          </div>
          <div class="information-item">
            <div class="item-header">邮箱</div>
            <div class="item-body">{{ info.email }}</div>
          </div>
          <div class="information-item">
            <div class="item-header">学院</div>
            <div class="item-body">{{ info.academy }}</div>
          </div>
          <div class="information-line">
            <div class="information-item">
              <div class="item-header">专业</div>
              <div class="item-body">{{ info.major }}</div>
            </div>
            <div class="information-item">
              <div class="item-header">班级</div>
              <div class="item-body">{{ info.className }}</div>
            </div>
          </div>

          <div class="information-line">
            <div class="information-item">
              <div class="item-header">入学年份</div>
              <div class="item-body">{{ info.enrollmentYear }}</div>
            </div>
            <div class="information-item">
              <div class="item-header">毕业年份</div>
              <div class="item-body">{{ info.graduationYear }}</div>
            </div>
          </div>
          <div class="information-line">
            <div class="information-item">
              <div class="item-header">就业单位</div>
              <div class="item-body">{{ info.employer }}</div>
            </div>
            <div class="information-item">
              <div class="item-header">就业城市</div>
              <div class="item-body">{{ info.city }}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="data-container">
        <div class="data-header">
          <div class="left">
            <div class="icon-container">
              <i class="iconfont icon-shujuzhanshi"></i>
            </div>
            数据统计
          </div>
        </div>
        <div class="data-body">
          <div class="data-item">
            <div class="data-label">通讯信息总数</div>
            <div class="data-value">{{ targetValues.totalContacts }}位</div>
          </div>
          <div class="data-item highlight">
            <div class="data-label">待处理请求</div>
            <div class="data-value">
              <router-link to="/stu/fail"
                >{{ targetValues.pendingReply }}条</router-link
              >
            </div>
          </div>
          <div class="data-stats">
            <div class="stat-line">
              <div class="stat-item">
                <div class="stat-label">已发送</div>
                <div class="stat-value">
                  <div class="stat-num">{{ targetValues.sent }}</div>
                  条
                </div>
              </div>
            </div>
            <div class="stat-line">
              <div class="stat-item">
                <div class="stat-label">待回复</div>
                <div class="stat-value">
                  <div class="stat-num">{{ targetValues.pendingReply }}</div>
                  条
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-label">已通过</div>
                <div class="stat-value">
                  <div class="stat-num">{{ targetValues.approved }}</div>
                  条
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-label">已拒绝</div>
                <div class="stat-value">
                  <div class="stat-num">{{ targetValues.rejected }}</div>
                  条
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-label">已删除</div>
                <div class="stat-value">
                  <div class="stat-num">{{ targetValues.deleted }}</div>
                  条
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
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
  .one-container {
    margin: 20px auto 50px;
    width: 80vw; // 增加最大宽度限制
    max-width: 1400px;
    min-width: 800px;
    height: auto;
    display: flex;

    .bulletin-container {
      flex: 1;
    }
    .banner-container {
      flex: 4;
    }
  }
  .two-container {
    margin: 20px auto 50px;
    width: 80vw;
    max-width: 1400px; // 增加最大宽度限制
    min-width: 800px;
    height: auto;
    display: flex;
    gap: 20px;
    .information-container {
      flex: 1;
    }
    .data-container {
      flex: 1;
    }
  }
}
.bulletin-container {
  display: flex;
  flex-direction: column;
  background: #fff;
  flex: 1;
  min-width: 300px;
  border: 1px solid #ddd;
  padding: 20px;
  .bulletin-container-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 2px 10px;
    .left {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 8px;
      font-size: 20px;
      color: #f88649;
      .icon-container {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 40px;
        height: 40px;
        background-color: #f88649;
        border-radius: 8px;
        .iconfont {
          font-size: 24px;
          color: #fff;
        }
      }
    }
    .right {
      cursor: pointer;
      &:hover {
        color: #f88649;
      }
    }
  }
  .bulletin-container-body {
    margin-top: 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: start;
    gap: 2px;
    .bulletin-item {
      display: flex;
      justify-content: space-between;
      gap: 4px;
      padding: 16px 8px;
      border-radius: 4px;
      cursor: pointer;
      &:hover {
        color: #f88649;
      }
      &:hover .time {
        color: #f88649;
      }
      .title {
        flex: 1;
        font-size: 16px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      .time {
        min-width: 80px;
        font-size: 14px;
        color: #aaa;
      }
    }
  }
}
.banner-container {
  margin: 0 auto;
  width: 80vw;
  height: 500px;
  :deep(.el-carousel__item) {
    background-color: transparent !important;
  }
  .el-carousel__item {
    .box-container {
      width: 100%;
      height: 100%;
      position: relative;
      overflow: hidden;
      .bulletin-title-container {
        position: absolute;
        bottom: 8%;
        left: 4%;
        font-weight: 600;
        font-size: 30px;
        text-shadow:
          0 1px 3px rgba(0, 0, 0, 0.8),
          1px 1px 1px rgba(255, 255, 255, 0.3);
        color: white;
      }
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        object-position: center;
      }
    }

    &:nth-child(2n) {
      background-color: #99a9bf;
    }
    &:nth-child(2n + 1) {
      background-color: #d3dce6;
    }
  }
}

.information-container {
  display: flex;
  flex-direction: column;
  background: #fff;
  flex: 1;
  min-width: 300px;
  border: 1px solid #ddd;
  padding: 20px;
  .information-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 2px 10px;
    .left {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 8px;
      font-size: 20px;
      color: #67b3fb;
      .icon-container {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 40px;
        height: 40px;
        background-color: #67b3fb;
        border-radius: 8px;
        .iconfont {
          font-size: 24px;
          color: #fff;
        }
      }
    }
    .right {
      cursor: pointer;
      display: flex;
      gap: 10px;
      span {
        &:hover {
          color: #67b3fb;
        }
      }
    }
  }
  .information-body {
    margin-top: 8px;
    .information-line {
      display: flex;
      justify-content: center;
      align-items: center;
      @media (max-width: 1240px) {
        flex-direction: column;
        align-items: start;
      }
      .information-item {
        flex: 1;
      }
    }
    .information-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 5px;
      font-size: 16px;
      gap: 10px;
      .item-header {
        width: 80px;
      }
      .item-body {
        flex: 1;
      }
    }
  }
}
.data-container {
  display: flex;
  flex-direction: column;
  background: #fff;
  flex: 1;
  min-width: 300px;
  border: 1px solid #ddd;
  padding: 20px;

  .data-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 2px 10px;
    font-size: 20px;
    color: $mainColor;
    .left {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 8px;
      font-size: 20px;
      color: #3dda67;
      .icon-container {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 40px;
        height: 40px;
        background-color: #3dda67;
        border-radius: 8px;
        .iconfont {
          font-size: 24px;
          color: #fff;
        }
      }
    }
  }
  .data-body {
    padding: 16px 20px;

    .data-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 18px 0;
      border-bottom: 1px dashed #eee;

      &.highlight {
        .data-label {
          color: #3dda67;
        }
        .data-value a {
          color: #3dda67;
        }
      }

      &:last-child {
        border-bottom: none;
      }

      .data-label {
        font-size: 15px;
        color: #666;
      }

      .data-value {
        font-size: 16px;
        font-weight: 500;
        color: #333;
      }
    }

    .data-stats {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-top: 36px;
      .stat-line {
        display: flex;
        justify-content: space-between;
        @media (max-width: 1240px) {
          flex-wrap: wrap; // 允许换行
          gap: 20px; // 设置元素间距

          .stat-item {
            width: calc(50% - 10px); // 每行两个，考虑gap间距

            &:nth-child(2n) {
              margin-right: 0; // 每行第二个元素清除右边距
            }
          }
        }
        .stat-item {
          width: 100px;
          text-align: center;
          padding: 8px;
          border-radius: 6px;

          .stat-label {
            position: relative;
            font-size: 13px;
            color: #888;
            margin-bottom: 4px;
            &::before {
              content: '';
              position: absolute;
              top: 0;
              left: 8px;
              width: 1px;
              height: 100%;
              background-color: #666;
            }
          }

          .stat-value {
            display: flex;
            justify-content: center;
            align-items: end;
            .stat-num {
              font-size: 30px;
              transform: translateX(-2px) translateY(2px);
            }
            font-size: 16px;
            font-weight: 500;
            color: #333;
          }
        }
      }
    }
  }
}
</style>
