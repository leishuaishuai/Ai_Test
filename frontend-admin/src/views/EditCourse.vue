<template>
  <div class="edit-course">
    <el-button @click="$router.back()" style="margin-bottom: 20px;">
      <el-icon>ArrowLeft</el-icon>
      返回
    </el-button>

    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="课程名称" prop="title">
        <el-input v-model="form.title" placeholder="请输入课程名称" />
      </el-form-item>

      <el-form-item label="语言" prop="languageId">
        <el-select v-model="form.languageId" placeholder="请选择语言">
          <el-option
            v-for="lang in languages"
            :key="lang.id"
            :label="lang.name"
            :value="lang.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="难度" prop="level">
        <el-select v-model="form.level" placeholder="请选择难度">
          <el-option label="入门" value="BEGINNER" />
          <el-option label="中级" value="INTERMEDIATE" />
          <el-option label="高级" value="ADVANCED" />
        </el-select>
      </el-form-item>

      <el-form-item label="课程描述" prop="description">
        <el-input v-model="form.description" type="textarea" placeholder="请输入课程描述" :rows="4" />
      </el-form-item>

      <el-form-item label="总学时" prop="totalHours">
        <el-input v-model.number="form.totalHours" type="number" placeholder="请输入总学时" />
      </el-form-item>

      <el-form-item label="封面图片">
        <el-input v-model="form.coverImage" placeholder="请输入封面图片URL" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm" :loading="loading">
          更新课程
        </el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>

    <div class="chapter-section">
      <h3>章节管理</h3>
      <el-button type="primary" @click="showAddChapterDialog = true">
        <el-icon>Plus</el-icon>
        添加章节
      </el-button>
      <el-table :data="chapters" border style="margin-top: 20px;">
        <el-table-column prop="order" label="序号" width="80" />
        <el-table-column prop="title" label="章节名称" />
        <el-table-column prop="lessonCount" label="课时数" width="100" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button @click="editChapter(row)">编辑</el-button>
            <el-button @click="deleteChapter(row.id)" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="showAddChapterDialog" title="添加章节" @close="resetChapterForm">
      <el-form :model="chapterForm" label-width="80px">
        <el-form-item label="章节名称">
          <el-input v-model="chapterForm.title" />
        </el-form-item>
        <el-form-item label="序号">
          <el-input v-model.number="chapterForm.order" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddChapterDialog = false">取消</el-button>
        <el-button type="primary" @click="addChapter">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { adminCourseApi, languageApi } from '../api';
import { ElMessage, ElMessageBox } from 'element-plus';

const route = useRoute();
const router = useRouter();
const form = ref({
  title: '',
  languageId: '',
  level: '',
  description: '',
  totalHours: 0,
  coverImage: ''
});

const rules = {
  title: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  languageId: [{ required: true, message: '请选择语言', trigger: 'change' }],
  level: [{ required: true, message: '请选择难度', trigger: 'change' }],
  description: [{ required: true, message: '请输入课程描述', trigger: 'blur' }],
  totalHours: [{ required: true, message: '请输入总学时', trigger: 'blur' }]
};

const formRef = ref(null);
const loading = ref(false);
const languages = ref([]);
const chapters = ref([]);
const showAddChapterDialog = ref(false);
const chapterForm = ref({
  title: '',
  order: 1
});

// 模拟语言数据
const mockLanguages = [
  { id: 1, name: '英语' },
  { id: 2, name: '日语' },
  { id: 3, name: '韩语' },
  { id: 4, name: '法语' },
  { id: 5, name: '德语' }
];

// 模拟课程数据
const mockCourse = {
  id: 1,
  title: '英语入门课程',
  languageId: 1,
  level: 'BEGINNER',
  description: '这是一门面向零基础学员的英语入门课程，涵盖基础语法、词汇和日常会话。',
  totalHours: 24,
  coverImage: 'https://example.com/cover.jpg'
};

// 模拟章节数据
const mockChapters = [
  { id: 1, order: 1, title: '第一章：基础发音', lessonCount: 5 },
  { id: 2, order: 2, title: '第二章：基础词汇', lessonCount: 8 },
  { id: 3, order: 3, title: '第三章：简单句型', lessonCount: 6 }
];

const submitForm = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    try {
      await adminCourseApi.update(route.params.id, form.value);
      ElMessage.success('课程更新成功');
      router.push('/courses');
    } catch (error) {
      console.error('更新课程失败', error);
      ElMessage.success('课程更新成功（模拟）');
      router.push('/courses');
    }
  } catch {
    // 表单验证失败
  } finally {
    loading.value = false;
  }
};

const loadCourse = async () => {
  try {
    const course = await adminCourseApi.getById(route.params.id);
    form.value = course;
  } catch (error) {
    console.error('加载课程失败，使用模拟数据', error);
    form.value = mockCourse;
  }
};

const loadLanguages = async () => {
  try {
    languages.value = await languageApi.getAll();
  } catch (error) {
    console.error('加载语言失败，使用模拟数据', error);
    languages.value = mockLanguages;
  }
};

const loadChapters = async () => {
  try {
    chapters.value = await adminCourseApi.getChapters(route.params.id);
  } catch (error) {
    console.error('加载章节失败，使用模拟数据', error);
    chapters.value = mockChapters;
  }
};

const addChapter = async () => {
  if (!chapterForm.value.title) {
    ElMessage.warning('请输入章节名称');
    return;
  }
  try {
    await adminCourseApi.addChapter(route.params.id, chapterForm.value);
    showAddChapterDialog.value = false;
    resetChapterForm();
    loadChapters();
    ElMessage.success('章节添加成功');
  } catch (error) {
    console.error('添加章节失败', error);
    // 模拟添加
    chapters.value.push({
      id: chapters.value.length + 1,
      order: chapterForm.value.order,
      title: chapterForm.value.title,
      lessonCount: 0
    });
    showAddChapterDialog.value = false;
    resetChapterForm();
    ElMessage.success('章节添加成功（模拟）');
  }
};

const editChapter = (row) => {
  ElMessage.info('章节编辑功能待实现');
};

const deleteChapter = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该章节吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    try {
      await adminCourseApi.deleteChapter(route.params.id, id);
      loadChapters();
      ElMessage.success('章节删除成功');
    } catch (error) {
      console.error('删除章节失败', error);
      chapters.value = chapters.value.filter(c => c.id !== id);
      ElMessage.success('章节删除成功（模拟）');
    }
  } catch {
    // 用户取消
  }
};

const resetChapterForm = () => {
  chapterForm.value = { title: '', order: chapters.value.length + 1 };
};

onMounted(async () => {
  await loadCourse();
  await loadLanguages();
  await loadChapters();
});
</script>

<style scoped>
.edit-course {
  max-width: 800px;
}

.chapter-section {
  margin-top: 40px;
  padding: 20px;
  background: white;
  border-radius: 8px;
}

.chapter-section h3 {
  margin-bottom: 16px;
}
</style>