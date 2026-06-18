<template>
  <div class="bottom-nav">
    <div class="nav-item" :class="{ active: currentRoute === '/home' }" @click="navigate('/home')">
      <el-icon size="20">HomeFilled</el-icon>
      <span>首页</span>
    </div>
    <div class="nav-item" :class="{ active: currentRoute === '/courses' }" @click="navigate('/courses')">
      <el-icon size="20">Reading</el-icon>
      <span>课程</span>
    </div>
    <div class="nav-item" :class="{ active: currentRoute === '/words' }" @click="navigate('/words')">
      <el-icon size="20">EditPen</el-icon>
      <span>单词</span>
    </div>
    <div class="nav-item" :class="{ active: currentRoute === '/community' }" @click="navigate('/community')">
      <el-icon size="20">ChatDotRound</el-icon>
      <span>社区</span>
    </div>
    <div class="nav-item" :class="{ active: currentRoute === '/profile' }" @click="navigate('/profile')">
      <el-icon size="20">User</el-icon>
      <span>我的</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const currentRoute = computed(() => route.path)

const navigate = (path) => {
  router.push(path)
}
</script>

<style scoped>
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: #fff;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
}

.nav-item.active {
  color: #409EFF;
}

.nav-item span {
  font-size: 12px;
}
</style>