<template>
  <div class="login-container">
    <div class="login-box">
      <div class="logo-section">
        <el-icon size="48" color="#409EFF">BookOpen</el-icon>
        <h1>语言学习平台</h1>
        <p>管理后台</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { authApi } from '../api';
const router = useRouter();
const store = useStore();
const form = ref({
 username: '',
 password: ''
});
const rules = {
 username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
 password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};
const formRef = ref(null);
const loading = ref(false);
const handleLogin = async () => {
 if (!formRef.value)
 return;
 await formRef.value.validate(async (valid) => {
 if (!valid)
 return;
 loading.value = true;
 try {
 const result = await authApi.login(form.value);
 store.dispatch('login', { token: result.token, user: result.user });
 router.push('/dashboard');
 }
 catch (error) {
 console.error('登录失败', error);
 }
 finally {
 loading.value = false;
 }
 });
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.logo-section {
  text-align: center;
  margin-bottom: 30px;
}

.logo-section h1 {
  font-size: 24px;
  color: #333;
  margin: 16px 0 8px;
}

.logo-section p {
  color: #999;
  font-size: 14px;
}
</style>