<template>
  <div class="borrow-list">
    <el-card>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索读者/书名" clearable @keyup.enter="fetchData"
          style="width: 240px; margin-right: 8px" />
        <el-select v-model="filterStatus" placeholder="全部状态" clearable style="width: 140px; margin-right: 8px"
          @change="fetchData">
          <el-option label="借阅中" :value="1" />
          <el-option label="已归还" :value="2" />
          <el-option label="已逾期" :value="3" />
        </el-select>
        <el-button type="primary" @click="fetchData">搜索</el-button>
        <el-button type="success" icon="Plus" @click="openBorrowDialog">借书</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="记录ID" width="80" />
        <el-table-column prop="readerName" label="读者" width="100" />
        <el-table-column prop="bookTitle" label="书名" min-width="180" show-overflow-tooltip />
        <el-table-column label="借阅日期" width="160">
          <template #default="{ row }">{{ fmt(row.borrowDate) }}</template>
        </el-table-column>
        <el-table-column label="应还日期" width="160">
          <template #default="{ row }">{{ fmt(row.dueDate) }}</template>
        </el-table-column>
        <el-table-column label="归还日期" width="160">
          <template #default="{ row }">{{ fmt(row.returnDate) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'warning' : row.status === 2 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '借阅中' : row.status === 2 ? '已归还' : '已逾期' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="renewCount" label="续借" width="60" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="success" link size="small"
              @click="handleReturn(row.id)">还书</el-button>
            <el-button v-if="row.status === 1" type="warning" link size="small"
              @click="handleRenew(row.id)">续借</el-button>
            <span v-if="row.status === 2" style="color:#909399">已完成</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page.current" v-model:page-size="page.size"
        :total="page.total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
        @change="fetchData" style="margin-top:20px;justify-content:flex-end" />
    </el-card>

    <!-- 借书弹窗 -->
    <el-dialog v-model="borrowDialogVisible" title="借书" width="480px">
      <el-form label-width="80px">
        <el-form-item label="读者">
          <el-select v-model="borrowForm.readerId" filterable placeholder="搜索读者" style="width:100%">
            <el-option v-for="r in readers" :key="r.id" :label="`${r.realName} (${r.readerNo})`" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="图书">
          <el-select v-model="borrowForm.bookId" filterable placeholder="搜索图书" style="width:100%">
            <el-option v-for="b in books" :key="b.id"
              :label="`${b.title} - ${b.author} (可借:${b.availableCopies})`" :value="b.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="borrowing" @click="handleBorrow">确认借书</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getBorrowList, borrowBook, returnBook, renewBook } from '@/api/borrow'
import request from '@/api/request'

const keyword = ref(''); const filterStatus = ref(null)
const loading = ref(false); const tableData = ref([])
const page = reactive({ current: 1, size: 10, total: 0 })

// 借书弹窗
const borrowDialogVisible = ref(false); const borrowing = ref(false)
const borrowForm = reactive({ readerId: null, bookId: null })
const readers = ref([]); const books = ref([])

function fmt(d) { return d ? d.replace('T', ' ').substring(0, 16) : '-' }

async function fetchData() {
  loading.value = true
  try {
    const res = await getBorrowList({ keyword: keyword.value, status: filterStatus.value, current: page.current, size: page.size })
    tableData.value = res.data.records; page.total = res.data.total
  } catch { /* ignore */ }
  loading.value = false
}

async function openBorrowDialog() {
  borrowForm.readerId = null; borrowForm.bookId = null
  try {
    const [rRes, bRes] = await Promise.all([
      request.get('/reader/list', { params: { size: 999 } }),
      request.get('/book/list', { params: { status: 1, size: 999 } })
    ])
    readers.value = rRes.data.records || []
    books.value = bRes.data.records || []
  } catch { /* ignore */ }
  borrowDialogVisible.value = true
}

async function handleBorrow() {
  if (!borrowForm.readerId || !borrowForm.bookId) {
    ElMessage.warning('请选择读者和图书'); return
  }
  borrowing.value = true
  try {
    await borrowBook(borrowForm)
    ElMessage.success('借书成功')
    borrowDialogVisible.value = false
    fetchData()
  } catch { /* ignore */ }
  borrowing.value = false
}

async function handleReturn(id) {
  await returnBook(id)
  ElMessage.success('还书成功')
  fetchData()
}

async function handleRenew(id) {
  await renewBook(id)
  ElMessage.success('续借成功')
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; display: flex; gap: 8px; }
</style>
