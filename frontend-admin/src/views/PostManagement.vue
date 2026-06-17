<template>
  <div class="post-management">
    <div class="toolbar">
      <div class="search-box">
        <el-input v-model="searchKeyword" placeholder="搜索帖子内容" @keyup.enter="loadPosts" />
        <el-button @click="loadPosts">搜索</el-button>
      </div>
    </div>

    <el-table :data="posts" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.content.substring(0, 50) }}{{ row.content.length > 50 ? '...' : '' }}
        </template>
      </el-table-column>
      <el-table-column prop="authorName" label="作者" width="100" />
      <el-table-column prop="likeCount" label="点赞数" width="100" />
      <el-table-column prop="commentCount" label="评论数" width="100" />
      <el-table-column prop="createdAt" label="发布时间" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button @click="deletePost(row.id)" type="danger">删除</el-button>
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

<script setup>
import { ref, onMounted } from 'vue';
import { postApi } from '../api';

const posts = ref([]);
const searchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const loadPosts = async () => {
  try {
    const result = await postApi.getAll({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    });
    posts.value = result.records || [];
    total.value = result.total || 0;
  } catch (error) {
    console.error('加载帖子失败', error);
  }
};

const deletePost = async (id) => {
  if (!confirm('确定要删除该帖子吗？')) return;
  try {
    await postApi.delete(id);
    loadPosts();
  } catch (error) {
    console.error('删除帖子失败', error);
  }
};

const handlePageChange = (page) => {
  currentPage.value = page;
  loadPosts();
};

onMounted(() => {
  loadPosts();
});
</script>

<style scoped>
.post-management {
  max-width: 1200px;
  margin: 0 auto;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
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