<template>
  <div class="achievement-management">
    <div class="toolbar">
      <el-button type="primary" @click="showCreateModal = true">
        <el-icon>Plus</el-icon>
        创建成就
      </el-button>
    </div>

    <el-table :data="achievements" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="成就名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="points" label="奖励积分" width="100" />
      <el-table-column prop="type" label="类型">
        <template #default="{ row }">
          <el-tag>{{ getTypeText(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="condition" label="条件" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button @click="editAchievement(row)">编辑</el-button>
          <el-button @click="deleteAchievement(row.id)" type="danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showCreateModal" :title="editingAchievement ? '编辑成就' : '创建成就'">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="成就名称" required>
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="描述" required>
          <el-input v-model="formData.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="奖励积分" required>
          <el-input-number v-model="formData.points" :min="1" />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-select v-model="formData.type">
            <el-option label="学习" value="LEARNING" />
            <el-option label="课程" value="COURSE" />
            <el-option label="签到" value="SIGNIN" />
            <el-option label="社区" value="COMMUNITY" />
          </el-select>
        </el-form-item>
        <el-form-item label="条件" required>
          <el-input v-model="formData.condition" placeholder="如：连续签到7天" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateModal = false">取消</el-button>
        <el-button type="primary" @click="saveAchievement">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { achievementApi } from '../api';

const achievements = ref([]);
const showCreateModal = ref(false);
const editingAchievement = ref(null);
const formData = ref({
  name: '',
  description: '',
  points: 10,
  type: 'LEARNING',
  condition: ''
});

const getTypeText = (type) => {
  const types = {
    'LEARNING': '学习',
    'COURSE': '课程',
    'SIGNIN': '签到',
    'COMMUNITY': '社区'
  };
  return types[type] || type;
};

const loadAchievements = async () => {
  try {
    achievements.value = await achievementApi.getAll();
  } catch (error) {
    console.error('加载成就失败', error);
  }
};

const editAchievement = (row) => {
  editingAchievement.value = row;
  formData.value = {
    name: row.name,
    description: row.description,
    points: row.points,
    type: row.type,
    condition: row.condition
  };
  showCreateModal.value = true;
};

const saveAchievement = async () => {
  try {
    if (editingAchievement.value) {
      await achievementApi.update(editingAchievement.value.id, formData.value);
    } else {
      await achievementApi.create(formData.value);
    }
    showCreateModal.value = false;
    editingAchievement.value = null;
    formData.value = {
      name: '',
      description: '',
      points: 10,
      type: 'LEARNING',
      condition: ''
    };
    loadAchievements();
  } catch (error) {
    console.error('保存成就失败', error);
  }
};

const deleteAchievement = async (id) => {
  if (!confirm('确定要删除该成就吗？')) return;
  try {
    await achievementApi.delete(id);
    loadAchievements();
  } catch (error) {
    console.error('删除成就失败', error);
  }
};

onMounted(() => {
  loadAchievements();
});
</script>

<style scoped>
.achievement-management {
  max-width: 1200px;
  margin: 0 auto;
}

.toolbar {
  margin-bottom: 20px;
}
</style>