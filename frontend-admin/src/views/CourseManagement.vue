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

<script setup>import { ref, onMounted } from 'vue';
import { adminCourseApi } from '../api';
import { useRouter } from 'vue-router';
const router = useRouter();
const courses = ref([]);
const searchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
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
 }
 catch (error) {
 console.error('加载课程失败', error);
 }
};
const editCourse = (id) => {
 router.push(`/courses/edit/${id}`);
};
const toggleStatus = async (row) => {
 try {
 await adminCourseApi.updateStatus(row.id, row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE');
 loadCourses();
 }
 catch (error) {
 console.error('更新状态失败', error);
 }
};
const deleteCourse = async (id) => {
 if (!confirm('确定要删除该课程吗？'))
 return;
 try {
 await adminCourseApi.delete(id);
 loadCourses();
 }
 catch (error) {
 console.error('删除课程失败', error);
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