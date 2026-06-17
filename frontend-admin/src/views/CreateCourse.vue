<template>
  <div class="create-course">
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
          创建课程
        </el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { adminCourseApi, languageApi } from '../api';
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
const submitForm = async () => {
 if (!formRef.value)
 return;
 await formRef.value.validate(async (valid) => {
 if (!valid)
 return;
 loading.value = true;
 try {
 await adminCourseApi.create(form.value);
 router.push('/courses');
 }
 catch (error) {
 console.error('创建课程失败', error);
 }
 finally {
 loading.value = false;
 }
 });
};
const loadLanguages = async () => {
 try {
 languages.value = await languageApi.getAll();
 }
 catch (error) {
 console.error('加载语言失败', error);
 }
};
onMounted(() => {
 loadLanguages();
});
</script>

<style scoped>
.create-course {
  max-width: 600px;
}
</style>