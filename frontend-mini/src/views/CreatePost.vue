<template>
  <div class="create-post-container">
    <div class="header">
      <h1>发布帖子</h1>
      <p>分享你的学习心得或问题</p>
    </div>

    <div class="form-container">
      <div class="form-group">
        <label>帖子标签</label>
        <select v-model="form.tag">
          <option value="学习心得">学习心得</option>
          <option value="问题求助">问题求助</option>
          <option value="资源分享">资源分享</option>
          <option value="话题讨论">话题讨论</option>
        </select>
      </div>

      <div class="form-group">
        <label>帖子标题</label>
        <input
          v-model="form.title"
          type="text"
          placeholder="请输入帖子标题"
        />
      </div>

      <div class="form-group">
        <label>帖子内容</label>
        <textarea
          v-model="form.content"
          placeholder="请输入帖子内容..."
          rows="8"
        ></textarea>
      </div>

      <div class="form-group">
        <label>添加图片</label>
        <div class="upload-area">
          <input type="file" accept="image/*" @change="handleImageUpload" />
          <div class="upload-placeholder">
            <el-icon size="48">Upload</el-icon>
            <p>点击上传图片</p>
          </div>
          <div v-if="form.image" class="image-preview">
            <img :src="form.image" alt="预览">
            <button class="remove-btn" @click="removeImage">
              <el-icon>Close</el-icon>
            </button>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <button class="cancel-btn" @click="$router.back()">
          取消
        </button>
        <button class="submit-btn" @click="submitPost" :disabled="!isValid">
          发布帖子
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { postApi } from '../api';
const router = useRouter();
const form = ref({
 tag: '学习心得',
 title: '',
 content: '',
 image: ''
});
const isValid = computed(() => {
 return form.value.title.trim() && form.value.content.trim();
});
const handleImageUpload = (event) => {
 const file = event.target.files[0];
 if (file) {
 const reader = new FileReader();
 reader.onload = (e) => {
 form.value.image = e.target.result;
 };
 reader.readAsDataURL(file);
 }
};
const removeImage = () => {
 form.value.image = '';
};
const submitPost = async () => {
 if (!isValid.value)
 return;
 try {
 await postApi.create(form.value);
 router.push('/community');
 }
 catch (error) {
 console.error('发布帖子失败', error);
 }
};
</script>

<style scoped>
.create-post-container {
  padding: 20px;
  max-width: 600px;
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

.form-container {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group textarea {
  resize: vertical;
}

.upload-area {
  position: relative;
  height: 150px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
}

.upload-area input {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.upload-placeholder {
  text-align: center;
  color: #999;
}

.upload-placeholder p {
  margin-top: 8px;
}

.image-preview {
  position: relative;
  width: 100%;
  height: 100%;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 32px;
  height: 32px;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
}

.form-actions {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}

.cancel-btn {
  flex: 1;
  padding: 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: white;
  color: #666;
  cursor: pointer;
  font-size: 16px;
}

.submit-btn {
  flex: 2;
  padding: 14px;
  border: none;
  border-radius: 8px;
  background: #409EFF;
  color: white;
  cursor: pointer;
  font-size: 16px;
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>