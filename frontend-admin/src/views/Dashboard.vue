<template>
  <div class="dashboard-container">
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon users">
          <el-icon>User</el-icon>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.totalUsers }}</span>
          <span class="stat-label">总用户数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon courses">
          <el-icon>Document</el-icon>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.totalCourses }}</span>
          <span class="stat-label">总课程数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon posts">
          <el-icon>Message</el-icon>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.totalPosts }}</span>
          <span class="stat-label">总帖子数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon achievements">
          <el-icon>Medal</el-icon>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.totalAchievements }}</span>
          <span class="stat-label">总成就数</span>
        </div>
      </div>
    </div>

    <div class="charts-row">
      <div class="chart-card">
        <h3>用户增长趋势</h3>
        <div class="line-chart">
          <div class="chart-bars">
            <div
              v-for="(item, index) in userGrowth"
              :key="index"
              class="bar-item"
            >
              <div class="bar-wrapper">
                <div class="bar" :style="{ height: (item.value / maxUserValue * 100) + '%' }"></div>
              </div>
              <span class="bar-label">{{ item.label }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="chart-card">
        <h3>课程语言分布</h3>
        <div class="pie-chart">
          <div class="pie-container">
            <svg viewBox="0 0 100 100" class="pie-svg">
              <circle
                v-for="(item, index) in languageDistribution"
                :key="index"
                cx="50"
                cy="50"
                r="40"
                :fill="item.color"
                :stroke="index === 0 ? 'none' : '#fff'"
                :stroke-width="2"
                :transform="`rotate(${item.offset}, 50, 50)`"
                :style="{
                  clipPath: `polygon(50% 50%, 50% 0%, 100% 0%, 100% 100%, 0% 100%, 0% 0%, 50% 0%)`,
                  '--rotation': `${item.rotation}deg`
                }"
              />
            </svg>
          </div>
          <div class="legend">
            <div
              v-for="item in languageDistribution"
              :key="item.name"
              class="legend-item"
            >
              <span class="legend-color" :style="{ background: item.color }"></span>
              <span class="legend-text">{{ item.name }} ({{ item.percent }}%)</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="data-tables-row">
      <div class="table-card">
        <h3>最近用户</h3>
        <el-table :data="recentUsers" border>
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column prop="registerTime" label="注册时间" />
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
                {{ row.status === 'ACTIVE' ? '活跃' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="table-card">
        <h3>最近发布的帖子</h3>
        <el-table :data="recentPosts" border>
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="authorName" label="作者" />
          <el-table-column prop="createdAt" label="发布时间" />
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag type="success">已发布</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { adminUserApi, adminCourseApi, achievementApi, postApi } from '../api';

const stats = ref({
  totalUsers: 0,
  totalCourses: 0,
  totalPosts: 0,
  totalAchievements: 0
});

const userGrowth = ref([
  { label: '1月', value: 120 },
  { label: '2月', value: 150 },
  { label: '3月', value: 180 },
  { label: '4月', value: 220 },
  { label: '5月', value: 280 },
  { label: '6月', value: 320 }
]);

const languageDistribution = ref([
  { name: '英语', percent: 45, color: '#409EFF' },
  { name: '日语', percent: 30, color: '#67C23A' },
  { name: '韩语', percent: 25, color: '#E6A23C' }
]);

const recentUsers = ref([]);
const recentPosts = ref([]);

const maxUserValue = computed(() => {
  return Math.max(...userGrowth.value.map(d => d.value), 1);
});

// 模拟数据用于后端不可用时
const mockStats = {
  totalUsers: 1256,
  totalCourses: 48,
  totalPosts: 892,
  totalAchievements: 24
};

const mockRecentUsers = [
  { id: 1, username: '张三', email: 'zhangsan@example.com', registerTime: '2024-01-15', status: 'ACTIVE' },
  { id: 2, username: '李四', email: 'lisi@example.com', registerTime: '2024-01-14', status: 'ACTIVE' },
  { id: 3, username: '王五', email: 'wangwu@example.com', registerTime: '2024-01-13', status: 'INACTIVE' },
  { id: 4, username: '赵六', email: 'zhaoliu@example.com', registerTime: '2024-01-12', status: 'ACTIVE' },
  { id: 5, username: '钱七', email: 'qianqi@example.com', registerTime: '2024-01-11', status: 'ACTIVE' }
];

const mockRecentPosts = [
  { id: 1, title: '如何快速学习英语口语', authorName: '张三', createdAt: '2024-01-15', status: 'PUBLISHED' },
  { id: 2, title: '日语学习心得分享', authorName: '李四', createdAt: '2024-01-14', status: 'PUBLISHED' },
  { id: 3, title: '韩语发音技巧', authorName: '王五', createdAt: '2024-01-13', status: 'PUBLISHED' },
  { id: 4, title: '语言学习方法论', authorName: '赵六', createdAt: '2024-01-12', status: 'PUBLISHED' },
  { id: 5, title: '多语言学习经验', authorName: '钱七', createdAt: '2024-01-11', status: 'PUBLISHED' }
];

const loadStats = async () => {
  try {
    const users = await adminUserApi.getAll({ page: 1, size: 1 });
    const courses = await adminCourseApi.getAll({ page: 1, size: 1 });
    const achievements = await achievementApi.getAll();
    const posts = await postApi.getAll({ page: 1, size: 1 });
    stats.value = {
      totalUsers: users.total || 0,
      totalCourses: courses.total || 0,
      totalPosts: posts.total || 0,
      totalAchievements: achievements.length || 0
    };
  } catch (error) {
    console.error('加载统计数据失败，使用模拟数据', error);
    // 使用模拟数据
    stats.value = mockStats;
  }
};

const loadRecentUsers = async () => {
  try {
    const result = await adminUserApi.getAll({ page: 1, size: 5 });
    recentUsers.value = result.records || [];
  } catch (error) {
    console.error('加载用户失败，使用模拟数据', error);
    // 使用模拟数据
    recentUsers.value = mockRecentUsers;
  }
};

const loadRecentPosts = async () => {
  try {
    const result = await postApi.getAll({ page: 1, size: 5 });
    recentPosts.value = result.records || [];
  } catch (error) {
    console.error('加载帖子失败，使用模拟数据', error);
    // 使用模拟数据
    recentPosts.value = mockRecentPosts;
  }
};

onMounted(async () => {
  await loadStats();
  await loadRecentUsers();
  await loadRecentPosts();
});
</script>

<style scoped>
.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.stat-icon.users {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.courses {
  background: linear-gradient(135deg, #409EFF 0%, #66B1FF 100%);
}

.stat-icon.posts {
  background: linear-gradient(135deg, #67C23A 0%, #85CE61 100%);
}

.stat-icon.achievements {
  background: linear-gradient(135deg, #E6A23C 0%, #F0C78A 100%);
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.charts-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.chart-card h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 20px;
}

.line-chart {
  height: 200px;
}

.chart-bars {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 100%;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.bar-wrapper {
  width: 40px;
  height: 150px;
  background: #f5f5f5;
  border-radius: 4px;
  display: flex;
  align-items: flex-end;
}

.bar {
  width: 100%;
  background: linear-gradient(to top, #409EFF, #66B1FF);
  border-radius: 4px;
  transition: height 0.5s;
}

.bar-label {
  font-size: 12px;
  color: #666;
}

.pie-chart {
  display: flex;
  align-items: center;
  gap: 30px;
}

.pie-container {
  flex-shrink: 0;
}

.pie-svg {
  width: 120px;
  height: 120px;
}

.legend {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.legend-text {
  font-size: 14px;
  color: #666;
}

.data-tables-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.table-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.table-card h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 20px;
}
</style>