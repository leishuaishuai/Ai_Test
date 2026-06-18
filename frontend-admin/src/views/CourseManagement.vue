<template>
  <div class="course-management">
    <div class="toolbar">
      <el-button type="primary" @click="$router.push('/courses/create')">
        <el-icon>Plus</el-icon>
        创建课程
      </el-button>
      <div class="search-box">
        <el-input v-model="searchKeyword" placeholder="搜索课程" @keyup.enter="loadCourses" />
        <el-button @click="loadCourses">搜索</el-button>
      </div>
    </div>

    <el-table :data="courses" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="课程名称" />
      <el-table-column prop="languageName" label="语言" />
      <el-table-column prop="level" label="难度">
        <template #default="{ row }">
          <el-tag :type="getLevelTagType(row.level)">
            {{ getLevelText(row.level) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="chapterCount" label="章节数" width="100" />
      <el-table-column prop="totalHours" label="学时" width="100" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'warning'">
            {{ row.status === 'ACTIVE' ? '上线' : '下线' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button @click="editCourse(row.id)">编辑</el-button>
          <el-button @click="toggleStatus(row)" :type="row.status === 'ACTIVE' ? 'warning' : 'success'">
            {{ row.status === 'ACTIVE' ? '下线' : '上线' }}
          </el-button>
          <el-button @click="deleteCourse(row.id)" type="danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @current-change="handlePageChange"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next, jumper, ->, total"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { adminCourseApi } from '../api';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const courses = ref([]);
const searchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 模拟数据用于后端不可用时
const mockCourses = [
  { id: 1, title: '英语入门课程', languageName: '英语', level: 'BEGINNER', chapterCount: 12, totalHours: 24, status: 'ACTIVE', createdAt: '2024-01-10' },
  { id: 2, title: '日语N3备考', languageName: '日语', level: 'INTERMEDIATE', chapterCount: 18, totalHours: 36, status: 'ACTIVE', createdAt: '2024-01-08' },
  { id: 3, title: '韩语基础会话', languageName: '韩语', level: 'BEGINNER', chapterCount: 10, totalHours: 20, status: 'ACTIVE', createdAt: '2024-01-05' },
  { id: 4, title: '商务英语进阶', languageName: '英语', level: 'ADVANCED', chapterCount: 15, totalHours: 30, status: 'INACTIVE', createdAt: '2024-01-03' },
  { id: 5, title: '日语N2冲刺', languageName: '日语', level: 'INTERMEDIATE', chapterCount: 20, totalHours: 40, status: 'ACTIVE', createdAt: '2024-01-01' }
];

const getLevelText = (level) => {
  const levels = { 'BEGINNER': '入门', 'INTERMEDIATE': '中级', 'ADVANCED': '高级' };
  return levels[level] || level;
};

const getLevelTagType = (level) => {
  const types = { 'BEGINNER': 'success', 'INTERMEDIATE': 'warning', 'ADVANCED': 'danger' };
  return types[level] || 'info';
};

const loadCourses = async () => {
  try {
    const result = await adminCourseApi.getAll({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    });
    courses.value = result.records || [];
    total.value = result.total || 0;
  } catch (error) {
    console.error('加载课程失败，使用模拟数据', error);
    // 使用模拟数据
    let filtered = mockCourses;
    if (searchKeyword.value) {
      filtered = mockCourses.filter(c => 
        c.title.includes(searchKeyword.value) || 
        c.languageName.includes(searchKeyword.value)
      );
    }
    courses.value = filtered;
    total.value = filtered.length;
  }
};

const editCourse = (id) => {
  router.push(`/courses/edit/${id}`);
};

const toggleStatus = async (row) => {
  try {
    await adminCourseApi.updateStatus(row.id, row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE');
    ElMessage.success('状态更新成功');
    loadCourses();
  } catch (error) {
    console.error('更新状态失败', error);
    // 模拟更新
    row.status = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    ElMessage.success('状态更新成功（模拟）');
  }
};

const deleteCourse = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该课程吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    try {
      await adminCourseApi.delete(id);
      ElMessage.success('删除成功');
      loadCourses();
    } catch (error) {
      console.error('删除课程失败', error);
      // 模拟删除
      courses.value = courses.value.filter(c => c.id !== id);
      ElMessage.success('删除成功（模拟）');
    }
  } catch {
    // 用户取消
  }
};

const handlePageChange = (page) => {
  currentPage.value = page;
  loadCourses();
};

onMounted(() => {
  loadCourses();
});
</script>

<style scoped>
.course-management {
  max-width: 1200px;
  margin: 0 auto;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  gap: 10px;
}

.search-box .el-input {
  width: 250px;
}
</style>