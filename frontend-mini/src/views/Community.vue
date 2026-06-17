<template>
  <div class="community-container">
    <div class="header">
      <h1>学习社区</h1>
      <p>与志同道合的学习者交流分享</p>
    </div>

    <div class="filter-bar">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['filter-btn', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
      </button>
    </div>

    <div class="posts-list">
      <div
        v-for="post in posts"
        :key="post.id"
        class="post-card"
        @click="$router.push(`/post/${post.id}`)"
      >
        <div class="post-header">
          <div class="author-info">
            <div class="author-avatar">
              <el-icon size="24">User</el-icon>
            </div>
            <div class="author-detail">
              <span class="author-name">{{ post.authorName }}</span>
              <span class="post-time">{{ post.createdAt }}</span>
            </div>
          </div>
          <span :class="['post-tag', post.tag]">{{ post.tag }}</span>
        </div>
        <h3 class="post-title">{{ post.title }}</h3>
        <p class="post-content">{{ post.content }}</p>
        <div class="post-image" v-if="post.image">
          <img :src="post.image" alt="帖子图片">
        </div>
        <div class="post-stats">
          <div class="stat-item">
            <el-icon>ChatDotRound</el-icon>
            <span>{{ post.commentCount }}</span>
          </div>
          <div class="stat-item" @click.stop="toggleLike(post)">
            <el-icon :class="{ liked: post.liked }">Star</el-icon>
            <span>{{ post.likeCount }}</span>
          </div>
          <div class="stat-item">
            <el-icon>Eye</el-icon>
            <span>{{ post.viewCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="posts.length === 0" class="empty-state">
      <el-icon size="48">Message</el-icon>
      <p>暂无帖子，快来发布第一个吧！</p>
    </div>

    <button class="fab-button" @click="$router.push('/create-post')">
      <el-icon>Plus</el-icon>
    </button>
  </div>
</template>

<script setup>import { ref, onMounted } from 'vue';
import { postApi } from '../api';
const activeTab = ref('all');
const tabs = [
 { key: 'all', label: '全部' },
 { key: 'study', label: '学习心得' },
 { key: 'question', label: '问题求助' },
 { key: 'resource', label: '资源分享' },
 { key: 'discussion', label: '话题讨论' }
];
const posts = ref([
 {
 id: 1,
 title: '分享我的英语学习心得',
 content: '经过三个月的坚持学习，我的英语水平有了很大提升。想和大家分享一下我的学习方法...',
 authorName: '学霸小明',
 createdAt: '2小时前',
 tag: '学习心得',
 commentCount: 12,
 likeCount: 45,
 viewCount: 234,
 liked: false
 },
 {
 id: 2,
 title: '请问日语的敬语怎么区分？',
 content: '刚学日语，对敬语的使用很困惑，什么时候用ます形，什么时候用ですか？求大神解答！',
 authorName: '日语初学者',
 createdAt: '5小时前',
 tag: '问题求助',
 commentCount: 8,
 likeCount: 15,
 viewCount: 156,
 liked: true
 },
 {
 id: 3,
 title: '推荐几个好用的韩语学习网站',
 content: '整理了一些我常用的韩语学习资源网站，有免费的也有付费的，希望对大家有帮助...',
 authorName: '韩语爱好者',
 createdAt: '昨天',
 tag: '资源分享',
 commentCount: 23,
 likeCount: 67,
 viewCount: 512,
 liked: false
 },
 {
 id: 4,
 title: '大家觉得哪个语言最难学？',
 content: '想做个调查，大家觉得英语、日语、韩语哪个最难学？为什么？',
 authorName: '语言学习者',
 createdAt: '2天前',
 tag: '话题讨论',
 commentCount: 45,
 likeCount: 89,
 viewCount: 890,
 liked: false
 }
]);
const toggleLike = async (post) => {
 try {
 if (post.liked) {
 await postApi.unlike(post.id);
 post.likeCount--;
 }
 else {
 await postApi.like(post.id);
 post.likeCount++;
 }
 post.liked = !post.liked;
 }
 catch (error) {
 console.error('点赞失败', error);
 }
};
const loadPosts = async () => {
 try {
 const data = await postApi.getAll({ tag: activeTab.value === 'all' ? '' : activeTab.value });
 posts.value = data;
 }
 catch (error) {
 console.error('加载帖子失败', error);
 }
};
onMounted(() => {
 loadPosts();
});
</script>

<style scoped>
.community-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.header p {
  color: #666;
}

.filter-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  overflow-x: auto;
}

.filter-btn {
  padding: 10px 20px;
  border: 1px solid #ddd;
  border-radius: 20px;
  background: white;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
  transition: all 0.3s;
}

.filter-btn.active {
  background: #409EFF;
  color: white;
  border-color: #409EFF;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.2s;
}

.post-card:hover {
  transform: translateY(-2px);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 40px;
  height: 40px;
  background: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.author-detail {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-size: 14px;
  color: #333;
  font-weight: bold;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-tag {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.post-tag.学习心得 {
  background: #f0f7ff;
  color: #409EFF;
}

.post-tag.问题求助 {
  background: #fef0f0;
  color: #F56C6C;
}

.post-tag.资源分享 {
  background: #f0fdf4;
  color: #67C23A;
}

.post-tag.话题讨论 {
  background: #fdf6ec;
  color: #E6A23C;
}

.post-title {
  font-size: 18px;
  color: #333;
  margin-bottom: 12px;
}

.post-content {
  font-size: 15px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-image {
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
}

.post-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.post-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #999;
  cursor: pointer;
}

.stat-item:hover {
  color: #409EFF;
}

.stat-item .liked {
  color: #E6A23C;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.empty-state p {
  margin-top: 12px;
}

.fab-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 60px;
  height: 60px;
  background: #409EFF;
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 24px;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transition: transform 0.2s;
}

.fab-button:hover {
  transform: scale(1.1);
}
</style>