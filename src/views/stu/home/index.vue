<script setup>
const info = {
  studentId: '9109234567',
  name: 'xry',
  major: '计算机科学与技术',
  class: '2020级3班',
  entranceYear: '2020',
  graduationYear: '2024',
  employer: 'XX科技有限公司',
  city: '上海',
  phone: '13800138000',
  email: 'student@university.edu.cn',
}
</script>
<template>
  <div class="container">
    <div class="banner-container">
      <div class="block text-center">
        <el-carousel trigger="click" height="70vh">
          <el-carousel-item v-for="item in 5" :key="item">
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
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>
    <div class="info-container">
      <div class="bulletin-container">
        <div class="bulletin-container-header">
          <div class="left">公告栏</div>
          <div class="right">更多></div>
        </div>
        <div class="bulletin-container-body">
          <div class="bulletin-item">
            <div class="title">测试公告标题ceshitiaobi123</div>
            <div class="time">2025-05-26</div>
          </div>
          <div class="bulletin-item">
            <div class="title">
              测试公告标题1111111111111111111111ceshitiaobi123
            </div>
            <div class="time">2025-05-26</div>
          </div>
          <div class="bulletin-item">
            <div class="title">测试公告wwwwwwwwwwww22www标题ceshitiaobi123</div>
            <div class="time">2025-05-26</div>
          </div>
          <div class="bulletin-item">
            <div class="title">
              测试公告标22222222222222222222222222222题ceshitiaobi123
            </div>
            <div class="time">2025-05-26</div>
          </div>
          <div class="bulletin-item">
            <div class="title">测试公告标题ceshitiaobi123</div>
            <div class="time">2025-05-26</div>
          </div>
        </div>
      </div>
      <div class="information-container">
        <div class="information-header">
          <div class="left">个人信息</div>
          <div class="right">编辑></div>
        </div>
        <div class="information-body">
          <div class="information-item">
            <div class="item-header">学号</div>
            <div class="item-body">9109234567</div>
          </div>
          <div class="information-item">
            <div class="item-header">姓名</div>
            <div class="item-body">xry</div>
          </div>
          <div class="information-item">
            <div class="item-header">专业</div>
            <div class="item-body">计算机科学与技术</div>
          </div>
          <div class="information-item">
            <div class="item-header">班级</div>
            <div class="item-body">2020级3班</div>
          </div>
          <div class="information-item">
            <div class="item-header">入学年份</div>
            <div class="item-body">2020</div>
          </div>
          <div class="information-item">
            <div class="item-header">毕业年份</div>
            <div class="item-body">2024</div>
          </div>
          <div class="information-item">
            <div class="item-header">就业单位</div>
            <div class="item-body">XX科技有限公司</div>
          </div>
          <div class="information-item">
            <div class="item-header">就业城市</div>
            <div class="item-body">上海</div>
          </div>
          <div class="information-item">
            <div class="item-header">电话</div>
            <div class="item-body">13800138000</div>
          </div>
          <div class="information-item">
            <div class="item-header">邮箱</div>
            <div class="item-body">student@university.edu.cn</div>
          </div>
        </div>
      </div>
      <div class="data-container">
        <div class="data-header">数据统计</div>
        <div class="data-body">
          <div class="data-item">
            <div class="data-label">通讯信息总数</div>
            <div class="data-value">20位</div>
          </div>
          <div class="data-item highlight">
            <div class="data-label">待处理请求</div>
            <div class="data-value">
              <router-link to="/stu/fail">3条</router-link>
            </div>
          </div>
          <div class="data-stats">
            <div class="stat-line">
              <div class="stat-item">
                <div class="stat-label">已发送</div>
                <div class="stat-value">
                  <div class="stat-num">12</div>
                  条
                </div>
              </div>
            </div>
            <div class="stat-line">
              <div class="stat-item">
                <div class="stat-label">待回复</div>
                <div class="stat-value">
                  <div class="stat-num">3</div>
                  条
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-label">已通过</div>
                <div class="stat-value">
                  <div class="stat-num">20</div>
                  条
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-label">已拒绝</div>
                <div class="stat-value">
                  <div class="stat-num">0</div>
                  条
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-label">已删除</div>
                <div class="stat-value">
                  <div class="stat-num">1</div>
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
.container {
  .banner-container {
    margin: 0 auto;
    width: 80vw;
    height: 70vh;
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
  .info-container {
    margin: 20px auto 50px;
    width: 80vw; // 增加最大宽度限制
    height: auto;
    display: flex;
    flex-wrap: wrap;
    gap: 20px;

    > div {
      flex: 1 1 calc(33.333% - 20px);
      min-width: 300px;
      border: 1px solid #ddd;
      border-radius: 8px;
      padding: 20px;
      background: #fff;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;
    }

    @media (max-width: 1440px) {
      > div {
        flex: 1 1 calc(50% - 20px);
      }
    }

    @media (max-width: 1020px) {
      > div {
        flex: 1 1 100%;
      }
    }
  }
}
.bulletin-container {
  display: flex;
  flex-direction: column;
  .bulletin-container-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 2px 10px;
    border-bottom: 1px solid #aaa;
    .left {
      font-size: 20px;
      color: $mainColor;
    }
    .right {
      cursor: pointer;
      &:hover {
        color: $mainColor;
      }
    }
  }
  .bulletin-container-body {
    margin-top: 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    gap: 2px;
    .bulletin-item {
      display: flex;
      justify-content: space-between;
      padding: 16px 8px;
      border-radius: 4px;
      cursor: pointer;
      &:hover {
        color: $mainColor;
        background-color: #eee;
      }
      &:hover .time {
        color: $mainColor;
      }
      .title {
        flex: 1;
        max-width: 400px;
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
.information-container {
  display: flex;
  flex-direction: column;
  .information-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 2px 10px;
    border-bottom: 1px solid #aaa;
    .left {
      font-size: 20px;
      color: $mainColor;
    }
    .right {
      cursor: pointer;
      &:hover {
        color: $mainColor;
      }
    }
  }
  .information-body {
    margin-top: 8px;
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
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  .data-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 2px 10px;
    border-bottom: 1px solid #999;
    font-size: 20px;
    color: $mainColor;
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
        font-weight: 600;
        color: $mainColor;
      }
      .data-value a {
        color: #ff6b6b;
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
            font-size: 24px;
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
</style>
