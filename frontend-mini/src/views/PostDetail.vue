<template>
  <div class="post-detail-container">
    <div class="post-header" v-if="post">
      <div class="back-btn" @click="$router.back()">
        <el-icon>ArrowLeft</el-icon>
        返回
      </div>
      <div class="author-info">
        <div class="author-avatar">
          <el-icon size="32">User</el-icon>
        </div>
        <div class="author-detail">
          <span class="author-name">{{ post.authorName }}</span>
          <span class="post-time">{{ post.createdAt }}</span>
        </div>
      </div>
      <span :class="['post-tag', post.tag]">{{ post.tag }}</span>
    </div>

    <div class="post-content" v-if="post">
      <h1>{{ post.title }}</h1>
      <p>{{ post.content }}</p>
      <div class="post-image" v-if="post.image">
        <img :src="post.image" alt="帖子图片">
      </div>
      <div class="post-stats">
        <div class="stat-item">
          <el-icon>Eye</el-icon>
          <span>{{ post.viewCount }}</span>
        </div>
        <div class="stat-item" @click="toggleLike">
          <el-icon :class="{ liked: post.isLiked }">Star</el-icon>
          <span>{{ post.likeCount }}</span>
        </div>
        <div class="stat-item">
          <el-icon>ChatDotRound</el-icon>
          <span>{{ post.commentCount }}</span>
        </div>
      </div>
    </div>

    <div class="comments-section">
      <h2>评论 ({{ comments.length }})</h2>
      <div class="comments-list">
        <div
          v-for="comment in comments"
          :key="comment.id"
          class="comment-item"
        >
          <div class="comment-avatar">
            <el-icon size="24">User</el-icon>
          </div>
          <div class="comment-content">
            <div class="comment-header">
              <span class="comment-author">{{ comment.authorName }}</span>
              <span class="comment-time">{{ comment.createdAt }}</span>
            </div>
            <p>{{ comment.content }}</p>
            <div class="comment-actions">
              <span class="like-btn" @click="likeComment(comment)">
                <el-icon>ThumbUp</el-icon>
                {{ comment.likeCount }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="comments.length === 0" class="no-comments">
        <p>暂无评论，快来发表第一条吧！</p>
      </div>

      <div class="comment-input">
        <textarea
          v-model="commentText"
          placeholder="写下你的评论..."
          rows="3"
        ></textarea>
        <button @click="submitComment" :disabled="!commentText.trim()">
          发表评论
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { postApi, commentApi } from '../api';
import { ElMessage } from 'element-plus';
const route = useRoute();
const post = ref(null);
const comments = ref([]);
const commentText = ref('');
const loading = ref(false);
const toggleLike = async () => {
 if (!post.value)
 return;
 try {
 if (post.value.isLiked) {
 await postApi.unlike(post.value.id);
 post.value.likeCount--;
 }
 else {
 await postApi.like(post.value.id);
 post.value.likeCount++;
 }
 post.value.isLiked = !post.value.isLiked;
 ElMessage.success(post.value.isLiked ? '点赞成功' : '取消点赞');
 }
 catch (error) {
 console.error('点赞失败', error);
 ElMessage.error('操作失败');
 }
};
const likeComment = async (comment) => {
 ElMessage.info('评论点赞功能开发中');
};
const submitComment = async () => {
 if (!commentText.value.trim())
 return;
 try {
 const res = await commentApi.create({
 postId: route.params.id,
 content: commentText.value
 });
 if (res.code === 200) {
 commentText.value = '';
 ElMessage.success('评论成功');
 loadComments();
 }
 }
 catch (error) {
 console.error('发表评论失败', error);
 ElMessage.error('发表评论失败');
 }
};
const loadPost = async () => {
 loading.value = true;
 try {
 const res = await postApi.getById(route.params.id);
 if (res.code === 200) {
 post.value = res.data;
 }
 }
 catch (error) {
 console.error('加载帖子失败', error);
 ElMessage.error('加载帖子失败');
 }
 finally {
 loading.value = false;
 }
};
const loadComments = async () => {
 try {
 const res = await commentApi.getByPost(route.params.id);
 if (res.code === 200) {
 comments.value = res.data || [];
 }
 }
 catch (error) {
 console.error('加载评论失败', error);
 }
};
onMounted(async () => {
 await loadPost();
 await loadComments();
});
</script>

<style scoped>
.post-detail-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #409EFF;
  cursor: pointer;
  font-size: 14px;
}

.author-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 48px;
  height: 48px;
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
  font-size: 16px;
  color: #333;
  font-weight: bold;
}

.post-time {
  font-size: 13px;
  color: #999;
}

.post-tag {
  padding: 6px 14px;
  border-radius: 4px;
  font-size: 13px;
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

.post-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.post-content h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 16px;
}

.post-content p {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 16px;
}

.post-image {
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
}

.post-image img {
  width: 100%;
  height: 250px;
  object-fit: cover;
}

.post-stats {
  display: flex;
  gap: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
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

.comments-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.comments-section h2 {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 24px;
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  background: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-author {
  font-size: 14px;
  color: #333;
  font-weight: bold;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content p {
  font-size: 15px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.like-btn {
  font-size: 13px;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.like-btn:hover {
  color: #409EFF;
}

.no-comments {
  text-align: center;
  padding: 30px;
  color: #999;
}

.comment-input {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-input textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
}

.comment-input button {
  align-self: flex-end;
  padding: 10px 24px;
  background: #409EFF;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.comment-input button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>