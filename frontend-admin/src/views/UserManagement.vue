<template>
  <div class="user-management">
    <div class="toolbar">
      <div class="search-box">
        <el-input v-model="searchKeyword" placeholder="搜索用户名或邮箱" @keyup.enter="loadUsers" />
        <el-select v-model="statusFilter" placeholder="状态筛选">
          <el-option label="全部" value="" />
          <el-option label="正常" value="ACTIVE" />
          <el-option label="禁用" value="INACTIVE" />
        </el-select>
        <el-button @click="loadUsers">搜索</el-button>
      </div>
    </div>

    <el-table :data="users" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="role" label="角色">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'">
            {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'warning'">
            {{ row.status === 'ACTIVE' ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalPoints" label="积分" width="100" />
      <el-table-column prop="createdAt" label="注册时间" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button @click="viewDetail(row.id)">详情</el-button>
          <el-button @click="toggleStatus(row)" :type="row.status === 'ACTIVE' ? 'warning' : 'success'">
            {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
          </el-button>
          <el-button @click="deleteUser(row.id)" type="danger">删除</el-button>
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
import { adminUserApi } from '../api';
import { useRouter } from 'vue-router';

const router = useRouter();
const users = ref([]);
const searchKeyword = ref('');
const statusFilter = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const loadUsers = async () => {
  try {
    const result = await adminUserApi.getAll({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value,
      status: statusFilter.value
    });
    users.value = result.records || [];
    total.value = result.total || 0;
  } catch (error) {
    console.error('加载用户失败', error);
  }
};

const viewDetail = (id) => {
  router.push(`/users/detail/${id}`);
};

const toggleStatus = async (row) => {
  try {
    await adminUserApi.updateStatus(row.id, row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE');
    loadUsers();
  } catch (error) {
    console.error('更新状态失败', error);
  }
};

const deleteUser = async (id) => {
  if (!confirm('确定要删除该用户吗？')) return;
  try {
    await adminUserApi.delete(id);
    loadUsers();
  } catch (error) {
    console.error('删除用户失败', error);
  }
};

const handlePageChange = (page) => {
  currentPage.value = page;
  loadUsers();
};

onMounted(() => {
  loadUsers();
});
</script>

<style scoped>
.user-management {
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
  align-items: center;
}

.search-box .el-input {
  width: 250px;
}

.search-box .el-select {
  width: 120px;
}
</style>