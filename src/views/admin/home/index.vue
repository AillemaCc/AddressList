<template>
  <div class="dashboard-container">
    <!-- 欢迎区域 -->
    <div class="welcome-card">
      <h2>欢迎回来，系统管理员1 👋</h2>
      <p>
        今天是 {{ currentDate }}，您有 {{ pendingCount }} 条待审核请求需要处理
      </p>
    </div>

    <!-- 数据概览卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card">
        <div class="stat-content">
          <div>
            <h3>待审核请求</h3>
            <p class="number">{{ pendingCount }}</p>
          </div>
        </div>
        <el-button type="text" @click="$router.push('/admin/request_fail')"
          >查看详情</el-button
        >
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div>
            <h3>已发布公告</h3>
            <p class="number">{{ publishedCount }}</p>
          </div>
        </div>
        <el-button type="text" @click="$router.push('/admin/bulletin_released')"
          >查看详情</el-button
        >
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div>
            <h3>用户查询量</h3>
            <p class="number">{{ queryCount }}</p>
          </div>
        </div>
        <el-button type="text" @click="$router.push('/admin/query')"
          >查看详情</el-button
        >
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div>
            <h3>系统状态</h3>
            <p class="number" :class="systemStatus.class">
              {{ systemStatus.text }}
            </p>
          </div>
        </div>
        <el-button type="text">查看详情</el-button>
      </el-card>
    </div>

    <!-- 快捷操作区域 -->
    <el-card class="quick-actions">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
        </div>
      </template>
      <div class="action-buttons">
        <el-button
          type="primary"
          @click="$router.push('/admin/bulletin_draft')"
        >
          新建公告
        </el-button>
        <el-button @click="$router.push('/admin/query')"> 信息查询 </el-button>
        <el-button @click="$router.push('/admin/request_fail')">
          审核请求
        </el-button>
      </div>
    </el-card>

    <!-- 最近活动 -->
    <div class="recent-activity">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>最近活动</span>
          </div>
        </template>
        <el-timeline>
          <el-timeline-item
            v-for="(activity, index) in recentActivities"
            :key="index"
            :timestamp="activity.time"
            placement="top"
          >
            <el-card shadow="hover">
              <p>{{ activity.content }}</p>
              <p class="activity-user">
                {{ activity.user }} · {{ activity.action }}
              </p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 模拟数据
const pendingCount = ref(5)
const publishedCount = ref(12)
const queryCount = ref(28)

const systemStatus = computed(() => {
  return {
    text: '运行正常',
    class: 'status-normal',
    color: '#67C23A',
  }
})

const currentDate = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`
})

const recentActivities = ref([
  {
    time: '2023-05-15 14:30',
    content: '用户张三更新了个人信息',
    user: '张三',
    action: '信息更新',
  },
  {
    time: '2023-05-15 13:45',
    content: '发布了新公告《系统维护通知》',
    user: '系统管理员1',
    action: '公告发布',
  },
  {
    time: '2023-05-15 10:20',
    content: '通过了李四的认证请求',
    user: '系统管理员1',
    action: '请求审核',
  },
  {
    time: '2023-05-14 16:15',
    content: '下架了过期公告《五一放假通知》',
    user: '系统管理员1',
    action: '公告管理',
  },
])
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.welcome-card {
  background: linear-gradient(135deg, #409eff 0%, #79bbff 100%);
  color: white;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 20px;

  h2 {
    margin: 0 0 8px 0;
    font-size: 24px;
  }

  p {
    margin: 0;
    opacity: 0.9;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  :deep(.el-card__body) {
    padding: 16px;
  }

  .stat-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    h3 {
      margin: 0 0 8px 0;
      font-size: 16px;
      color: #606266;
    }

    .number {
      margin: 0;
      font-size: 28px;
      font-weight: bold;
      color: #303133;
    }

    .status-normal {
      color: #67c23a;
    }

    .status-warning {
      color: #e6a23c;
    }

    .status-error {
      color: #f56c6c;
    }
  }
}

.quick-actions {
  margin-bottom: 20px;

  .card-header {
    font-weight: bold;
    font-size: 18px;
  }

  .action-buttons {
    display: flex;
    gap: 12px;

    .el-button {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      padding: 12px;
    }
  }
}

.recent-activity {
  .activity-user {
    margin-top: 8px;
    font-size: 12px;
    color: #909399;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>
