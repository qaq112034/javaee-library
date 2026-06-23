<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
            <el-icon :size="48" :color="card.color"><component :is="card.icon" /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>热门图书 Top 10</template>
          <el-table :data="hotBooks" stripe size="small" max-height="360">
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="title" label="书名" show-overflow-tooltip />
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column prop="borrowCount" label="借阅次数" width="100" sortable />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>逾期未还提醒</template>
          <el-table :data="overdueRecords" stripe size="small" max-height="360">
            <el-table-column prop="bookTitle" label="书名" show-overflow-tooltip />
            <el-table-column prop="readerName" label="借阅人" width="100" />
            <el-table-column prop="dueDate" label="应还日期" width="120">
              <template #default="{ row }">{{ formatDate(row.dueDate) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getDashboard } from '@/api/statistics'
import { getHotBooks } from '@/api/book'
import { getOverdueRecords } from '@/api/borrow'

const statCards = reactive([
  { label: '馆藏图书', value: 0, icon: 'Notebook', color: '#409EFF' },
  { label: '注册读者', value: 0, icon: 'User', color: '#67C23A' },
  { label: '借阅中', value: 0, icon: 'Tickets', color: '#E6A23C' },
  { label: '逾期未还', value: 0, icon: 'Warning', color: '#F56C6C' }
])

const hotBooks = ref([])
const overdueRecords = ref([])

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
}

onMounted(async () => {
  try {
    const dashboard = (await getDashboard()).data
    statCards[0].value = dashboard.totalBooks
    statCards[1].value = dashboard.totalReaders
    statCards[2].value = dashboard.borrowingCount
    statCards[3].value = dashboard.overdueCount
  } catch { /* ignore */ }

  try {
    hotBooks.value = (await getHotBooks(10)).data || []
  } catch { /* ignore */ }

  try {
    overdueRecords.value = (await getOverdueRecords()).data || []
  } catch { /* ignore */ }
})
</script>

<style scoped>
.stat-card { border-radius: 12px; border: 1px solid #eef1f5; }
.stat-card :deep(.el-card__body) { padding: 24px; }
.stat-content { display: flex; justify-content: space-between; align-items: center; }
.stat-value { font-size: 32px; font-weight: 700; color: #1d2b3a; line-height: 1; }
.stat-label { font-size: 14px; color: #909399; margin-top: 8px; }

:deep(.el-card__header) {
  font-weight: 600; color: #2c3e50;
  border-bottom: 1px solid #f0f3f8;
}
</style>
