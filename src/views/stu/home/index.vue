<template>
  <div class="student-home">
    <!-- 个人信息卡片 -->
    <div class="profile-card">
      <div class="profile-header">
        <img
          :src="userInfo.avatar || require('@/assets/imgs/default-avatar.png')"
          class="avatar"
          alt="头像"
        />
        <div class="profile-info">
          <h2>{{ userInfo.name }}</h2>
          <p class="student-id">学号: {{ userInfo.studentId }}</p>
          <div class="profile-meta">
            <span>🏫 {{ userInfo.college }}</span>
            <span>📚 {{ userInfo.major }}</span>
            <span>👥 {{ userInfo.class }}</span>
          </div>
        </div>
      </div>
      <div class="profile-details">
        <div class="detail-item">
          <span class="label">📱 手机:</span>
          <span>{{ userInfo.phone || '未填写' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">✉️ 邮箱:</span>
          <span>{{ userInfo.email || '未填写' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">📅 入学时间:</span>
          <span>{{ userInfo.enrollmentDate }}</span>
        </div>
      </div>
    </div>

    <!-- 公告展示区 -->
    <div class="announcement-section">
      <h3 class="section-title">📢 最新公告</h3>
      <div class="announcement-list">
        <div
          v-for="(announcement, index) in announcements"
          :key="index"
          class="announcement-card"
        >
          <div class="announcement-header">
            <span class="announcement-title">{{ announcement.title }}</span>
            <span class="announcement-date">{{
              formatDate(announcement.date)
            }}</span>
          </div>
          <div class="announcement-content">
            {{ announcement.content }}
          </div>
          <div class="announcement-footer">
            <span class="announcement-category">{{
              announcement.category
            }}</span>
            <span class="announcement-author"
              >发布者: {{ announcement.author }}</span
            >
          </div>
        </div>
      </div>
      <div v-if="announcements.length === 0" class="empty-announcement">
        暂无公告
      </div>
    </div>

    <!-- 快捷功能区 -->
    <div class="quick-actions">
      <h3 class="section-title">⚡ 快捷操作</h3>
      <div class="action-grid">
        <button class="action-btn" @click="navigateTo('friends')">
          <span class="action-icon">👥</span>
          <span>通讯录</span>
        </button>
        <button class="action-btn" @click="navigateTo('query')">
          <span class="action-icon">🔍</span>
          <span>信息查询</span>
        </button>
        <button class="action-btn" @click="navigateTo('course')">
          <span class="action-icon">📚</span>
          <span>选课系统</span>
        </button>
        <button class="action-btn" @click="navigateTo('library')">
          <span class="action-icon">📖</span>
          <span>图书馆</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

interface UserInfo {
  name: string
  studentId: string
  college: string
  major: string
  class: string
  phone?: string
  email?: string
  avatar?: string
  enrollmentDate: string
}

interface Announcement {
  title: string
  content: string
  date: string
  category: string
  author: string
}

const router = useRouter()

// 模拟用户数据
const userInfo = ref<UserInfo>({
  name: '谢融悠',
  studentId: '2023012345',
  college: '数学与计算机学院',
  major: '计算机科学与技术',
  class: '2023级1班',
  phone: '13800138000',
  email: 'xierongyou@example.com',
  enrollmentDate: '2023年9月',
})

// 模拟公告数据
const announcements = ref<Announcement[]>([
  {
    title: '关于期末考试安排的通知',
    content: '本学期期末考试将于2024年1月15日开始，请同学们提前做好准备...',
    date: '2023-12-20',
    category: '教务通知',
    author: '教务处',
  },
  {
    title: '寒假放假安排',
    content: '根据校历安排，寒假将于2024年1月22日开始，2月25日结束...',
    date: '2023-12-18',
    category: '学校通知',
    author: '校长办公室',
  },
  {
    title: '校园网维护通知',
    content: '为提升网络服务质量，校园网将于12月22日0:00-6:00进行维护...',
    date: '2023-12-15',
    category: '网络服务',
    author: '信息中心',
  },
])

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

// 导航功能
const navigateTo = (path: string) => {
  router.push(`/stu/${path}`)
}
</script>

<style scoped lang="scss">
.student-home {
  padding: 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto 1fr;
  gap: 20px;
  height: calc(100vh - 136px); // 减去header高度
}

.profile-card {
  grid-column: 1;
  grid-row: 1;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 20px;
  border: 2px solid #f0f0f0;
}

.profile-info h2 {
  margin: 0;
  font-size: 22px;
  color: #333;
}

.student-id {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.profile-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
  font-size: 14px;
  color: #555;
}

.profile-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.label {
  margin-right: 8px;
  color: #666;
}

.announcement-section {
  grid-column: 2;
  grid-row: 1 / span 2;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.section-title {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.announcement-card {
  padding: 15px;
  border-radius: 6px;
  background-color: #f9f9f9;
  transition: transform 0.2s;
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.announcement-title {
  font-weight: bold;
  color: #333;
}

.announcement-date {
  font-size: 12px;
  color: #888;
}

.announcement-content {
  font-size: 14px;
  color: #555;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.announcement-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #888;
}

.empty-announcement {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

.quick-actions {
  grid-column: 1;
  grid-row: 2;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px 10px;
  background-color: #f5f7fa;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  &:hover {
    background-color: #e1e5eb;
    transform: translateY(-2px);
  }
}

.action-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

@media (max-width: 1200px) {
  .student-home {
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto;
    height: auto;
  }

  .announcement-section {
    grid-column: 1;
    grid-row: 2;
  }

  .quick-actions {
    grid-row: 3;
  }
}
</style>
