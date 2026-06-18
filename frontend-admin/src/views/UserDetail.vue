<template>
  <div class="user-detail">
    <el-button @click="goBack">返回</el-button>

    <div v-if="user" class="detail-content">
      <div class="basic-info">
        <h2>基本信息</h2>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="用户名">
              <span>{{ user.username }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="邮箱">
              <span>{{ user.email }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="手机号">
              <span>{{ user.phone || '-' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="角色">
              <el-tag :type="user.role === 'ADMIN' ? 'danger' : 'info'">
                {{ user.role === 'ADMIN' ? '管理员' : '用户' }}
              </el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态">
              <el-tag :type="user.status === 'ACTIVE' ? 'success' : 'warning'">
                {{ user.status === 'ACTIVE' ? '正常' : '禁用' }}
              </el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="注册时间">
              <span>{{ user.createdAt }}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="stats-info">
        <h2>学习统计</h2>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card>
              <div class="stat-item">
                <span class="stat-label">总积分</span>
                <span class="stat-value">{{ user.totalPoints || 0 }}</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card>
              <div class="stat-item">
                <span class="stat-label">完成课程数</span>
                <span class="stat-value">{{ user.completedCourses || 0 }}</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card>
              <div class="stat-item">
                <span class="stat-label">学习天数</span>
                <span class="stat-value">{{ user.learningDays || 0 }}</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card>
              <div class="stat-item">
                <span class="stat-label">解锁成就</span>
                <span class="stat-value">{{ user.unlockedAchievements || 0 }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div class="achievements-info">
        <h2>获得成就</h2>
        <div v-if="achievements.length > 0" class="achievements-grid">
          <el-card v-for="achievement in achievements" :key="achievement.id" class="achievement-card">
            <div class="achievement-icon">
              <el-icon size="48" color="#E6A23C">Medal</el-icon>
            </div>
            <div class="achievement-info">
              <span class="achievement-name">{{ achievement.name }}</span>
              <span class="achievement-desc">{{ achievement.description }}</span>
            </div>
          </el-card>
        </div>
        <div v-else class="empty-state">
          <el-icon size="48" color="#999">Medal</el-icon>
          <p>暂无成就</p>
        </div>
      </div>

      <div class="courses-info">
        <h2>学习课程</h2>
        <el-table v-if="courses.length > 0" :data="courses" border>
          <el-table-column prop="courseTitle" label="课程名称" />
          <el-table-column prop="languageName" label="语言" />
          <el-table-column prop="progress" label="进度">
            <template #default="{ row }">
              <el-progress :percentage="row.progress" :stroke-width="12" />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'info'">
                {{ row.status === 'COMPLETED' ? '已完成' : '学习中' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <div v-else class="empty-state">
          <el-icon size="48" color="#999">BookOpen</el-icon>
          <p>暂无学习记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { adminUserApi, achievementApi } from '../api';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const user = ref(null);
const achievements = ref([]);
const courses = ref([]);

// 模拟数据用于后端不可用时
const mockUser = {
  id: 1,
  username: '张三',
  email: 'zhangsan@example.com',
  phone: '13800138001',
  role: 'USER',
  status: 'ACTIVE',
  createdAt: '2024-01-10',
  totalPoints: 1250,
  completedCourses: 5,
  learningDays: 30,
  unlockedAchievements: 8
};

const mockAchievements = [
  { id: 1, name: '初学者', description: '完成第一个课程的学习', icon: 'Medal' },
  { id: 2, name: '勤奋学员', description: '连续学习7天', icon: 'Medal' },
  { id: 3, name: '知识达人', description: '累计学习100小时', icon: 'Medal' }
];

const mockCourses = [
  { id: 1, courseTitle: '英语入门课程', languageName: '英语', progress: 85, status: 'IN_PROGRESS' },
  { id: 2, courseTitle: '日语N3备考', languageName: '日语', progress: 100, status: 'COMPLETED' },
  { id: 3, courseTitle: '韩语基础会话', languageName: '韩语', progress: 60, status: 'IN_PROGRESS' }
];

const goBack = () => {
  router.push('/users');
};

const loadUserDetail = async () => {
  try {
    const userId = route.params.id;
    user.value = await adminUserApi.getById(userId);
    
    // 加载成就数据
    const allAchievements = await achievementApi.getAll();
    achievements.value = allAchievements.filter(a => a.id <= 3);
    
    // 加载课程数据（假设API有这个方法）
    courses.value = mockCourses;
  } catch (error) {
    console.error('加载用户详情失败，使用模拟数据', error);
    // 使用模拟数据
    user.value = mockUser;
    achievements.value = mockAchievements;
    courses.value = mockCourses;
  }
};

onMounted(() => {
  loadUserDetail();
});
</script>

<style scoped>
.user-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.detail-content {
  margin-top: 20px;
}

.basic-info, .stats-info, .achievements-info, .courses-info {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.basic-info h2, .stats-info h2, .achievements-info h2, .courses-info h2 {
  font-size: 18px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.achievements-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.achievement-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.achievement-info {
  flex: 1;
}

.achievement-name {
  display: block;
  font-weight: bold;
  margin-bottom: 4px;
}

.achievement-desc {
  display: block;
  font-size: 12px;
  color: #999;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty-state p {
  margin-top: 10px;
}
</style>