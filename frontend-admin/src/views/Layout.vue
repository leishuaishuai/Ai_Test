<template>
  <div class="layout-container">
    <aside class="sidebar">
      <div class="logo">
        <el-icon size="32" color="#409EFF">BookOpen</el-icon>
        <span>语言学习平台</span>
      </div>
      <el-menu :default-active="activeMenu" router>
        <el-menu-item index="/dashboard">
          <el-icon>Layout</el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/courses">
          <el-icon>Document</el-icon>
          <span>课程管理</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon>User</el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/achievements">
          <el-icon>Medal</el-icon>
          <span>成就管理</span>
        </el-menu-item>
        <el-menu-item index="/posts">
          <el-icon>Message</el-icon>
          <span>帖子管理</span>
        </el-menu-item>
      </el-menu>
    </aside>
    <main class="main-content">
      <header class="header">
        <div class="header-left">
          <el-icon class="menu-toggle" @click="toggleSidebar">Menu</el-icon>
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-button @click="handleLogout">退出登录</el-button>
        </div>
      </header>
      <div class="content-area">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>import { computed } from 'vue';
import { useRouter, useStore } from 'vue-router';
import { useRoute } from 'vue-router';
const router = useRouter();
const route = useRoute();
const store = useStore();
const activeMenu = computed(() => route.path);
const pageTitle = computed(() => {
 const titles = {
 '/dashboard': '数据概览',
 '/courses': '课程管理',
 '/courses/create': '创建课程',
 '/courses/edit': '编辑课程',
 '/users': '用户管理',
 '/users/detail': '用户详情',
 '/achievements': '成就管理',
 '/posts': '帖子管理'
 };
 return titles[route.path] || '管理后台';
});
const toggleSidebar = () => {
};
const handleLogout = () => {
 store.dispatch('logout');
 router.push('/login');
};
</script>

<style scoped>
.layout-container {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 240px;
  background: #001529;
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  height: 60px;
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.menu-toggle {
  font-size: 20px;
  cursor: pointer;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.content-area {
  flex: 1;
  padding: 20px;
  overflow: auto;
}
</style>