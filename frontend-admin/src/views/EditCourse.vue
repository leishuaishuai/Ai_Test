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
        <el-textarea v-model="form.description" placeholder="请输入课程描述" :rows="4" />
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

<script setup>import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { adminCourseApi, languageApi } from '../api';
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
const submitForm = async () => {
 if (!formRef.value)
 return;
 await formRef.value.validate(async (valid) => {
 if (!valid)
 return;
 loading.value = true;
 try {
 await adminCourseApi.update(route.params.id, form.value);
 router.push('/courses');
 }
 catch (error) {
 console.error('更新课程失败', error);
 }
 finally {
 loading.value = false;
 }
 });
};
const loadCourse = async () => {
 try {
 const course = await adminCourseApi.getById(route.params.id);
 form.value = course;
 }
 catch (error) {
 console.error('加载课程失败', error);
 }
};
const loadLanguages = async () => {
 try {
 languages.value = await languageApi.getAll();
 }
 catch (error) {
 console.error('加载语言失败', error);
 }
};
const loadChapters = async () => {
 try {
 chapters.value = await adminCourseApi.getChapters(route.params.id);
 }
 catch (error) {
 console.error('加载章节失败', error);
 }
};
const addChapter = async () => {
 try {
 await adminCourseApi.addChapter(route.params.id, chapterForm.value);
 showAddChapterDialog.value = false;
 resetChapterForm();
 loadChapters();
 }
 catch (error) {
 console.error('添加章节失败', error);
 }
};
const editChapter = (row) => {
};
const deleteChapter = async (id) => {
 if (!confirm('确定要删除该章节吗？'))
 return;
 try {
 await adminCourseApi.deleteChapter(route.params.id, id);
 loadChapters();
 }
 catch (error) {
 console.error('删除章节失败', error);
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