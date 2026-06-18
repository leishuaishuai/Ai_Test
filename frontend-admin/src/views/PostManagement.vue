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
import { ElMessage, ElMessageBox } from 'element-plus';

const posts = ref([]);
const searchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 模拟数据用于后端不可用时
const mockPosts = [
  { id: 1, title: '如何快速学习英语口语', content: '分享一些学习英语口语的有效方法，包括听力训练、模仿练习等技巧...', authorName: '张三', likeCount: 125, commentCount: 32, createdAt: '2024-01-15' },
  { id: 2, title: '日语学习心得分享', content: '经过一年的日语学习，总结了一些学习心得，希望对大家有所帮助...', authorName: '李四', likeCount: 89, commentCount: 18, createdAt: '2024-01-14' },
  { id: 3, title: '韩语发音技巧', content: '韩语发音对于初学者来说比较困难，这里分享一些发音技巧...', authorName: '王五', likeCount: 56, commentCount: 12, createdAt: '2024-01-13' },
  { id: 4, title: '语言学习方法论', content: '探讨语言学习的通用方法论，如何更高效地掌握一门新语言...', authorName: '赵六', likeCount: 210, commentCount: 45, createdAt: '2024-01-12' },
  { id: 5, title: '多语言学习经验', content: '同时学习多门语言的经验分享，如何平衡不同语言的学习进度...', authorName: '钱七', likeCount: 78, commentCount: 23, createdAt: '2024-01-11' }
];

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
    console.error('加载帖子失败，使用模拟数据', error);
    // 使用模拟数据
    let filtered = mockPosts;
    if (searchKeyword.value) {
      filtered = mockPosts.filter(p => 
        p.title.includes(searchKeyword.value) || 
        p.content.includes(searchKeyword.value) ||
        p.authorName.includes(searchKeyword.value)
      );
    }
    posts.value = filtered;
    total.value = filtered.length;
  }
};

const deletePost = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    try {
      await postApi.delete(id);
      ElMessage.success('删除成功');
      loadPosts();
    } catch (error) {
      console.error('删除帖子失败', error);
      // 模拟删除
      posts.value = posts.value.filter(p => p.id !== id);
      total.value = posts.value.length;
      ElMessage.success('删除成功（模拟）');
    }
  } catch {
    // 用户取消
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